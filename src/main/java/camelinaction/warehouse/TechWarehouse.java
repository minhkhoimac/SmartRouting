package camelinaction.warehouse;
import camelinaction.inventory.InventoryManager;
import camelinaction.model.OrderItem;

public class TechWarehouse implements Warehouse {
    private String name;

    public TechWarehouse(String name) {
        this.name = name;
    }

    @Override
    public String processItem(OrderItem item) {
        InventoryManager inventory = InventoryManager.getInstance();
        int quantity = item.getQuantity();
        int available = inventory.getStock(item.getSku());
        if (available < quantity) {
            return "Insufficient stock for item: " + item.getSku() + " at " + name;
        }
        return "Processed generic item: " + item.getSku() + " at " + name;
    }

    @Override
    public String getName() {
        return name;
    }
}
