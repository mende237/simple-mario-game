import numpy as np
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Input
from tensorflow.keras.optimizers import Adam
import random
from collections import deque
import threading

class DQN:
    def __init__(self, state_size, action_size):
        self.state_size = state_size
        self.action_size = action_size
        self.memory = deque(maxlen=2000)
        self.gamma = 0.95    # discount rate
        self.epsilon = 1.0  # exploration rate
        self.epsilon_min = 0.01
        self.epsilon_decay = 0.995
        self.learning_rate = 0.001
        self.model = self._build_model()
        self.model_lock = threading.Lock()
        self.memory_lock = threading.Lock()

    def _build_model(self):
        # Neural Net for Deep-Q learning Model
        model = Sequential()
        model.add(Input(shape=(self.state_size,)))
        model.add(Dense(24, activation='relu'))
        model.add(Dense(24, activation='relu'))
        model.add(Dense(self.action_size, activation='linear'))
        model.compile(loss='mse',
                      optimizer=Adam(learning_rate=self.learning_rate))
        return model

    def remember(self, state, action, reward, next_state, done):
        with self.memory_lock:
            self.memory.append((state, action, reward, next_state, done))

    def act(self, state):
        with self.model_lock:
            if np.random.rand() <= self.epsilon:
                return random.randrange(self.action_size)
            act_values = self.model.predict(state, verbose=0)
        return np.argmax(act_values[0])

    def replay(self, batch_size):
        with self.memory_lock:
            if len(self.memory) < batch_size:
                return
            minibatch = random.sample(self.memory, batch_size)

        states = np.array([t[0] for t in minibatch]).reshape(-1, self.state_size)
        actions = np.array([t[1] for t in minibatch])
        rewards = np.array([t[2] for t in minibatch])
        next_states = np.array([t[3] for t in minibatch]).reshape(-1, self.state_size)
        dones = np.array([t[4] for t in minibatch])

        with self.model_lock:
            # Predict future discounted reward from next states
            target_next = self.model.predict(next_states, verbose=0)
            # Predict current Q-values
            target_current = self.model.predict(states, verbose=0)

            # Update the targets for the states in the minibatch
            for i in range(batch_size):
                if dones[i]:
                    target_current[i][actions[i]] = rewards[i]
                else:
                    target_current[i][actions[i]] = rewards[i] + self.gamma * np.amax(target_next[i])
            
            # Train the model on the updated targets
            self.model.fit(states, target_current, epochs=1, verbose=0)

            if self.epsilon > self.epsilon_min:
                self.epsilon *= self.epsilon_decay

    def load(self, name):
        with self.model_lock:
            self.model.load_weights(name)

    def save(self, name):
        with self.model_lock:
            self.model.save_weights(name)