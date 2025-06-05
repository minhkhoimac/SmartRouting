package camelinaction.inventory;

public interface InventoryObserver {
    void onInventoryChange(String sku, int newQuantity);
}
