package camelinaction.routing;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.management.JmxNotificationEventNotifier;
import org.apache.camel.spi.EventNotifier;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;

import java.util.ArrayList;
import java.util.List;

import camelinaction.model.Order;

public class OrderProcessing {
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

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("activemq:queue:ORDER_STANDARD")
                .errorHandler(deadLetterChannel("activemq:queue:ORDER_ERROR")
                    .maximumRedeliveries(3)
                    .redeliveryDelay(1000))
                .log("Processing STANDARD order: ${body}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String orderStr = exchange.getIn().getBody(String.class);
                        Order order = Order.stringToOrder(orderStr);
                        order.setItems(orderStr);
                        System.out.println(order);
                        System.out.println("Processing STANDARD order with ID: " + order.getOrderId());
                        String result = Router.routeOrder(order);

                        exchange.getOut().setBody(result);
                    }
                })
                .to("file:orders/processed?fileName=standard-${date:now:yyyyMMdd-HHmmssSSS}.txt");

                from("activemq:queue:ORDER_EXPRESS")
                .log("Processing EXPRESS order: ${body}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String orderStr = exchange.getIn().getBody(String.class);
                        Order order = Order.stringToOrder(orderStr);
                        order.setItems(orderStr);
                        System.out.println(order);
                        System.out.println("Processing EXPRESS order with ID: " + order.getOrderId());
                        String result = Router.routeOrder(order);

                        exchange.getOut().setBody(result);
                    }
                })
                .to("file:orders/processed?fileName=express-${date:now:yyyyMMdd-HHmmssSSS}.txt");
            }
        });

        context.start();
        Thread.sleep(5000);
        context.stop();
    }
}
