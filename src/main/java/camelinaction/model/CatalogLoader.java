package camelinaction.model;

import java.util.ArrayList;
import java.util.List;

import camelinaction.inventory.InventoryManager;;

public class CatalogLoader {
    public static final List<OrderItem> CATALOG = new ArrayList<>();

    public static String getCategory(String sku) {
        for (OrderItem item : CATALOG) {
            if (item.getSku().equals(sku)) {
                return item.getCategory();
            }
        }
        return null;
    }

    public static void loadCatalogAndInventory(InventoryManager manager) {
        // Technology
        add("SKU1001", "Laptop", "technology", "east", 20, manager);
        add("SKU1002", "Smartphone", "technology", "central", 15, manager);
        add("SKU1003", "Bluetooth Speaker", "technology", "west", 25, manager);
        add("SKU1004", "Wireless Mouse", "technology", "east", 30, manager);
        add("SKU1005", "Mechanical Keyboard", "technology", "central", 12, manager);
        add("SKU1006", "USB-C Charger", "technology", "west", 40, manager);
        add("SKU1007", "External Hard Drive", "technology", "central", 18, manager);
        add("SKU1008", "Webcam", "technology", "east", 22, manager);
        add("SKU1009", "Router", "technology", "west", 16, manager);
        add("SKU1010", "Noise Cancelling Headphones", "technology", "central", 10, manager);

        // Apparel
        add("SKU2001", "T-Shirt", "apparel", "east", 50, manager);
        add("SKU2002", "Jeans", "apparel", "central", 40, manager);
        add("SKU2003", "Jacket", "apparel", "west", 35, manager);
        add("SKU2004", "Sneakers", "apparel", "east", 45, manager);
        add("SKU2005", "Cap", "apparel", "central", 55, manager);
        add("SKU2006", "Sweater", "apparel", "west", 28, manager);
        add("SKU2007", "Scarf", "apparel", "east", 33, manager);
        add("SKU2008", "Socks", "apparel", "central", 60, manager);
        add("SKU2009", "Gloves", "apparel", "west", 27, manager);
        add("SKU2010", "Belt", "apparel", "central", 38, manager);

        // Books
        add("SKU3001", "Cookbook", "books", "west", 30, manager);
        add("SKU3002", "Sci-Fi Novel", "books", "central", 35, manager);
        add("SKU3003", "Biography", "books", "east", 40, manager);
        add("SKU3004", "History Book", "books", "west", 25, manager);
        add("SKU3005", "Textbook - Math", "books", "central", 20, manager);
        add("SKU3006", "Fantasy Novel", "books", "east", 28, manager);
        add("SKU3007", "Self-help Guide", "books", "west", 32, manager);
        add("SKU3008", "Science Journal", "books", "central", 24, manager);
        add("SKU3009", "Travel Book", "books", "east", 19, manager);
        add("SKU3010", "Mystery Novel", "books", "west", 36, manager);

        // Entertainment
        add("SKU4001", "Board Game", "entertainment", "east", 22, manager);
        add("SKU4002", "Puzzle", "entertainment", "central", 18, manager);
        add("SKU4003", "Action DVD", "entertainment", "west", 26, manager);
        add("SKU4004", "Comedy DVD", "entertainment", "central", 30, manager);
        add("SKU4005", "Video Game", "entertainment", "east", 14, manager);
        add("SKU4006", "Card Game", "entertainment", "west", 25, manager);
        add("SKU4007", "Remote Car", "entertainment", "central", 19, manager);
        add("SKU4008", "Doll", "entertainment", "east", 21, manager);
        add("SKU4009", "Toy Train", "entertainment", "west", 23, manager);
        add("SKU4010", "Magic Kit", "entertainment", "central", 20, manager);

        // Home
        add("SKU5001", "Vacuum Cleaner", "home", "west", 10, manager);
        add("SKU5002", "Coffee Maker", "home", "central", 18, manager);
        add("SKU5003", "Lamp", "home", "east", 25, manager);
        add("SKU5004", "Toaster", "home", "central", 15, manager);
        add("SKU5005", "Microwave", "home", "west", 9, manager);
        add("SKU5006", "Cutlery Set", "home", "east", 34, manager);
        add("SKU5007", "Air Purifier", "home", "central", 11, manager);
        add("SKU5008", "Blender", "home", "west", 20, manager);
        add("SKU5009", "Rice Cooker", "home", "east", 13, manager);
        add("SKU5010", "Iron", "home", "central", 17, manager);

        // Additional Mixed Items
        for (int i = 5011; i <= 5100; i++) {
            String sku = "SKU" + i;
            String name = "Item" + i;
            String category = i % 5 == 0 ? "home" : i % 4 == 0 ? "entertainment" : i % 3 == 0 ? "books" : i % 2 == 0 ? "apparel" : "technology";
            String region = i % 3 == 0 ? "east" : i % 3 == 1 ? "central" : "west";
            int quantity = (i % 10 + 1) * 3;
            add(sku, name, category, region, quantity, manager);
        }
    }

    private static void add(String sku, String name, String category, String region, int quantity, InventoryManager manager) {
        CATALOG.add(new OrderItem("0", sku, category, region, 1));
        manager.updateStock(sku, quantity);
    }
}
