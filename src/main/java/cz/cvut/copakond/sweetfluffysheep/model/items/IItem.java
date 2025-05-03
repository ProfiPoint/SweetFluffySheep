package cz.cvut.copakond.sweetfluffysheep.model.items;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.IGameObject;

/**
 * Interface representing an item in the game.
 * An item can be used to perform an action or provide an effect.
 */
public interface IItem extends IGameObject {

    /**
     * Uses the item, applying its effect.
     *
     * @return true if the item was successfully used, false otherwise.
     */
    boolean use();

    /**
     * Returns the type of the effect, alternatively can be known by Item.class
     *
     * @return the type of the effect as an ItemEnum.
     */
    ItemEnum getItemEffect();
}
