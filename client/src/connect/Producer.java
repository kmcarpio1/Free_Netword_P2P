package src.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import src.datatypes.IpPort;

class Producer implements Runnable {
    private final DataQueue dataQueue;
    private boolean running = false; 
    private Socket clientSocket;

    public Producer(DataQueue dataQueue, Socket socket) {
        this.dataQueue = dataQueue;
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        running = true;
        produce();
    }

    public void produce() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (running) {
                if(dataQueue.isFull()) {
                    try {
                        dataQueue.waitIsNotFull();
                    } catch (InterruptedException e) {
                        // log.severe("Error while waiting to Produce messages.");
                        break;
                    }
                    dataQueue.notifyIsNotFull();
                }
                if (!running) {
                    break;
                }
                
                Message message = new Message(in.readLine(), new IpPort(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort()));
                if (message.toString() != null) dataQueue.add(message);
            }
            // log.info("Producer Stopped");
        } catch (IOException e) {
            // log.error("Socket(s) creation/reading error " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        dataQueue.notifyIsNotFull();
    }
}