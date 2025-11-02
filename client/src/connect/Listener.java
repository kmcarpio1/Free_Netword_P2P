package src.connect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.Function;

import src.connect.Connect;

class Listener implements Runnable {
    private boolean running = false;
    private ServerSocket selfEndpoint;
    private Consumer<Connect> callback;

    public Listener(ServerSocket endpoint, Consumer<Connect> callback){
        selfEndpoint = endpoint;
        this.callback = callback;
    }

    @Override
    public void run() {
        running = true;
        listen();
    }

    public void listen() {
        try {
            while (running) {
                Socket soc = selfEndpoint.accept();
                Connect conn = new Connect();
                conn.createConnection(soc);
                callback.accept(conn);
            }
            // log.info("Consumer Stopped");
        } catch (IOException e) {
            // log.error("Socket creation/reading error " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }
}
