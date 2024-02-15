package xyz.zeppelin.casino.game;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a game session.
 * A game session is a single instance of a game being played by a group or a single player.
 * It contains the state of the game session and the players involved.
 * Responsible for controlling the game and the players involved.
 * Game sessions should not be reused, a new game session should be created for each game played.
 *
 * @see Game
 */
public interface GameSession {

    /**
     * Starts the game session.
     *
     * @return true if the game session was started, false if the game session was already started.
     */
    boolean start();

    /**
     * Stops the game session.
     *
     * @return true if the game session was stopped, false if the game session was already stopped or has not been started.
     */
    boolean stop();

    /**
     * Returns the state of the game session.
     *
     * @return the state of the game session.
     */
    State getState();

    /**
     * Returns the game being played in the game session.
     *
     * @return the game being played in the game session.
     */
    Game getGame();

    /**
     * Returns the players involved in the game session.
     *
     * @return the players involved in the game session.
     */
    List<Player> getPlayers();

    /**
     * The state of the game session.
     */
    enum State {
        /**
         * The game session is idle and has not been started.
         */
        Idle,
        /**
         * The game session is currently running.
         */
        Going,
        /**
         * The game session has finished.
         */
        Finished,
        /**
         * The game session has been stopped.
         */
        Stopped
    }
}
