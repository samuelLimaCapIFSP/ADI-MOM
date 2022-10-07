package com.adi.middleware.middleware.jms;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Scanner;

public class TesteConsumidor {
    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext context = new InitialContext();

        ConnectionFactory cf = (ConnectionFactory) context.lookup("ConnectionFactory");
        Connection conexao = cf.createConnection();

        conexao.start();
        Session session = conexao.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination fila = (Destination) context.lookup("financeiro");
        MessageConsumer consumer = session.createConsumer(fila);

        Message message = consumer.receive();

        System.out.println("Recebendo msg: " + message);

        new Scanner(System.in).nextLine();

        session.close();
        conexao.close();
        context.close();
    }
}
