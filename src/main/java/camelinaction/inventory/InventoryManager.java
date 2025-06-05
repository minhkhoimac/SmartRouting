package camelinaction.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Singleton pattern
public class InventoryManager {
    private Map<String, Integer> stock = new HashMap<>();
    private List<InventoryObserver> observers = new ArrayList<>();

    private static InventoryManager instance;
    private InventoryManager() { }

    public static synchronized InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public void addObserver(InventoryObserver observer) {
        observers.add(observer);
    }

    public void updateStock(String sku, int change) {
        int currentQty = stock.getOrDefault(sku, 0);
        int qty = currentQty + change;
        if (qty < 0) {
            throw new IllegalArgumentException("Cannot reduce stock below zero for SKU: " + sku);
        }
        stock.put(sku, qty);
        for (InventoryObserver obs : observers) {
            obs.onInventoryChange(sku, qty);
        }
    }

    public boolean isInStock(String sku, int qty) {
        return stock.getOrDefault(sku, 0) >= qty;
    }

    public int getStock(String sku) {
        return stock.getOrDefault(sku, 0);
    }
}
