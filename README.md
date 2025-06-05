# OO Architecture Final Project

## E-Commerce Warehouse Routing System

When consumers order a variety of products online, often times the items are not all in the same place when ordered. E-commerce businesses often maintain multiple geographically distributed warehouses to reduce shipping time and costs as well as satisfy customer needs. This project implements a mock-system that simulated the routing of orders to warehouses based on the items ordered and their locations.

## Design patterns used (details below):
- **Facade**: in `OrderRouteFacade`
- **Strategy**: in `router/Strategy`
- **Singleton**: in `inventory/InventoryManager`
- **Template Method**: in `Warehouse`
- **Observer**: in `inventory/InventoryObserver`

## Enterprise Integration Patterns used (details below):
- **Point-to-Point Channel**: in `router/OrderIngestion`
- **Message Translator**: in `router/OrderIngestion`
- **Event-Driven Consumer**: in `router/OrderRouting`
- **Filter**: in `router/OrderRouting`
- **Content-Based Router**: in `router/OrderRouting`
- **Invalid Message Queue**: in `router/OrderRouting`
- **Dead Letter Channel**: in `router/OrderRouting`
- **Message Broker, Message, Message Endpoint, Message Channel, Pipes & Filters**: in `router/Order_`


## Orders
The `orders` folder contains the inputs and outputs of the system. The `orders/incoming` folder contains a list of CSV files which represent a submitted order placed by a consumer. The `orders/processed` folder contains the output of the system, which is a text file summarizing the order as well as the warehouses that each item in the order was routed to.

The first line of the incoming CSV files is a header row, containing two entries: `REGION` and `DELIVERY_METHOD`. The `REGION` is the geographical region of the consumer and can be `EAST, WEST, NORTH, SOUTH, CENTRAL`. The `DELIVERY_METHOD` is the method of delivery chosen by the consumer and can be `STANDARD, EXPRESS`.

The subsequent lines of the CSV files contain the items ordered by the consumer, with each line containing two entries: `ITEM_SKU` and `QUANTITY`. The `ITEM_SKU` is the stock keeping unit of the item ordered, and the `QUANTITY` is the number of units of that item ordered.

## Model
The "order" and "order item" models are defined in `model/Order` and `model/OrderItem`, respectively. They contain logic for keeping information about the order as it passes through the system as well as translation functions to and from Strings. 

`CatalogLoader` is a static class that loads the inventory catalog with a list of items, their SKUs, and the categories that they belong to. The possible categories are `TECHNOLOGY, APPAREL, HOME, BOOKS, ENTERTAINMENT`. 

## Inventory Manager
The inventory manager is a singleton class that manages the inventory of items in the system. The singleton pattern is used here to ensure that there is only one instance of the inventory (manager) throughout the system. The inventory manager provides functionality of adjusting the inventory of items, checking if an item is in stock, and notifying observers when the inventory of an item changes (used in `WarehouseNotifier`).

## Warehouses
The `Warehouse` class is an interface that implements the template method pattern for the differen types of warehouses for the e-commerce company. The types of warehouses are `BooksWarehouse`, `ClothesWarehouse`, `EntertainmentWarehouse`, `HomeWarehouse`, `PrimaryWarehouse`, and `TechWarehouse`. Each one implements the `processItem` method, which "processes" each item differently. For example, the `BooksWarehouses` "checks ISBN" and the book quality. 

The `WarehouseNotifier` class is an observer that is notified when the inventory of an item changes. It prints out these changes to the console. In further implementations, this could be used to notify the user if an item is low in stock or out of stock, or notify the warehouse manager to restock the item.

The `WarehouseRegistry` class is another static singleton that manages the warehouses in the system.

## Router
The `routing` package contains the logic for routing orders to warehouses based on the items ordered and their locations. It implements the strategy pattern to allow for different routing strategies to be used. The different strategies are based on `Category`, `Region`, `Inventory`, and `Default`. 

The `Category` strategy routes items to warehouses if the user requests an `EXPRESS` delivery and the most common category of items in the order accounts for more than 50% of the order. The `Region` strategy routes items to warehouses based on the region if the user requests an `EXPRESS` delivery and the most common category of items in the order accounts for less than 50% of the order. The `Inventory` strategy routes all items to the `PRIMARY` central warehouse if the user requests a `STANDARD` delivery and if it is in stock as well as the most common category of items in the order accounts for more than 50% of the order. If it is not in stock, it routes to the `DEFAULT` warehouse, which "stores" items that are not in stock. This shouldn't happen though, as when items are out of stock, the order should be sent to a dead-message queue and the user should post a new order. Lastly, the `Default` strategy routes all items to where they are originally stored, based on category. This occurs when the user requests a `STANDARD` delivery and in all other cases.  The logic for strategy selection is in `routing/Router`.

## EIPs and Message Routing
The system pipeline takes place using three routes: `OrderIngestion`, `OrderRouting`, and `OrderProcessing`. The first route, `OrderIngestion`, is responsible for reading the CSV files in `orders/incoming`, which represents each order. The orders are formed and are written to strings and sent the the `ORDER_INGESTION` queue in ActiveMQ. It contains logic for a dead-letter channel, point-to-point queue, a message translator from CSV to `Order` objects to strings. The next route, `OrderRouting`, is an event-driven consumer that consumes the messages from the `ORDER_INGESTION` queue and routes them to several queues depending on the delivery method. It contains logic for an event-driven consumer, a content-based router (for `EXPRESS` and `STANDARD` delivery), a filter (for non-null bodies), a dead-letter channel, and an invalid message queue. The last route(s), in `OrderProcessing` consumes the messages from the `EXPRESS` and `STANDARD` queues and processes them using the routing strategies defined in `routing/Router`. It handles message translation from strings to `Order` and returns the output of the routing to `orders/processed` as a text file summarizing the order and the warehouses that each item was routed to. 

## Facade
The `OrderRouteFacade` class implements the facade pattern to provide a simple interface for the system, running the entire routing process with a single method call. It also initializes the inventory and the warehouses in the system and loads in the inventory. 

## Running the System
The system can be run by executing the `Main` class in Eclipse as a Maven project. 

## Future Improvements
Given the forwarded deadline, there are many improvements that can be made to the system.
- Implement a user interface for the system to allow users to place orders and view their order history
- Implement a user class to manage user accounts and authentication
- Differentiate the warehouses to have more specialized functions when processing items using the factory pattern. The factory pattern would allow for more flexibility in managing and adding warehouses in the future without specifically creating new classes for each warehouse
- Allow routes that provide channels for the user to submit reviews of items. This can be implemented using a publish-subscribe channel, where users would post reviews to a channel and various parts of the system would subscribe to that channel to receive the reviews. A potential future database portion of the system could store these reviews, the UI could display them, and the warehouses can use the reviews to improve their processing of items or suppliers. 
- The routing process can be implemented using a chain-of-responsibility pattern, where each step in order processing is decoupled because high coupling leads to some future design issues.
- A splitter and aggregator can be used to split the order into individual items and aggregate the results of the routing process after each item is processed
- Physical orders can be represented using the composite pattern, where items of an order can be routed to different warehouses and then joined together to form more complete orders