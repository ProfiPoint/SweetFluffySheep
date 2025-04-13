package cz.cvut.copakond.pinkfluffyunicorn.model.items;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.*;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;

import java.util.HashMap;
import java.util.Map;

public class ItemFactory {
    private static final Map<ItemEnum, Class<? extends Item>> itemMap = new HashMap<>();

    // Static block to register item types to their corresponding classes
    static {
        itemMap.put(ItemEnum.COIN, Coin.class);
        itemMap.put(ItemEnum.FIRE, FireItem.class);
        itemMap.put(ItemEnum.RAINBOW, RainbowItem.class);
        // Add other item types here as needed
    }

    // Create an instance of the appropriate Item subclass based on the ItemEnum type
    public static IItem createItem(ItemEnum itemEnum, double[] position, int duration) {
        Class<? extends Item> itemClass = itemMap.get(itemEnum);

        if (itemClass == null) {
            throw new IllegalArgumentException("Invalid item type: " + itemEnum);
        }

        try {
            // Dynamically create the instance using the constructor that accepts position and duration
            return itemClass.getConstructor(double[].class, int.class).newInstance(position, duration);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create item of type: " + itemEnum, e);
        }
    }
}
