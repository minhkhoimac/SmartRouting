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

public class OrderRouting {
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

        // EIP: event-driven consumer
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("activemq:queue:ORDER_INGESTION")
                // EIP: dead-letter channel
                .errorHandler(deadLetterChannel("activemq:queue:ORDER_ERROR")
                    .maximumRedeliveries(3)
                    .redeliveryDelay(1000))
                .log("Received raw order message: ${body}")
                // EIP: filter
                .filter(body().isNotNull())
                // EIP: content-based router
                .choice()
                    .when(simple("${headers.deliveryMethod} == 'EXPRESS' && ${headers.negQuantity} == false && ${headers.unknownSku} == false"))
                        .log("Processing EXPRESS order")
                        .to("activemq:queue:ORDER_EXPRESS")
                    .when(simple("${headers.deliveryMethod} == 'STANDARD' && ${headers.negQuantity} == false && ${headers.unknownSku} == false"))
                        .log("Processing STANDARD order")
                        .to("activemq:queue:ORDER_STANDARD")
                    // EIP: invalid message queue
                    .otherwise()
                        .log("Processing INVALID order")
                        .to("activemq:queue:INVALID_ORDER");
            }
        });

        // Start the Camel context
        context.start();
        Thread.sleep(5000);
        context.stop();
    }
}
