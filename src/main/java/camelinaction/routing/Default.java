package camelinaction.routing;

import camelinaction.inventory.InventoryManager;
import camelinaction.model.OrderItem;
import camelinaction.warehouse.Warehouse;
import camelinaction.warehouse.WarehouseRegistry;

public class Default implements Strategy {

    @Override
    public Warehouse selectWarehouse(OrderItem item) {
        switch (item.getCategory().toLowerCase()) {
            case "technology":
                return WarehouseRegistry.getWarehouse("EAST_TECH");
            case "apparel":
                return WarehouseRegistry.getWarehouse("CENTRAL_CLOTHING");
            case "home":
                return WarehouseRegistry.getWarehouse("NORTH_HOME");
            case "entertainment":
                return WarehouseRegistry.getWarehouse("SOUTH_ENTERTAINMENT");
            case "books":
                return WarehouseRegistry.getWarehouse("WEST_BOOKS");
            default:
                return WarehouseRegistry.getWarehouse("DEFAULT");
        }
    }

    public String getName() {
        return "DEFAULT";
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
