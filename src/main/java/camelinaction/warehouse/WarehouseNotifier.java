package camelinaction.warehouse;

import camelinaction.inventory.InventoryObserver;

public class WarehouseNotifier implements InventoryObserver {
    private String name;
    private int quantity;

    public WarehouseNotifier(String name) {
        this.name = name;
    }

    @Override
    public void onInventoryChange(String sku, int newQuantity) {
        this.quantity = newQuantity;
        System.out.println("Warehouse " + name + " notified of inventory change: " + sku + " - New Quantity: " + newQuantity);
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
