package camelinaction.model;

public class OrderItem {
    private String orderId;
    private String sku;
    private String category;
    private String region;
    private int quantity;

    public OrderItem(String orderId, String sku, String category, String region, int quantity) {
        this.orderId = orderId;
        this.sku = sku;
        this.category = category;
        this.region = region;
        this.quantity = quantity;
    }

    public String getSku() { 
        return sku; 
    }
    
    public String getCategory() { 
        return category; 
    }

    public String getRegion() { 
        return region; 
    }

    public int getQuantity() { 
        return quantity; 
    }

    // to string
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId='" + orderId + '\'' +
                ", sku='" + sku + '\'' +
                ", category='" + category + '\'' +
                ", region='" + region + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    public static OrderItem stringToItem(String line) {
        String[] parts = line.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid line format: " + line);
        }
        String orderId = parts[0].trim();
        orderId = orderId.substring(orderId.indexOf("'") + 1, orderId.lastIndexOf("'"));
        String sku = parts[1].trim();
        sku = sku.substring(sku.indexOf("'") + 1, sku.lastIndexOf("'"));
        String category = parts[2].trim();
        category = category.substring(category.indexOf("'") + 1, category.lastIndexOf("'"));
        String region = parts[3].trim();
        region = region.substring(region.indexOf("'") + 1, region.lastIndexOf("'"));
        String quantityStr = parts[4].trim().split("=")[1];
        quantityStr = quantityStr.substring(0, quantityStr.indexOf("}"));
        int quantity = Integer.parseInt(quantityStr);
        System.out.println("Parsed OrderItem: " + orderId + ", " + sku + ", " + category + ", " + region + ", " + quantity);
        return new OrderItem(orderId, sku, category, region, quantity);
    }
}
