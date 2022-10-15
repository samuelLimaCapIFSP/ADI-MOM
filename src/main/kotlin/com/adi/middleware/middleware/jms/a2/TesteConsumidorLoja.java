package com.adi.middleware.middleware.jms.a2;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.Scanner;

public class TesteConsumidorLoja {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        InitialContext context = new InitialContext();
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

        Connection connection = factory.createConnection("guest","senha");
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination fila = (Destination) context.lookup("loja");
        MessageConsumer consumer = session.createConsumer(fila);

        consumer.setMessageListener(new MessageListener() {

            @Override
            public void onMessage(Message message) {

                TextMessage textMessage = (TextMessage) message;
                try {
                    //message.acknowledge();
                    System.out.println(textMessage.getText());

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }

        });


        new Scanner(System.in).nextLine();

        session.close();
        connection.close();
        context.close();
    }
}

