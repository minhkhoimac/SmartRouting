package camelinaction.routing;

import camelinaction.model.Order;
import camelinaction.model.OrderItem;
import camelinaction.warehouse.Warehouse;
import java.util.HashMap;
import java.util.Map;
import camelinaction.inventory.*;

public class Router {
    private final Strategy strategy;

    private final static HashMap<String, Strategy> strategies = new HashMap<>();

    static {
        strategies.put("CATEGORY", new Category(""));
        strategies.put("REGION", new Region());
        strategies.put("INVENTORY", new Inventory(InventoryManager.getInstance()));
        strategies.put("DEFAULT", new Default());
    }

    public Router(Strategy strategy) {
        this.strategy = strategy;
    }

    public static Strategy getStrategy(String strategyType) {
        return strategies.get(strategyType);
    }

    public Warehouse routeOrder(OrderItem item) {
        return strategy.selectWarehouse(item);
    }

    private static Strategy chooseStrat(Order order) {
        Map<String, Integer> categoryCount = order.getCategoryCount();
        // get the category with the highest count
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
            }
        }
        int itemCount = order.getCount();
        if (maxCount / itemCount >= 0.5 && order.getDeliveryMethod().equals("EXPRESS")) {
            return getStrategy("CATEGORY");
        } else if (order.getDeliveryMethod().equals("EXPRESS")) {
            return getStrategy("REGION");
        } else if (maxCount / itemCount >= 0.5 && order.getDeliveryMethod().equals("STANDARD")) {
            return getStrategy("INVENTORY");
        } else {
            return getStrategy("DEFAULT");
        }
    }

    public static String routeOrder(Order order) {
        Strategy strat = chooseStrat(order);
        String result = String.format("Order %s routed using %s strategy\n", order.getOrderId(), strat.getName());
        System.out.println("ChosenStrategy: " + strat.getName());
        if (strat.getName().equals("CATEGORY")) {
            ((Category) strat).setCategory(order.getMaxCategory());
            result += String.format("Category: %s\n", ((Category) strat).getCategory());
        }
        for (OrderItem item : order.getItems()) {
            Warehouse warehouse = strat.selectWarehouse(item);
            String res = warehouse.processItem(item);
            strat.updateInventory(item);
            System.out.println("Order ID: " + order.getOrderId() + ", Item SKU: " + item.getSku() + ", Result: " + res);
            result += String.format("Item SKU: %s, quantity: %d, result: %s\n", 
                                    item.getSku(), item.getQuantity(), res);

        }
        return result;
    }
}
