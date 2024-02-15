package xyz.zeppelin.casino.game;

/**
 * Represents a casino game.
 * Games contain only the logic and state of the game.
 */
public interface Game {

    /**
     * Called to prepare the game for starting.
     */
    default void prepare() {
        // Do nothing by default for convenience
    }

    /**
     * Called to clean up the game after it has been stopped.
     */
    default void clear() {
        // Do nothing by default for convenience
    }
}
