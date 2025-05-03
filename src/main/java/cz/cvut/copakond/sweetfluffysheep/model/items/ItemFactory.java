package cz.cvut.copakond.sweetfluffysheep.model.items;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Factory class for creating items.
 * This class uses the Factory Method pattern to create instances of different item types.
 * It registers item types and their corresponding classes in a map and provides a method to create items.
 */
public class ItemFactory {
    private static final Logger logger = Logger.getLogger(ItemFactory.class.getName());

    private static final Map<ItemEnum, Class<? extends Item>> itemMap = new HashMap<>();

    // item register of types and their corresponding classes
    static {
        itemMap.put(ItemEnum.COIN, Coin.class);
        itemMap.put(ItemEnum.FREEZE, FreezeItem.class);
        itemMap.put(ItemEnum.RAGE, RageItem.class);
    }

    /**
     * This method creates an instance of the specified item type.
     * It uses reflection to create the instance based on the item type.
     *
     * @param itemEnum The type of item to create.
     * @param position The position of the item in the game world.
     * @param duration The duration of the item effect.
     *
     * @return An instance of the specified item type.
     */
    public static IItem createItem(ItemEnum itemEnum, double[] position, int duration) {
        Class<? extends Item> itemClass = itemMap.get(itemEnum);

        if (itemClass == null) {
            throw new IllegalArgumentException("Invalid item type: " + itemEnum);
        }

        try {
            // dynamically create the instance using the constructor that accepts position and duration
            // used for the creation of the item
            return itemClass.getConstructor(double[].class, int.class).newInstance(position, duration);
        } catch (Exception e) {
            logger.severe("Failed to create item of type: " + itemEnum + " - " + e.getMessage());
            throw new RuntimeException("Failed to create item of type: " + itemEnum, e);
        }
    }
}