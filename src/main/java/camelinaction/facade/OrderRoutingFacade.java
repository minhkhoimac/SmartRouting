package camelinaction.facade;

import camelinaction.inventory.InventoryManager;
import camelinaction.model.CatalogLoader;
import camelinaction.routing.*;
import camelinaction.warehouse.WarehouseNotifier;

// Facade pattern
public class OrderRoutingFacade {
    public static void run() throws Exception {
        InventoryManager inventory = InventoryManager.getInstance();
        inventory.addObserver(new WarehouseNotifier("Main Notifier"));
        
        // Load the catalog
        CatalogLoader.loadCatalogAndInventory(inventory);

        // Start the order ingestion process
        OrderIngestion orderIngestion = new OrderIngestion();
        orderIngestion.configure();

        OrderRouting orderRouting = new OrderRouting();
        orderRouting.configure();

        OrderProcessing orderProcessing = new OrderProcessing();
        orderProcessing.configure();
    }
}
