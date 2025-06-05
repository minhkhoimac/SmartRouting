package camelinaction.routing;

import camelinaction.inventory.InventoryManager;
import camelinaction.model.OrderItem;
import camelinaction.warehouse.Warehouse;
import camelinaction.warehouse.WarehouseRegistry;

public class Inventory implements Strategy {
    private final InventoryManager inventory;

    public Inventory(InventoryManager inventory) {
        this.inventory = inventory;
    }

    @Override
    public Warehouse selectWarehouse(OrderItem item) {
        boolean inStock = inventory.isInStock(item.getSku(), item.getQuantity());
        return inStock ? WarehouseRegistry.getWarehouse("PRIMARY") : WarehouseRegistry.getWarehouse("DEFAULT");
    }

    public String getName() {
        return "INVENTORY";
    }

    public void updateInventory(OrderItem item) {
        InventoryManager inventory = InventoryManager.getInstance();
        if (inventory.isInStock(item.getSku(), item.getQuantity())) {
            inventory.updateStock(item.getSku(), -item.getQuantity());
        } else {
            throw new IllegalStateException("Item not in stock: " + item.getSku());
        }
    }
}
