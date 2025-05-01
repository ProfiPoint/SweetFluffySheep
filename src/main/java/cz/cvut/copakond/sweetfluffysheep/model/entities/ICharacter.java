package cz.cvut.copakond.sweetfluffysheep.model.entities;

/**
 * Interface representing a game character (e.g., Sheep or Wolf).
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
     * Checks if the character is an enemy (e.g., a Wolf).
     *
     * @return true if enemy, false otherwise.
     */
    boolean isEnemy();
}