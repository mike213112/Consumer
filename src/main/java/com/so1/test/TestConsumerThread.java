/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.so1.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.core.MediaType;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author mmendez
 */
public class TestConsumerThread extends Thread {

    private String threadId;
    private long millis;

    public TestConsumerThread(String threadId, long millis) {
        this.threadId = threadId;
        this.millis = millis;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    @Override
    public void run() {
        String nombreCola = "queue.so1.demo";
        String nombreServicio = "EjemploCola_" + threadId;
        String serverLocation = "failover:(tcp://172.17.0.2:61616)?timeout=3000";

        try {

            //MessageConsumer consumer = QueueUtil.getMessageConsumer(serverLocation, nombreCola, nombreServicio);
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(serverLocation);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(nombreCola);

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            while (true) {
                try {

                    Message message = consumer.receive(1000);

                    
                    // extraccion de datos en cola
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();
                        System.out.println("[" + threadId + "]Received: " + text);
                        
                        // mm. 21102017 codigo de conversion json a obj persona
                        ObjectMapper mapper = new ObjectMapper();
                        Persona objetoPersona = mapper.readValue(text, Persona.class);
//
                        System.out.println("Nombre:" + objetoPersona.getNombre());
//
//                        // mm. 21102017 codigo de envio de obj persona a tomcat (consumo de restful en tomcat)
                        Persona response = new Persona();
                        try {
                            Client client;
                            client = Client.create();
                            client.setConnectTimeout(10000);
                            client.setReadTimeout(60000);

                            WebResource service = client
                                    .resource("http://172.17.0.3:8080/ServicioWeb/rest/enviarDatos");
                            WebResource servicio = client
                                    .resource("http://185.168.1.10/ServicioWeb/rest/enviarDatos");

                            response = service
                                    .type(MediaType.APPLICATION_JSON)
                                    .post(Persona.class, objetoPersona);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (response != null) {
                            System.out.println("id:" + response.getId());
                            System.out.println("Nombre:" + response.getNombre());
                            System.out.println("edad:" + response.getEdad());
                        }

                    } else {
                        System.out.println("[" + threadId + "]Received: " + message);
                    }

                    //Thread.sleep(millis);
//                    Message message = consumer.receive(1000);
//                    procesarMensaje(message);
//                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesarMensaje(Message message) {

        if (message instanceof ActiveMQTextMessage) {
            ActiveMQTextMessage msg = (ActiveMQTextMessage) message;

            try {
                System.out.println("[" + threadId + "] mensaje:" + msg.getText());

            } catch (Exception e) {
                System.out.println("[" + threadId + "]" + "Mensaje no puede ser leido ..");
            }

        } else {
            System.out.println("[" + threadId + "]" + "Se desconoce el formato de mensaje..." + message);
        }

    }

}
