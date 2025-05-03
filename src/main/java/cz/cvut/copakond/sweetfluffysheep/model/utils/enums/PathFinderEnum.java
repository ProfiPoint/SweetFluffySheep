package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

/**
 * Enum representing different states in a pathfinding algorithm.
 * The pathfinding algorithm is used to find if it is possible to reach a certain cell in a grid in level editor.
 * <p>
 * This enum is used to represent the state of each cell in a grid during pathfinding.
 * The states include:
 * <ul>
 *     <li>VOID: Represents an empty or unvisited cell.</li>
 *     <li>TILE: Represents a cell that is part of the path.</li>
 *     <li>VISITED: Represents a cell that has been visited during the pathfinding process.</li>
 *     <li>COIN: Represents a cell that contains a coin or collectible item.</li>
 * </ul>
 */
public enum PathFinderEnum {
    VOID,
    TILE,
    VISITED,
    COIN,
}
