package com.adi.middleware.middleware.jms.mom;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DurableSubscriberOne {

    public static void main(String[] args) {
        DurableSubscriberOne durableSubscriber = new DurableSubscriberOne();
        durableSubscriber.receiveMessage();
    }

    public void receiveMessage() {
        InitialContext initialContext = null;
        TopicConnectionFactory connectionFactory;
        TopicConnection connection = null;
        TopicSubscriber subscriber;
        TopicSession session = null;
        Topic topic;

        try {
            // Step 1. Create an initial context to perform the JNDI lookup.
            initialContext = new InitialContext();

            // Step 2. Look-up the JMS topic
            topic = (Topic) initialContext.lookup("loja");

            // Step 3. Look-up the JMS Topic connection factory
            connectionFactory = (TopicConnectionFactory) initialContext.lookup("ConnectionFactory");

            // Step 4. Create a JMS Topic connection
            connection = connectionFactory.createTopicConnection();

            // Step 5. Set the client-id on the connection
            // in case of non-durable subscriber, please remove the below line
            connection.setClientID("estoque");

            // step 6. Start the connection
            connection.start();

            // step 7. Create Topic session
            session = connection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);

            // step 8. Create the durable subscriber
            // in case of non-durable subscriber, please use the below commented line
            // subscriber=session.createSubscriber(topic);
            subscriber = session.createDurableSubscriber(topic, "durableSubscriber");

            // Step 9. Consume the message
            Message message = subscriber.receive();

            if (message != null && message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println(
                        "DurableSubscriber received a message published by Publisher : " + textMessage.getText());
            } else if (message == null) {
                System.out.println(
                        "DurableSubscriber fails to receive the message sent by the publisher due to a timeout.");
            } else {
                throw new JMSException("Message must be a type of TextMessage");
            }
        } catch (JMSException | NamingException ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (initialContext != null) {
                try {
                    initialContext.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
