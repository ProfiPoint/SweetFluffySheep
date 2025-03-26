package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.TextureManager;
import javafx.scene.image.Image;
import java.util.List;

public class Unicorn extends Character {
    public Unicorn(int[] position, int renderPriority, List<Image> textures, List<int[]> textureSizes,
                 DirectionEnum direction) {
        super(position, renderPriority, textures, textureSizes, direction);
        this.setEnemy(false);
        List<Image> cloudTextures = new TextureManager().getTexture("unicorn");
    }
}
