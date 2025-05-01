package cz.cvut.copakond.sweetfluffysheep.model.utils.files;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.TextureListEnum;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TextureManager {
    private static final Logger logger = Logger.getLogger(TextureManager.class.getName());

    private static final Map<String, Image> loadedTextures = new HashMap<>();
    private static final Map<String, List<Image>> loadedTexturesList = new HashMap<>();
    private static final Map<String, List<int[]>> loadedTexturesSizes = new HashMap<>();

    private static String texturesPath;

    public static void setTexturesPath(String path) {
        texturesPath = path;
    }

    // avoid loading the same textures multiple times
    private static Image getLoadedTexture(String textureName) {
        if (loadedTextures.containsKey(textureName)) {
            return loadedTextures.get(textureName);
        }

        Image result;
        try {
            result = new Image(new File(textureName).toURI().toURL().toExternalForm());
            if (result.getHeight() == 0 || result.getWidth() == 0) {
                throw new RuntimeException("Texture not found: " + textureName);
            }
            loadedTextures.put(textureName, result); // cache the texture
        } catch (Exception e) {
            try {
                result = new Image(new File(texturesPath + "/missing_texture.png").toURI().toURL().toExternalForm());
                logger.warning(ErrorMsgsEnum.TEXTURE_MISSING.getValue(textureName, e));
            } catch (Exception e2) {
                logger.info(new File(texturesPath + "/missing_texture.png").getAbsolutePath());
                String e3 = ErrorMsgsEnum.TEXTURE_MISSING_IS_MISSING.getValue(textureName, e2);
                logger.warning(e3);
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
        List<Image> textures = new ArrayList<>();
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
        List<Image> textures = new ArrayList<>();
        for (int index : textureSelections) {
            if (index >= 0 && index < textureNames.length + 1) {
                String textureName = textureNames[index - 1];
                textures.add(getLoadedTexture(textureName));
            } else {
                logger.severe(ErrorMsgsEnum.TEXTURE_OUT_OF_INDEX.getValue(objectName + " " + index));
            }
        }
        loadedTexturesList.put(objectName, textures);
        return textures;
    }

    public List<int[]> getTextureSizes(List<Image> textures, String textureName) {
        if (loadedTexturesSizes.containsKey(textureName)) {
            return loadedTexturesSizes.get(textureName);
        }

        List<int[]> sizes = new ArrayList<>();
        for (Image texture : textures) {
            sizes.add(new int[]{(int) texture.getWidth(), (int) texture.getHeight()});
        }

        loadedTexturesSizes.put(textureName, sizes);
        return sizes;
    }
}