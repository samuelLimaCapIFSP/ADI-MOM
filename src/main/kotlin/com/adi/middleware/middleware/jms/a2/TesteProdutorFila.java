package com.adi.middleware.middleware.jms.a2;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.Random;
import java.util.Scanner;

public class TesteProdutorFila {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {

        // Fila LOG
        InitialContext context = new InitialContext();
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

        Connection filaLogConnection = factory.createConnection("user","senha");
        filaLogConnection.start();
        Session logSession = filaLogConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination filaLog = (Destination) context.lookup("LOG");

        // Fim inicialização fila LOG

        //Topico conexão
        var topicoLoja = (Topic) context.lookup("loja");

        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) context.lookup("ConnectionFactory");

        TopicConnection topicConnection = topicConnectionFactory.createTopicConnection("guest","senha");
        topicConnection.start();
        TopicSession topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        //Fim inicialização topico conexão
        var producerLog = logSession.createProducer(filaLog);
        var publisherLoja = topicSession.createPublisher(topicoLoja);

        var textPromotionDiasDasCriancas = "<promocao>Dia das criancas</promocao>";
        String[] textPromotions = new String[]{
                textPromotionDiasDasCriancas,
                "<promocao>Dia dos avós</promocao>",
                "<promocao>Dia dos avôs</promocao>",
                "<promocao>Dia de ninguém</promocao>",
        };

        var prefixPedido = "<pedido>";
        var suffixPedido = "</pedido>";


        var random = new Random();
        for (int i = 0; i < 1000; i++) {
            var choosedLog = LogInfo.values()[random.nextInt(4)];

            var choosedPromotion = textPromotions[random.nextInt(4)];
            var textIdPedido = "<id>" + i + "</id>";
            var textMessage = prefixPedido +
                    choosedPromotion +
                    textIdPedido + suffixPedido;


            if (choosedLog == LogInfo.NONE && choosedPromotion.equals(textPromotionDiasDasCriancas)) {
                var promotionMessage = topicSession.createTextMessage(textMessage);
                System.out.println("enviou");
                publisherLoja.send(promotionMessage);
            } else {
                var logDetails = " | Apache ActiveMQ 5.12.0 (localhost, ID:Mac-mini-de-IFSP.local-49701-1443131721783-0:1) is starting";
                var logMessage = logSession.createTextMessage(choosedLog + logDetails);
                var deliveryMode = DeliveryMode.NON_PERSISTENT;
                var priority = -1;

                switch (choosedLog) {
                    case ERR:
                        priority = 9;
                        break;
                    case DEBUG:
                        priority = 4;
                        break;
                    case WARN:
                        priority = 1;
                        break;
                    default:
                        break;

                }

                if (priority != -1) {
                    System.out.println("enviou com LOG");
                    producerLog.send(logMessage, deliveryMode, priority, 5000);
                }
            }
        }

        new Scanner(System.in).nextLine();

        logSession.close();
        topicSession.close();
        topicConnection.close();
        filaLogConnection.close();
        context.close();
    }
}
