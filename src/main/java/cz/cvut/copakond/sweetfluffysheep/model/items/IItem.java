package cz.cvut.copakond.sweetfluffysheep.model.items;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.IGameObject;

public interface IItem extends IGameObject {
    boolean use();
    ItemEnum getItemEffect();
}
