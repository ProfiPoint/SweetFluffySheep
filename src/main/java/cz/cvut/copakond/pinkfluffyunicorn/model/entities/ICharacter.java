package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

/**
 * Interface representing a game character (e.g., Unicorn or Cloud).
 */
public interface ICharacter {

    /**
     * Moves the character by a given number of tiles in its current direction.
     *
     * @param tiles Number of tiles to move.
     * @param doesTimeFlow Whether time is currently flowing (true for frame renderings + clock only moves with renders).
     */
    void move(double tiles, boolean doesTimeFlow);

    /**
     * Checks if the character is still alive.
     *
     * @return true if alive, false otherwise.
     */
    boolean isAlive();

    /**
     * Checks if the character is an enemy (e.g., a Cloud).
     *
     * @return true if enemy, false otherwise.
     */
    boolean isEnemy();
}