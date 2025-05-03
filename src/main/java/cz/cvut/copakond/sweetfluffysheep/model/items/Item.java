package cz.cvut.copakond.sweetfluffysheep.model.items;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.RenderPriorityEnums;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import javafx.scene.image.Image;

/**
 * Item class represents an item in the game.
 * It extends the GameObject class and implements the IItem interface.
 * The item has a texture, position, duration, and effect.
 * The item can be picked up and used by the player.
 * Each item must inherit from this class.
 */
public class Item extends GameObject implements IItem {
    // 10x per second it will update the anim texture
    private static final int textureChangeFrameCoefficient = (int) Math.ceil((double) GameObject.getFPS() / 10);

    protected int durationTicks;
    private final ItemEnum itemEffect;
    private boolean pickable;

    /**
     * Constructor for the Item class.
     *
     * @param textureName  The name of the texture for the item.
     * @param position     The position of the item in the game world.
     * @param duration     The duration of the item effect in seconds.
     * @param itemEffect   The effect of the item (e.g., COIN, FREEZE).
     */
    public Item(String textureName, double[] position, int duration, ItemEnum itemEffect) {
        super(textureName, position, RenderPriorityEnums.ITEM.getValue());
        this.durationTicks = duration * getFPS();
        this.itemEffect = itemEffect;
        this.pickable = true;
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
    }

    @Override
    public boolean use() {
        if (pickable){
            if (itemEffect != ItemEnum.COIN) {
                SoundManager.playSound(SoundListEnum.PRIZE);
            }
            pickable = false;
            super.visible = false;
            return true;
        }
        return false;
    }

    @Override
    public ItemEnum getItemEffect() {
        return itemEffect;
    }

    @Override
    public Image getTexture() {
        this.textureIdNow = (int)((Level.getCurrentCalculatedFrame()/textureChangeFrameCoefficient) % 32);
        return this.textures.get(this.textureIdNow);
    }
}
