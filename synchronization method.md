The synchronization of threads in the provided Java files (`Antagonist.java`, `Champignon.java`, `Turtle.java`, and `Collision.java`) is implemented using `java.util.concurrent.locks.ReentrantLock` objects to manage concurrent access to shared resources and character states.

Here's a breakdown of the synchronization strategy:

1.  **Individual Antagonist Threads:**
    *   Both `Champignon` and `Turtle` classes extend `Antagonist` and implement the `Runnable` interface.
    *   In their constructors, each `Champignon` and `Turtle` instance creates and starts its own `Thread`, allowing them to execute their `run()` method concurrently. The `run()` method primarily calls the `move()` method in a loop.

2.  **Per-Antagonist Position Lock (`positionLocker`):**
    *   The `Antagonist` class declares a `protected ReentrantLock positionLocker`. Each antagonist instance has its own `positionLocker`.
    *   In the `move()` method of `Champignon` and `Turtle`, this `positionLocker` is acquired (`this.positionLocker.lock()`) before the antagonist attempts to update its position or interact with other characters. It's released (`this.positionLocker.unlock()`) after the movement logic is completed. This ensures that only one thread can modify a specific antagonist's position at a time.

3.  **Adjacent Character Lock Checks:**
    *   Within the `move()` method, before an antagonist moves, it checks the lock status of its `frontCharacter` and `behindCharacter` (if they exist).
    *   Movement logic proceeds only if `!lockBehind && !lockFront` (i.e., neither the character directly behind nor the character directly in front has their `positionLocker` currently held). This is a fine-grained approach to prevent race conditions when multiple antagonists are in close proximity and might be trying to update their states or interact with each other simultaneously.

4.  **Global `GameManager` Locks:**
    *   The `GameManager` class (not fully provided, but referenced) appears to manage global read and write locks for all antagonist positions: `GameManager.getAllAntagonistPositionReaderLocker()` and `GameManager.getAllAntagonistPositionWriterLocker()`.
    *   In the `move()` method of `Champignon` and `Turtle`:
        *   They attempt to acquire a read lock using `GameManager.getAllAntagonistPositionReaderLocker().tryLock()`.
        *   They proceed with their movement logic only if `!GameManager.getAllAntagonistPositionWriterLocker().isLocked()`. This suggests that individual antagonist movements are paused if a global write operation on antagonist positions is in progress.
    *   In the `Collision.antagonist()` method:
        *   A global write lock is explicitly acquired using `GameManager.getAllAntagonistPositionWriterLocker().lock()` when the method is updating the `frontCharacter` and `behindCharacter` references for antagonists. This ensures that the relationships between antagonists are updated atomically and consistently, preventing other antagonist threads from reading or writing inconsistent state during this critical update.

In summary, the system uses a combination of per-object locks for individual character movement and global locks managed by `GameManager` and `Collision` to ensure consistency when updating relationships between characters or performing broader game state modifications involving multiple antagonists.