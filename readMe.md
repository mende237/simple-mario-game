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

### Build & Run

```sh
mvn clean javafx:run
```

The game window will open. Use your keyboard to play!

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
-   **Python Server (`server.py:_get_state_from_request`)**: Transforms the `GameData` message into a flat numerical `state` vector (NumPy array) for the neural network. This includes Mario's coordinates, `MarioState`, and relative positions/statuses of antagonists and items.

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
