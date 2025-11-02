package src.connect;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

import src.datatypes.IpPort;
import src.config.SigIntHandler;
import src.logger.Log;

/*
Socket possède 4 opérations :
- CreateConnection
- SendMessage
- OnReceiveMessage (événement)
- FreeConnection


- announce appelle CreateConnection
- toute autre fonction appelle SendMessage
- tout événement capté de l'extérieur appelle OnReceiveMessage
- la fin du programme appelle FreeConnection
*/


public class Connect {
    private Socket clientSocket;

    private static int MAX_QUEUE_CAPACITY = 100;
    private DataQueue dataQueueToSend;
    private DataQueue dataQueueReceived;

    private Consumer consumeToSend;
    private Producer produceReceived;

    private Thread sendThread;
    private Thread receiveThread;

    private long timeout;

    public Connect(long _timeout) {
        dataQueueToSend = new DataQueue(MAX_QUEUE_CAPACITY);
        dataQueueReceived = new DataQueue(MAX_QUEUE_CAPACITY);
        timeout = _timeout;
    }

    public Connect(){
        this(60);
    }

    /**
     * Creates a connection and starts thread to listen for incoming data.
     * 
     * @param ip The IP address and port to connect to.
     */
    public void createConnection(IpPort ipPortDest){
        try {
            Log.newEntryLog(0, "Connect.createConnection", "client", "IpPort I want to connect : " + ipPortDest.toString());
            Log.newEntryLog(0, "Connect.createConnection", "client", "Inet adress : " + ipPortDest.getInetAddress().toString());
            Log.newEntryLog(0, "Connect.createConnection", "client", "Port : " + ipPortDest.getPort());
            clientSocket = new Socket(ipPortDest.getInetAddress(), ipPortDest.getPort());

            SigIntHandler.add(clientSocket);

            consumeToSend = new Consumer(dataQueueToSend, clientSocket, timeout);
            produceReceived = new Producer(dataQueueReceived, clientSocket);

            sendThread = new Thread(consumeToSend);//thread d'envois
            receiveThread = new Thread(produceReceived);//thread de reception

            sendThread.start();
            receiveThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a connection and starts a server thread to listen for incoming data.
     * 
     * @param ip The IP address and port to connect to.
     */
    public void createConnection(Socket socket){
        try {
            clientSocket = socket;
            SigIntHandler.add(clientSocket);

            consumeToSend = new Consumer(dataQueueToSend, clientSocket, timeout);
            produceReceived = new Producer(dataQueueReceived, clientSocket);

            sendThread = new Thread(consumeToSend);//thread d'envois
            receiveThread = new Thread(produceReceived);//thread de reception

            sendThread.start();
            receiveThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        Message mess = new Message(message, null);
        dataQueueToSend.add(mess);
    }

    public void requeueMessage(String message){
        Message mess = new Message(message, null);
        try {
            dataQueueReceived.put(mess);   
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage(){
        Message mess = dataQueueReceived.poll(timeout,  TimeUnit.SECONDS);//bloquante dans le thread de reception
        return mess.toString();
    }

    public void freeConnection() {
        produceReceived.stop();
        consumeToSend.stop();
        try {
            clientSocket.close();
            System.out.println("Connexion libérée sur le port " + clientSocket.getLocalPort());
            SigIntHandler.remove(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return "<" + clientSocket.toString() + ">";
    }

    public String displayDataQueues(){
        return "\n Received :" + dataQueueReceived.toString() + 
                "\n To send :" + dataQueueToSend + 
                "\n";
    }

}
