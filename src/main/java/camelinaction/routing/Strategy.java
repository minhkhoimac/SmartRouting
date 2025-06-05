package camelinaction.routing;

import camelinaction.model.*;
import camelinaction.warehouse.Warehouse;

// Strategy pattern
public interface Strategy {
    Warehouse selectWarehouse(OrderItem item);
    String getName();
    void updateInventory(OrderItem item);
}
