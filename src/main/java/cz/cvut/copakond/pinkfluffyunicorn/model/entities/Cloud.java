package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.TextureManager;
import javafx.scene.image.Image;
import java.util.List;

public class Cloud extends Character {
    public Cloud(int[] position, int renderPriority, List<Image> textures, List<int[]> textureSizes,
                 DirectionEnum direction) {
        super(position, renderPriority, textures, textureSizes, direction);
        this.setEnemy(true);
        List<Image> cloudTextures = new TextureManager().getTexture("cloud");
    }
}
