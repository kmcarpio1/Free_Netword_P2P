package tst.mocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MockTrackerNoAns {
    private final static int port = 6666;

    public static void main(String[] args){

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);   
            System.out.println("Running mock tracker on port " + String.valueOf(port));

            int count = 600000;
            List<Socket> sockets = new ArrayList<Socket>();
            while (count>0){
                
                System.out.println("wainting 1 ");
                Socket socket = serverSocket.accept();//bloquant
                System.out.println("New client connected");

                Thread clientThread = new Thread(() -> {
                    try {
                        InputStream inputStream = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                        String input = reader.readLine();// fonction bloquante ici

                        System.out.println("J'ai recu le message suivant : " + input);
                        Thread.sleep(1000000000);
                    } catch (IOException error) {
                        error.printStackTrace();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                });
                clientThread.start();

                count--;
            }
            System.out.println("Fin du serveur");
            for (Socket sock : sockets) {
                sock.close();
            }
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
