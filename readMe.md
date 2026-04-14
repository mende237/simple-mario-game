# Mario JavaFX Game

Welcome to the **Mario JavaFX Game**!
This is a simple Mario-style platformer built with Java 11 and JavaFX.
Enjoy classic gameplay, collect coins, avoid enemies, and reach the flag!

---

## 🎮 Features

- Classic Mario platformer mechanics
- Smooth character movement and jumping
- Enemies: Champignons (mushrooms) and Turtles
- Collectible coins with animated sprites
- Score and timer display
- Level restart and game over transitions
- Responsive controls (keyboard)
- Custom pixel-art graphics

---

## 🖼️ Screenshots

| ![Screenshot 1](screenshot/Screenshot_1.png) | ![Screenshot 2](screenshot/Screenshot_2.png) | ![Screenshot 3](screenshot/Screenshot_3.png) |
|:--:|:--:|:--:|

---

## 🚀 Getting Started

### Prerequisites

- Java 11+
- Maven
- Perl (required to use the `run.pl` script for simplified execution)
- Python 3.x (required for the Reinforcement Learning model)
- Python virtual environment (recommended for managing RL model dependencies)
- `lsof` (for Linux/macOS users, if using `run.pl` to automatically kill processes on port 50051)

### Build & Run

The project can be built and run in two main ways:

#### 1. Running the Java Game Only

To run only the JavaFX game without the Reinforcement Learning model:

```sh
cd mario
mvn clean install
mvn javafx:run
```

The game window will open. Use your keyboard to play!

#### 2. Running the Java Game with the Python RL Server (Recommended)

For a more integrated experience, especially if you plan to interact with or train the RL model, use the provided Perl script `run.pl`. This script handles starting the Python gRPC server and then launching the Java game.

First, ensure you have the Python dependencies installed, preferably in a virtual environment. Navigate to the `model` directory and install them:

```sh
cd model
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
deactivate
cd ..
```

Then, from the project root directory, run the game and the RL server using `run.pl`:

```sh
# To run with the Python RL server using a virtual environment
perl run.pl --run-server --venv model/venv

# To run with the Python RL server using system Python (not recommended)
perl run.pl --run-server
```

The `run.pl` script will:
- Kill any existing process on port 50051 (where the gRPC server runs).
- Start the Python gRPC server in the background.
- Build the Java project (`mvn clean install`).
- Launch the JavaFX game.

Output from the Python server will be redirected to `model/proto_server/logs/python-server.log`. You can stop the Python server manually by finding its process ID (e.g., `ps aux | grep server.py`) and killing it.

---

## 🎹 Controls

- **Numpad 6**: Move Right
- **Numpad 4**: Move Left
- **Space**: Jump

---

## 📁 Project Structure

- `src/main/java/com/game/mario/` — Main game logic and classes
- `src/main/resources/com/game/mario/` — FXML layouts and resources
- `screenshot/` — Game screenshots
- `model/` — Contains the Python-based reinforcement learning model and gRPC server.

---

## 🛠️ Technologies

- Java 11
- JavaFX 13
- Maven
- Python 3.x (for RL model)
- gRPC (for communication between Java game and Python model)

---

## 🤖 Reinforcement Learning Model Training Workflow

This project includes a reinforcement learning (RL) model that can be trained to play the game automatically. The training workflow involves a continuous interaction between the Java game and a Python gRPC server.

### 1. Initialization
-   **Python Server (`server.py`)**: Initializes a `DQN` (Deep Q-Network) agent with a neural network, experience memory, and hyperparameters.
-   **Server State**: `server.py` also initializes `last_state`, `last_action`, and `last_mario` to `None`.

### 2. Game Loop and Data Collection
-   **Java Game (`GamerAI.java`)**: Continuously collects the current game state, packages it into a `GameData` protobuf message, and sends it via gRPC to the Python server's `GetAction` method.

### 3. State Representation
-   **Python Server (`server.py:_get_state_from_request`)**: Transforms the `GameData` message into a flat numerical `state` vector (NumPy array) for the neural network. This includes Mario's coordinates, `MarioState`, and relative positions/statuses of antagonists, items, and coins.

### 4. Experience Storage and Reward Calculation
-   **Python Server (`server.py:GetAction` and `_calculate_reward`)**: Calculates a `reward` for the *previous* action based on game events (e.g., moving forward, dying, jumping). The experience tuple `(last_state, last_action, reward, current_state, done)` is stored in the `DQN` agent's replay buffer.

### 5. Action Selection
-   **Python Server (`server.py:GetAction`) calls `dqn.py:act`**: The `DQN` agent uses an `epsilon-greedy` strategy to select an action:
    -   With probability `epsilon`, a random action is chosen (exploration).
    -   With probability `1 - epsilon`, the neural network predicts Q-values, and the action with the highest Q-value is selected (exploitation).

### 6. Action Execution
-   **Python Server (`server.py:GetAction`)**: Sends the selected `action` back to `GamerAI.java`.
-   **Java Game (`GamerAI.java`, `SceneUpdater.java`)**: Receives the action, stores it, and the `SceneUpdater` translates it into game commands (e.g., `mario.setWalke(true)`, `mario.setJump(true)`). Keyboard input is ignored if an AI action is pending.

### 7. Model Training (Replay)
-   **Python Server (`server.py:GetAction`) calls `dqn.py:replay`**: If enough samples are in memory, a `minibatch` of experiences is randomly sampled. Target Q-values are calculated using the Bellman equation, and the neural network is trained to match these targets. `epsilon` is gradually decayed.

### 8. State Update
-   **Python Server (`server.py:GetAction`)**: Updates `self.last_state`, `self.last_action`, and `self.last_mario` for the next iteration. These are reset to `None` if an episode terminates.

---

## 🏗️ Building Your Own Reinforcement Learning Model

To build your own custom reinforcement learning model for this game, you will primarily interact with the following files:

-   **`proto/data.proto`**: This Protocol Buffer definition file is critical as it defines the structure of the data exchanged between the Java game and your Python-based RL model.
```protobuf
syntax = "proto3";

package proto;

message Position {
    int32 x = 1;
    int32 y = 2;
}

message Dimensions {
    int32 height = 1;
    int32 width = 2;
}

// Represents a game object with position and dimensions.
message Mario {
    Position position = 1;
    Dimensions dimensions = 2;
    int32 numberOfLive = 3;
    map<string, bool> state = 4;
}

// Represents an antagonist character.
message Antagonist {
    Position position = 1;
    Dimensions dimensions = 2;
    int32 speed = 3;
    string name = 4;
    bool isdead = 5;
    bool isZombie = 6;
}

// Represents an item in the game.
message Item {
    Position position = 1;
    Dimensions dimensions = 2;
    string name = 3;
}

message Coin{
    Position position = 1;
    Dimensions dimensions = 2;
}

// Request data containing the state of the game.
message GameData {
    Mario mario = 1;
    int32 floor_level = 2;
    int32 antagonist_context_width = 3;
    int32 item_context_width = 4;
    repeated Antagonist antagonists = 5;
    repeated Item items = 6;
    repeated Coin coins = 7;
}

// Response data containing the action to be taken.
message Action {
    int32 action = 1;
}

// The game service definition.
service GameService {
    // Sends game data and receives an action.
    rpc GetAction(GameData) returns (Action) {}
}
```
- `GameData` message: This is the input your model will receive from the game, containing information about Mario, antagonists, items, coins, and game context. Your model's observation space will be derived from this data.
- `Action` message: This is the output your model must produce, indicating the action to be taken in the game.
Understanding these message structures is fundamental to correctly interpret game states and generate valid actions. `0: do nothing, 1: forward, 2: backward, 3: jump`

-   **`config/context.json`**: This configuration file provides essential context parameters that influence the game state and, consequently, the observations your model receives.
```json
{
    "contextItemWidth": 5,
    "contextAntogonistWidth": 6,
    "contextCoinWidth": 5,
    "windowFilter": {
        "min": 0,
        "max": 800
    }
}
```
![Screenshot 1](screenshot/Game_informations_extraction.png)

-   `windowFilter`: This represents the window within which information is collected. By default, humans perceive information within the game window, but it's possible to change this by modifying the `min` and `max` values. By default, these values correspond to the dimensions of the game window; you can enlarge or reduce this window.

-   `contextItemWidth`, `contextAntogonistWidth`, and `contextCoinWidth`: These respectively represent the number of items, antagonists, and coins that are extracted within the window defined by `windowFilter`.

-   **`config/server.json`**: This file specifies the network configuration for the gRPC server that facilitates communication between the Java game and your Python model.
```json
{
    "host": "localhost",
    "port": 50051
}
```
-   `host` and `port`: These define where your gRPC server (e.g., `model/proto_server/server.py`) will listen for incoming game data requests. Ensure your custom model's server implementation uses these same host and port settings to establish a connection with the game.

By understanding and utilizing these files, you can design a custom RL agent that processes game states, makes decisions, and interacts seamlessly with the Mario game environment.

---

## 🔒 Synchronization Details

The game uses `java.util.concurrent.locks.ReentrantLock` objects for thread synchronization, particularly for managing concurrent access to shared resources and character states among antagonists.

### 1. Individual Antagonist Threads
-   `Champignon` and `Turtle` instances run in their own threads, executing their `move()` method concurrently.

### 2. Per-Antagonist Position Lock (`positionLocker`)
-   Each antagonist has its own `protected ReentrantLock positionLocker`.
-   The `move()` method acquires this lock before updating an antagonist's position, ensuring exclusive access.

### 3. Adjacent Character Lock Checks
-   Before moving, antagonists check the `positionLocker` status of their `frontCharacter` and `behindCharacter`. Movement proceeds only if adjacent characters are not currently holding their locks, preventing race conditions in close proximity.

### 4. Global `GameManager` Locks
-   `GameManager` manages global read and write locks for all antagonist positions (`getAllAntagonistPositionReaderLocker()` and `getAllAntagonistPositionWriterLocker()`).
-   Individual antagonist movements acquire a read lock and pause if a global write operation is in progress.
-   The `Collision.antagonist()` method acquires a global write lock when updating `frontCharacter` and `behindCharacter` references, ensuring atomic and consistent updates of antagonist relationships.

This combined approach of per-object and global locks ensures consistency and prevents race conditions in the game's concurrent environment.

---

## 📜 License

This project is for educational purposes.

## 🔮 Perspectives

A future prospect for this project is to train a reinforcement learning model to play this game automatically, as detailed in the "Reinforcement Learning Model Training Workflow" section. This will enable us to explore artificial intelligence applied to video games and improve the performance of agents in interactive environments.

---
Enjoy playing!
Feel free to contribute or suggest improvements.
