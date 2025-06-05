package camelinaction.routing;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.management.JmxNotificationEventNotifier;
import org.apache.camel.spi.EventNotifier;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camelinaction.model.OrderItem;
import camelinaction.model.CatalogLoader;
import camelinaction.model.Order;


public class OrderIngestion {
    public void configure() throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();
        System.out.println("Starting Camel context...");

        // connect to ActiveMQ JMS broker listening on localhost on port 61616
        ConnectionFactory connectionFactory = 
        	new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        JmxNotificationEventNotifier notifier = new JmxNotificationEventNotifier();
        List<EventNotifier> eventNotifier = new ArrayList<EventNotifier>();
        eventNotifier.add(notifier);
        context.getManagementStrategy().addEventNotifier(notifier);
        

        // EIP: point-to-point channel
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("file:orders/incoming?noop=true")
                .errorHandler(deadLetterChannel("activemq:queue:ORDER_ERROR")
                    .maximumRedeliveries(3)
                    .redeliveryDelay(1000))
                // EIP: message translator
                .unmarshal().csv()
                .process(exchange -> {
                    @SuppressWarnings("unchecked")
                    List<List<String>> csvLines = (List<List<String>>) exchange.getIn().getBody(List.class);
                    List<OrderItem> items = new ArrayList<>();
                    String orderId = UUID.randomUUID().toString();
                    boolean firstLine = true;
                    String region = "";
                    String deliveryMethod = "standard";
                    exchange.getOut().setHeader("negQuantity", false);
                    exchange.getOut().setHeader("unknownSku", false);
                    for (List<String> line : csvLines) {
                        if (firstLine) {
                            firstLine = false;
                            region = line.get(0);
                            deliveryMethod = line.get(1);
                            continue;
                        }
                        String sku = line.get(0);
                        String category = CatalogLoader.getCategory(sku);
                        if (category == null) {
                            exchange.getOut().setHeader("unknownSku", true);
                            continue;
                        }
                        int quantity = Integer.parseInt(line.get(1));
                        if (quantity < 0) {
                            exchange.getOut().setHeader("negQuantity", true);
                            continue;
                        }
                        OrderItem item = new OrderItem(orderId, sku, category, region, quantity);
                        items.add(item);
                    }
                    Order order = new Order(orderId, region, deliveryMethod);
                    items.forEach(order::addItem);

                    exchange.getOut().setBody(order.toString());
                    exchange.getOut().setHeader("orderId", orderId);
                    exchange.getOut().setHeader("deliveryMethod", deliveryMethod);
                })
                .to("jms:queue:ORDER_INGESTION");
            }
        });

        context.start();

        Thread.sleep(10000);

        context.stop();
    }
}
