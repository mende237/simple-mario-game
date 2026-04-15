import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

import grpc
from concurrent import futures
import time
import numpy as np
import threading

import data_pb2
import data_pb2_grpc
from dqn import DQN
from util.mario_state import MarioState
from util.config import STATE_SIZE, ACTION_SIZE, BATCH_SIZE, MODEL_DIR, MODEL_PATH, SAVE_FREQUENCY, CONTEXT_ANTAGONIST_WIDTH, CONTEXT_ITEM_WIDTH, CONTEXT_COIN_WIDTH, PORT, MAX_DISTANCE, Y_MAY

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

class GameServiceServicer(data_pb2_grpc.GameServiceServicer):
    def __init__(self):
        self.agent = DQN(state_size=STATE_SIZE, action_size=ACTION_SIZE)
        self.last_state = None
        self.last_action = None
        self.last_mario = None
        
        # Initialize replay_counter BEFORE starting the thread
        self.replay_counter = 0 

        # Start a background thread for training
        self.training_thread = threading.Thread(target=self._train_loop, daemon=True)
        self.training_thread.start()

        # Load model if it exists
        if not os.path.exists(MODEL_DIR):
            os.makedirs(MODEL_DIR)
        if os.path.exists(MODEL_PATH):
            print(f"Loading model from {MODEL_PATH}")
            self.agent.load(MODEL_PATH)
        else:
            print(f"No model found at {MODEL_PATH}, starting with a new model.")

    def _train_loop(self):
        """
        A loop that runs in a background thread to train the agent.
        """
        while True:
            self.agent.replay(BATCH_SIZE)
            self.replay_counter += 1
            if self.replay_counter % SAVE_FREQUENCY == 0:
                print(f"Saving model to {MODEL_PATH}...")
                self.agent.save(MODEL_PATH)
                print("Model saved.")
            # Sleep to prevent busy-waiting and yield the CPU
            time.sleep(0.01) # 10ms sleep

    def _get_state_from_request(self, request):
        mario = request.mario
        
        # Normalize Mario's state (one-hot encoding from the map)
        mario_state_one_hot = np.zeros(len(MarioState))
        for i, state_enum_member in enumerate(MarioState):
            if state_enum_member.name in mario.state and mario.state[state_enum_member.name]:
                mario_state_one_hot[i] = 1

        state = [mario.position.y/Y_MAY]

        # Add antagonist and item distances in context
        def euclidean_distance(x1, y1, x2, y2):
            return np.hypot(x1 - x2, y1 - y2)

        ant_features = np.full(CONTEXT_ANTAGONIST_WIDTH, MAX_DISTANCE)
        item_features = np.full(CONTEXT_ITEM_WIDTH, MAX_DISTANCE)
        coin_features = np.full(CONTEXT_COIN_WIDTH, MAX_DISTANCE)
        
        ant_distances = [
            euclidean_distance(ant.position.x, ant.position.y, mario.position.x, mario.position.y) if ant else float('inf')
            for ant in request.antagonists
        ]
        
        item_distances = [
            euclidean_distance(item.position.x, item.position.y, mario.position.x, mario.position.y) if item else float('inf')
            for item in request.items
        ]
        
        coin_distances = [
            euclidean_distance(coin.position.x, coin.position.y, mario.position.x, mario.position.y) if coin else float('inf')
            for coin in request.coins
        ]
        
        
        for i, dist in enumerate(ant_distances[:CONTEXT_ANTAGONIST_WIDTH]):
            ant_features[i] = dist/MAX_DISTANCE
        
        for i, dist in enumerate(item_distances[:CONTEXT_ITEM_WIDTH]):
            item_features[i] = dist/MAX_DISTANCE
            
        for i, dist in enumerate(coin_distances[:CONTEXT_COIN_WIDTH]):
            coin_features[i] = dist/MAX_DISTANCE


        # Flatten and combine
        flat_state = np.concatenate([mario_state_one_hot, state, ant_features, item_features, coin_features]).ravel()
        
        # Ensure state is correct size, pad if necessary
        if len(flat_state) < STATE_SIZE:
            flat_state = np.pad(flat_state, (0, STATE_SIZE - len(flat_state)), 'constant')

        return flat_state.reshape(1, STATE_SIZE)

    def _calculate_reward(self, last_mario, current_mario):
        reward = 0
        
        
        delta_x = current_mario.position.x - last_mario.position.x
        reward += delta_x * 0.5 

        if delta_x > 0:
            reward += 0.5
        elif delta_x < 0:
            reward -= 0.5
        
        # Big penalty for dying
        if current_mario.state[MarioState.DEAD.name] == MarioState.DEAD.name:
            reward -= 10
            return reward
            
        if current_mario.state[MarioState.HIT_BY_ANTAGONIST.name] == MarioState.HIT_BY_ANTAGONIST.name:
            reward -= 2
            
        if current_mario.state[MarioState.BLOCKING_BY_OBJECT_HORIZONTAL.name] == MarioState.BLOCKING_BY_OBJECT_HORIZONTAL.name:
            reward -= 1
            
        if current_mario.state[MarioState.BLOCKING_BY_HORIZONTAL_BEGINNING_MAP.name] == MarioState.BLOCKING_BY_HORIZONTAL_BEGINNING_MAP.name:
            reward -= 2
            
            
        if current_mario.state[MarioState.STANDING.name] == MarioState.STANDING.name and last_mario.state[MarioState.STANDING.name] == MarioState.STANDING.name:
            reward -= 0.5
            
        # Reward for jumping
        if current_mario.state[MarioState.JUMPING.name] == MarioState.JUMPING.name and last_mario.state[MarioState.JUMPING.name] != MarioState.JUMPING.name:
            reward += 10
            
            
        if current_mario.state[MarioState.HIT_COIN.name] == MarioState.HIT_COIN.name:
            reward += 1
        
        # Reward for killing an antagonist (logic to be improved)
        if current_mario.state[MarioState.KILLING_ANTAGONIST.name] == MarioState.KILLING_ANTAGONIST.name:
            reward += 2
            
        if current_mario.state[MarioState.ZOMBIFIYING_ANTAGONIST.name] == MarioState.ZOMBIFIYING_ANTAGONIST.name:
            reward += 1.5


        reward += 0.01

        return reward

    def GetAction(self, request, context):
        current_state = self._get_state_from_request(request)
        done = request.mario.state == "DEAD" or request.mario.state == "WIN"

        if self.last_state is not None:
            reward = self._calculate_reward(self.last_mario, request.mario)
            self.agent.remember(self.last_state, self.last_action, reward, current_state, done)

        action = self.agent.act(current_state)

        self.last_state = current_state
        self.last_action = action
        self.last_mario = request.mario

        if done:
            self.last_state = None
            self.last_action = None
            self.last_mario = None

        return data_pb2.Action(action=action)

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    data_pb2_grpc.add_GameServiceServicer_to_server(GameServiceServicer(), server)
    server.add_insecure_port('[::]:{}'.format(PORT))
    server.start()
    print("Server started, listening on port 50051")
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)

if __name__ == '__main__':
    serve()
