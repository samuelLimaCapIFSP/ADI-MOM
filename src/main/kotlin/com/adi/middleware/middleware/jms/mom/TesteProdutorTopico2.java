package com.adi.middleware.middleware.jms.mom;

import javax.jms.*;
import javax.naming.InitialContext;

public class TesteProdutorTopico2 {

    public static void main(String[] args) throws Exception {

        InitialContext context = new InitialContext();
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination topico = (Destination) context.lookup("loja");
        MessageProducer producer = session.createProducer(topico);

        Message message = session.createTextMessage("<pedido><id>222</id></pedido>");
        message.setBooleanProperty("ebook", true);
        producer.send(message);

        session.close();
        connection.close();
        context.close();
    }
}

