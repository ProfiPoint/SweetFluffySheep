package cz.cvut.copakond.pinkfluffyunicorn.model.utils;

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

    public List<int[]> getTextureSizes(List<Image> textures) {
        List<int[]> sizes = new ArrayList<int[]>();
        for (Image texture : textures) {
            sizes.add(new int[] { (int) texture.getWidth(), (int) texture.getHeight() });
        }
        return sizes;
    }
}
