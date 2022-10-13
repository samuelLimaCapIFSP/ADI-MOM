package com.adi.middleware.middleware.jms.mom;

import javax.jms.*;
import javax.naming.InitialContext;

public class TesteProdutorTopico2 {

    public static void main(String[] args) throws Exception {

        InitialContext context = new InitialContext();
        var topic = (Topic) context.lookup("loja");
        TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup("ConnectionFactory");

        TopicConnection connection = factory.createTopicConnection("user","senha");
        connection.start();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        var publisher = session.createPublisher(topic);
        var textMessage = session.createTextMessage("<pedido><id>222</id></pedido>");
        textMessage.setBooleanProperty("ebook", false);
        publisher.publish(textMessage);

        session.close();
        connection.close();
        context.close();
    }
}

