import json
import os
from .mario_state import MarioState 
import numpy as np

with open("../../config/context.json", "r", encoding="utf-8") as f:
    data = json.load(f)

CONTEXT_ANTAGONIST_WIDTH = data["contextAntogonistWidth"]
CONTEXT_ITEM_WIDTH = data["contextItemWidth"]
CONTEXT_COIN_WIDTH = data["contextCoinWidth"]

with open("../../config/server.json", "r", encoding="utf-8") as f:
    data = json.load(f)
HOST = data["host"]
PORT = data["port"]

with open("../../const/position.json", "r", encoding="utf-8") as f:
    data = json.load(f)
X_MAX = data["furtherPoint"]["x"]
Y_MAY = data["furtherPoint"]["y"]

MAX_DISTANCE = np.hypot(X_MAX, Y_MAY)

# DQN Configuration
STATE_SIZE = len(MarioState) + 1 + CONTEXT_ANTAGONIST_WIDTH + CONTEXT_ITEM_WIDTH + CONTEXT_COIN_WIDTH # Mario state + y position + antagonist context + item context + coin context
ACTION_SIZE = 4  # 0: do nothing, 1: forward, 2: backward, 3: jump
BATCH_SIZE = 32

MODEL_DIR = "../dump"
MODEL_NAME = "mario_dqn.weights.h5"
MODEL_PATH = os.path.join(MODEL_DIR, MODEL_NAME)
SAVE_FREQUENCY = 100 # Save model every 100 replay steps

