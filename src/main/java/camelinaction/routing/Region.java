package camelinaction.routing;

import camelinaction.inventory.InventoryManager;
import camelinaction.model.OrderItem;
import camelinaction.warehouse.Warehouse;
import camelinaction.warehouse.WarehouseRegistry;

public class Region implements Strategy {
    @Override
    public Warehouse selectWarehouse(OrderItem item) {
        switch (item.getRegion().toLowerCase()) {
            case "east": return WarehouseRegistry.getWarehouse("EAST_TECH");
            case "west": return WarehouseRegistry.getWarehouse("WEST_BOOKS");
            case "central": return WarehouseRegistry.getWarehouse("CENTRAL_CLOTHING");
            case "north": return WarehouseRegistry.getWarehouse("NORTH_HOME");
            case "south": return WarehouseRegistry.getWarehouse("SOUTH_ENTERTAINMENT");
            default: return WarehouseRegistry.getWarehouse("DEFAULT");
        }
    }

    public String getName() {
        return "REGION";
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
