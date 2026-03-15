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

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

# --- Configuration ---
STATE_SIZE = 1 + 5 + 5  # Simplified state size
ACTION_SIZE = 4  # 0: do nothing, 1: forward, 2: backward, 3: jump
BATCH_SIZE = 32

MODEL_DIR = "../dump"
MODEL_NAME = "mario_dqn.weights.h5"
MODEL_PATH = os.path.join(MODEL_DIR, MODEL_NAME)
SAVE_FREQUENCY = 100 # Save model every 100 replay steps

class GameServiceServicer(data_pb2_grpc.GameServiceServicer):
    def __init__(self):
        self.agent = DQN(state_size=STATE_SIZE, action_size=ACTION_SIZE)
        self.last_state = None
        self.last_action = None
        self.last_mario = None
        self.mario_states = [
            "STANDING", "WALKING", "JUMPING", "FALLING", "DEAD", "WIN", 
            "ON_OBJECT", "BLOCKING_BY_OBJECT_HORIZONTAL", "BLOCKING_BY_OBJECT_VERTICAL",
            "BLOCKING_BY_SKY", "BLOCKING_BY_HORIZONTAL_BEGINNING_MAP", 
            "BLOCKING_BY_HORIZONTAL_END_MAP", "HIT_COIN", "KILLING_ANTAGONIST",
            "ZOMBIFIYING_ANTAGONIST", "HIT_BY_ANTAGONIST"
        ]
        
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
        
        # Normalize Mario's state
        mario_state_one_hot = np.zeros(len(self.mario_states))
        try:
            state_index = self.mario_states.index(mario.state)
            mario_state_one_hot[state_index] = 1
        except ValueError:
            pass # State not in list, remains all zeros

        # Simplified state: mario's y, and x relative to something could be useful
        # For now, let's use a very simple state
        state = [mario.y]

        # Add antagonist info
        ant_features = np.zeros(5)
        if request.antagonists:
            # Get the closest antagonist
            closest_ant = min(request.antagonists, key=lambda a: abs(a.x - mario.x))
            ant_features[0] = closest_ant.x - mario.x
            ant_features[1] = closest_ant.y - mario.y
            ant_features[2] = 1 if closest_ant.isdead else 0
        
        # Add item info
        item_features = np.zeros(5)
        if request.items:
            # Get the closest item
            closest_item = min(request.items, key=lambda i: abs(i.x - mario.x))
            item_features[0] = closest_item.x - mario.x
            item_features[1] = closest_item.y - mario.y

        # Flatten and combine
        flat_state = np.concatenate([state, ant_features, item_features]).ravel()
        
        # Ensure state is correct size, pad if necessary
        if len(flat_state) < STATE_SIZE:
            flat_state = np.pad(flat_state, (0, STATE_SIZE - len(flat_state)), 'constant')

        return flat_state.reshape(1, STATE_SIZE)

    def _calculate_reward(self, last_mario, current_mario):
        reward = 0
        # Reward for moving forward
        if current_mario.x > last_mario.x:
            reward += 1
        # Penalty for moving backward
        elif current_mario.x < last_mario.x:
            reward -= 1.5
        
        # Big penalty for dying
        if current_mario.state == "DEAD":
            reward -= 100
        
        # Reward for jumping
        if current_mario.state == "JUMPING" and last_mario.state != "JUMPING":
            reward += 0.5

        # Reward for killing an antagonist (logic to be improved)
        if current_mario.state == "KILLING_ANTAGONIST":
            reward += 10

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
    server.add_insecure_port('[::]:50051')
    server.start()
    print("Server started, listening on port 50051")
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)

if __name__ == '__main__':
    serve()
