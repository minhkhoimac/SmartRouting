package camelinaction.warehouse;

import java.util.HashMap;
import java.util.Map;

public class WarehouseRegistry {
    private static final Map<String, Warehouse> warehouses = new HashMap<>();

    static {
        warehouses.put("PRIMARY", new PrimaryWarehouse("Primary Warehouse"));
        warehouses.put("EAST_TECH", new TechWarehouse("East Coast Tech Warehouse"));
        warehouses.put("CENTRAL_CLOTHING", new ClothesWarehouse("Central Apparel Warehouse"));
        warehouses.put("NORTH_HOME", new HomeWarehouse("North Home Warehouse"));
        warehouses.put("SOUTH_ENTERTAINMENT", new EntertainmentWarehouse("South Entertainment Warehouse"));
        warehouses.put("WEST_BOOKS", new BooksWarehouse("West Coast Books Warehouse"));
        warehouses.put("DEFAULT", new PrimaryWarehouse("Out-of-Stock Warehouse"));
    }

    public static Warehouse getWarehouse(String key) {
        return warehouses.get(key);
    }
}
