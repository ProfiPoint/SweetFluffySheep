package cz.cvut.copakond.pinkfluffyunicorn.model.utils.files;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.TextureListEnum;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureManager {
    private final static Map<String, Image> loadedTextures = new HashMap<String, Image>();
    private static String texturesPath;

    List<Image> loadTexture(List<String> textureNames) {
        List<Image> images = new ArrayList<Image>();
        for (String textureName : textureNames) {
            images.add(new Image(textureName));
        }
        return images;
    }

    public static void setTexturesPath(String path) {
        texturesPath = path;
    }

    // avoid loading the same textures multiple times
    Image getLoadedTexture(String textureName) {
        if (loadedTextures.containsKey(textureName)) {
            return loadedTextures.get(textureName);
        }
        Image result;
        try {
            result = new Image(new File(textureName).toURI().toURL().toExternalForm());
        } catch (Exception e) {
            try {
                result = new Image(new File(texturesPath+"/missing_texture.png").toURI().toURL().toExternalForm());
                ErrorMsgsEnum.TEXTURE_MISSING.getValue(textureName, e);
            } catch (Exception e2) {
                String e3 = ErrorMsgsEnum.TEXTURE_MISSING_IS_MISSING.getValue(textureName, e2);
                throw new RuntimeException(e3);
            }
        }
        return result;
    }
    
    public List<Image> getTexture(String objectName) {
        TextureListEnum textureListEnum = TextureListEnum.fromValue(objectName);
        String[] textureNames = textureListEnum.getTextures();
        List<Image> textures = new ArrayList<Image>();
        for (String textureName : textureNames) {
            textures.add(getLoadedTexture(textureName));
            // print the w,h of the previously loaded texture
             System.out.println("Texture name: " + textures.get(textures.size() - 1).getUrl() + " Texture size: " +
             textures.get(textures.size() - 1).getWidth() + "x" + textures.get(textures.size() - 1).getHeight());
        }
        return textures;
    }

    public List<Image> getTexture(String objectName, int[] textureSelections) {
        TextureListEnum textureListEnum = TextureListEnum.fromValue(objectName);
        String[] textureNames = textureListEnum.getTextures();
        List<Image> textures = new ArrayList<Image>();
        for (int index : textureSelections) {
            if (index >= 0 && index < textureNames.length + 1) {
                String textureName = textureNames[index-1];
                textures.add(getLoadedTexture(textureName));
                // print the w,h of the previously loaded texture
                 System.out.println("Texture name: " + textures.get(textures.size() - 1).getUrl() + " Texture size: " +  textures.get(textures.size() - 1).getWidth() + "x" + textures.get(textures.size() - 1).getHeight());
            } else {
                System.err.println("Texture index out of range: " + index);
            }
        }
        return textures;
    }

    public List<int[]> getTextureSizes(List<Image> textures) {
        List<int[]> sizes = new ArrayList<int[]>();
        for (Image texture : textures) {
            sizes.add(new int[] { (int) texture.getWidth(), (int) texture.getHeight() });
        }
        return sizes;
    }
}
