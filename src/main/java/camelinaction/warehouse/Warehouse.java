package camelinaction.warehouse;

import camelinaction.model.*;

// Template method pattern
public interface Warehouse {
    String processItem(OrderItem item);
    String getName();
}
