package src.connect;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import src.datatypes.IpPort;

class Consumer implements Runnable {
    private final DataQueue dataQueue;
    private boolean running = false;
    private Socket socket;

    private long timeout;

    public Consumer(DataQueue dataQueue, Socket socket, long timeout) {
        this.dataQueue = dataQueue;
        this.socket = socket;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        running = true;
        consume();
    }

    public void consume() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (running) {
                if(dataQueue.isEmpty()) {
                    try {
                        dataQueue.waitIsNotEmpty();
                    } catch (InterruptedException e) {
                        // log.severe("Error while waiting to Consume messages.");
                        break;
                    }
                    dataQueue.notifyIsNotEmpty();
                }
                if (!running) {
                    break;
                }
                Message message = dataQueue.poll(timeout, TimeUnit.SECONDS);//bloquant dans le thread d'envois
                
                out.println(message.toString());
            }
            // log.info("Consumer Stopped");
        } catch (IOException e) {
            // log.error("Socket creation/reading error " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        dataQueue.notifyIsNotEmpty();
    }
}
