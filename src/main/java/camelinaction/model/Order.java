package camelinaction.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Order {
    private String orderId;
    private List<OrderItem> items = new ArrayList<>();
    private String destination;
    private String deliveryMethod;
    private int count;
    private String maxCategory;

    public Order(String orderId, String destination, String deliveryMethod) {
        this.orderId = orderId;
        this.destination = destination;
        this.deliveryMethod = deliveryMethod;
        this.count = 0;
        this.maxCategory = null;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        count++;
        String category = item.getCategory();
        if (maxCategory == null || category.compareTo(maxCategory) > 0) {
            maxCategory = category;
        }
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(String line) {
        int itemsIndex = line.indexOf("items=");
        String items = line.substring(itemsIndex + 7, line.indexOf("], destination="));
        List<OrderItem> itemsList = new ArrayList<>();
        for (String item : items.split("},")) {
            item = item.trim();
            if (!item.endsWith("}")) {
                item += "}";
            }
            OrderItem orderItem = OrderItem.stringToItem(item);
            itemsList.add(orderItem);
        }
        this.items = itemsList;
        this.count = itemsList.size();
        this.maxCategory = null;
        for (OrderItem item : itemsList) {
            String category = item.getCategory();
            if (maxCategory == null || category.compareTo(maxCategory) > 0) {
                maxCategory = category;
            }
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDestination() {
        return destination;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public int getCount() {
        return count;
    }

    public String getMaxCategory() {
        return maxCategory;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", items=" + items +
                ", destination='" + destination + '\'' +
                ", deliveryMethod='" + deliveryMethod + '\'' +
                '}';
    }

    public Map<String, Integer> getCategoryCount() {
        Map<String, Integer> categoryCount = new HashMap<>();
        for (OrderItem item : items) {
            String category = item.getCategory();
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }
        return categoryCount;
    }

    public static Order stringToOrder(String line) {
        int orderIdIndex = line.indexOf("orderId=");
        String orderId = line.substring(orderIdIndex + 9, line.indexOf(", items=") - 1);
        int destinationIndex = line.indexOf("destination=");
        String destination = line.substring(destinationIndex + 13, line.indexOf(", deliveryMethod=") - 1);
        int deliveryMethodIndex = line.indexOf("deliveryMethod=");
        String deliveryMethod = line.substring(deliveryMethodIndex + 16, line.lastIndexOf("}") - 1);
        
        Order order = new Order(orderId, destination, deliveryMethod);
        System.out.println("Parsed Order: " + order);
        return order;
    }
}
