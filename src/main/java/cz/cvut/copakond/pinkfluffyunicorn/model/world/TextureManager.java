package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class TextureManager {
    List<Image> loadTexture(List<String> textureNames) {
        List<Image> images = new ArrayList<Image>();
        for (String textureName : textureNames) {
            images.add(new Image(textureName));
        }
        return images;
    }

    public List<Image> getTexture(String objectName) {
        if (objectName.equals("cloud"))    {
            return loadTexture(new ArrayList<String>() {{
                add("cloud.png");
                add("tile1.png");
                add("tile2.png");
            }});
        }
        return null;
    }
}
