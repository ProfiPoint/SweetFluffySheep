package cz.cvut.copakond.pinkfluffyunicorn.model.utils.files;

import cz.cvut.copakond.pinkfluffyunicorn.Launcher;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.TextureListEnum;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TextureManager {
    private static final Logger logger = Logger.getLogger(TextureManager.class.getName());
    
    private final static Map<String, Image> loadedTextures = new HashMap<String, Image>();
    private final static Map<String, List<Image>> loadedTexturesList = new HashMap<String, List<Image>>();
    private final static Map<String, List<int[]>> loadedTexturesSizes = new HashMap<String, List<int[]>>();

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
                // print the full path of the file
                logger.info(new File(texturesPath+"/missing_texture.png").getAbsolutePath());
                String e3 = ErrorMsgsEnum.TEXTURE_MISSING_IS_MISSING.getValue(textureName, e2);
                throw new RuntimeException(e3);
            }
        }
        return result;
    }
    
    public List<Image> getTexture(String objectName) {
        if (loadedTexturesList.containsKey(objectName)) {
            return loadedTexturesList.get(objectName);
        }

        TextureListEnum textureListEnum = TextureListEnum.fromValue(objectName);
        String[] textureNames = textureListEnum.getTextures();
        List<Image> textures = new ArrayList<Image>();
        for (String textureName : textureNames) {
            textures.add(getLoadedTexture(textureName));
        }
        loadedTexturesList.put(objectName, textures);
        return textures;
    }

    public List<Image> getTexture(String objectName, int[] textureSelections) {
        if (loadedTexturesList.containsKey(objectName)) {
            return loadedTexturesList.get(objectName);
        }

        TextureListEnum textureListEnum = TextureListEnum.fromValue(objectName);
        String[] textureNames = textureListEnum.getTextures();
        List<Image> textures = new ArrayList<Image>();
        for (int index : textureSelections) {
            if (index >= 0 && index < textureNames.length + 1) {
                String textureName = textureNames[index-1];
                textures.add(getLoadedTexture(textureName));
            } else {
                ErrorMsgsEnum.TEXTURE_OUT_OF_INDEX.getValue(objectName + " " + index);
            }
        }
        loadedTexturesList.put(objectName, textures);
        return textures;
    }

    public List<int[]> getTextureSizes(List<Image> textures, String textureName) {
        if (loadedTexturesSizes.containsKey(textureName)) {
            return loadedTexturesSizes.get(textureName);
        }

        List<int[]> sizes = new ArrayList<int[]>();
        for (Image texture : textures) {
            sizes.add(new int[] { (int) texture.getWidth(), (int) texture.getHeight() });
        }

        loadedTexturesSizes.put(textureName, sizes);
        return sizes;
    }
}
