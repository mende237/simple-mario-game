## Reinforcement Learning Model Training Workflow

This document outlines the workflow for the reinforcement learning model training, focusing on the interaction between the Java game and the Python gRPC server.

### 1. Initialization

-   **Python Server (`server.py`)**: When the Python server starts, it initializes a `DQN` (Deep Q-Network) agent.
-   **DQN Agent (`dqn.py`)**: The `DQN` agent sets up:
    -   A neural network (`self.model`) for approximating Q-values.
    -   An experience `memory` (a `deque`) to store past interactions.
    -   Hyperparameters such as `gamma` (discount rate), `epsilon` (exploration rate), `epsilon_min`, `epsilon_decay`, and `learning_rate`.
-   **Server State**: `server.py` also initializes `last_state`, `last_action`, and `last_mario` to `None`, as there's no prior game state at the beginning.

### 2. Game Loop and Data Collection

-   **Java Game (`GamerAI.java`)**: The `GamerAI` thread in the Java game continuously:
    -   Collects the current game state, including Mario's position and status, and the context of nearby antagonists and items.
    -   Packages this information into a `GameData` protobuf message.
    -   Sends this `GameData` message via gRPC to the `GetAction` method on the Python server.

### 3. State Representation

-   **Python Server (`server.py:_get_state_from_request`)**: Upon receiving the `GameData` message, the `_get_state_from_request` method in `server.py` transforms the structured protobuf data into a flat numerical `state` vector (a NumPy array). This involves:
    -   Extracting Mario's coordinates (x, y) and encoding his current `MarioState` (e.g., "STANDING", "JUMPING") into a numerical format.
    -   Including relative positions (x, y) and statuses (e.g., `isdead`) of antagonists within Mario's context.
    -   Including relative positions (x, y) of game items within Mario's context.
    -   The resulting vector serves as the input for the neural network.

### 4. Experience Storage and Reward Calculation

-   **Python Server (`server.py:GetAction` and `_calculate_reward`)**:
    -   If it's not the very first step of an episode (i.e., `self.last_state` is not `None`), the server calculates a `reward` for the *previous* action taken.
    -   The `_calculate_reward` method compares the `last_mario` state (from the previous step) with the `current_mario` state (from the current `GameData`). Rewards are assigned based on game events:
        -   Positive reward for moving forward (increasing x-position).
        -   Penalty for moving backward (decreasing x-position).
        -   Significant penalty if Mario's state becomes "DEAD".
        -   Small positive reward for initiating a "JUMPING" state.
        -   Potential rewards for killing antagonists or collecting items (though these might require more sophisticated state tracking).
    -   The complete "experience tuple" `(last_state, last_action, reward, current_state, done)` is then stored in the `DQN` agent's `memory` (also known as a replay buffer). `done` is a boolean indicating if the current game episode has terminated (e.g., Mario died or won).

### 5. Action Selection

-   **Python Server (`server.py:GetAction`) calls `dqn.py:act`**:
    -   The `DQN` agent's `act` method is invoked with the `current_state` vector.
    -   It employs an `epsilon-greedy` strategy to balance exploration (trying new actions) and exploitation (using learned knowledge):
        -   With a probability `epsilon`, it chooses a random action from the available actions (0: do nothing, 1: move forward, 2: move back, 3: jump). This encourages the agent to discover new strategies.
        -   With a probability `1 - epsilon`, it uses its neural network (`self.model.predict(state)`) to predict the Q-values for all possible actions and selects the action with the highest predicted Q-value. This leverages the agent's current understanding of the game.
    -   The chosen `action` (an integer from 0 to 3) is returned to `server.py`.

### 6. Action Execution

-   **Python Server (`server.py:GetAction`)**: Sends the selected `action` back to `GamerAI.java` via the gRPC response.
-   **Java Game (`GamerAI.java`)**: Receives the action and calls `stage.setAiAction(action.getAction())` to store it in the game's `Stage` object.
-   **Java Game (`SceneUpdater.java`)**: In the game's main loop, the `SceneUpdater` reads the `aiAction` from the `Stage` object. It then translates this integer action into actual game commands by manipulating Mario's properties (e.g., `mario.setWalke(true)`, `mario.setJump(true)`) and the scene's displacement (`scene.setDx`). After processing, `stage.setAiAction(-1)` is called to reset the action, preventing it from being re-executed.
-   **Keyboard Input Handling (`Clavier.java`)**: The `Clavier` class is modified to ignore user keyboard input if an AI action is currently pending (`App.scene.getAiAction() != -1`), ensuring that the AI has control when active.

### 7. Model Training (Replay)

-   **Python Server (`server.py:GetAction`) calls `dqn.py:replay`**:
    -   After storing an experience, if the `memory` contains enough samples (more than `BATCH_SIZE`), `server.py` triggers the `DQN` agent's `replay` method.
    -   `replay` randomly samples a `minibatch` of experiences from the `memory`. This breaks correlations between consecutive experiences and improves training stability.
    -   For each experience in the minibatch, it calculates the "target Q-value" using the Bellman equation:
        `Target Q = Reward + gamma * max(Q(next_state))`
        If the episode is `done`, the `Target Q` is simply the `Reward`.
    -   The neural network is then trained (`self.model.fit`) using this minibatch. The network learns to adjust its weights so that its predicted Q-value for the `action` taken in the `state` matches the calculated `Target Q`. This is a supervised learning step where the network learns to estimate the long-term value of actions.
    -   The `epsilon` value is gradually decayed over time (`epsilon *= epsilon_decay`), which means the agent explores less and exploits its learned knowledge more as training progresses.

### 8. State Update

-   **Python Server (`server.py:GetAction`)**: Finally, `server.py` updates `self.last_state`, `self.last_action`, and `self.last_mario` to the `current_state`, the chosen `action`, and `request.mario` respectively. This prepares the server for the next iteration of the game loop.
-   If the episode is `done`, these `last_` variables are reset to `None` to signify the start of a fresh episode.

This continuous cycle of observing the game state, selecting an action, executing it, receiving a reward, storing the experience, and training the model allows the DQN agent to learn an optimal policy for playing the game over time.
