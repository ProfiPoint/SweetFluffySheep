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

/**
 * TextureManager is a singleton class responsible for loading and managing textures in the application.
 * It ensures that textures are loaded only once and provides methods to retrieve textures and their sizes.
 */
public class TextureManager {
    private static final Logger logger = Logger.getLogger(TextureManager.class.getName());

    private static final Map<String, Image> loadedTextures = new HashMap<>();
    private static final Map<String, List<Image>> loadedTexturesList = new HashMap<>();
    private static final Map<String, List<int[]>> loadedTexturesSizes = new HashMap<>();

    private static String texturesPath;

    public static void setTexturesPath(String path) {
        texturesPath = path;
    }

    /**
     * Retrieves the singleton instance of TextureManager.
     * Avoids loading the same textures multiple times
     * @return the singleton instance of TextureManager
     */
    private static Image getLoadedTexture(String textureName) {
        // Check if the texture is already loaded, preventing multiple loads
        if (loadedTextures.containsKey(textureName)) {
            return loadedTextures.get(textureName);
        }

        // If not loaded, attempt to load the texture
        Image result;
        try {
            result = new Image(new File(textureName).toURI().toURL().toExternalForm());
            if (result.getHeight() == 0 || result.getWidth() == 0) {
                throw new RuntimeException("Texture not found: " + textureName);
            }
            loadedTextures.put(textureName, result); // cache the texture
        } catch (Exception e) {
            // check if missing_texture.png exists
            try {
                result = new Image(new File(texturesPath + "/missing_texture.png").toURI().toURL().toExternalForm());
                logger.warning(ErrorMsgsEnum.TEXTURE_MISSING.getValue(textureName, e));
            } catch (Exception e2) {
                // the missing texture is also missing, which is kinda bad...
                logger.info(new File(texturesPath + "/missing_texture.png").getAbsolutePath());
                String e3 = ErrorMsgsEnum.TEXTURE_MISSING_IS_MISSING.getValue(textureName, e2);
                logger.warning(e3);
                throw new RuntimeException(e3);
            }
        }
        return result;
    }

    /**
     * Retrieves a list of all textures associated with a given object name.
     * This method checks if the textures are already loaded and returns them if available.
     *
     * @param objectName the name of the object whose textures are to be retrieved
     * @return a list of Image objects representing the textures associated with the object name
     */
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

    /**
     * Retrieves the sizes of all textures associated with a given texture name.
     * This method checks if the sizes are already loaded and returns them if available.
     *
     * @param textures the list of Image objects representing the textures
     * @param textureName the name of the texture whose sizes are to be retrieved
     * @return a list of int arrays representing the sizes of the textures
     */
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