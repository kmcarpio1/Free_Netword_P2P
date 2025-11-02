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

public class MockTracker {
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
                        while (true){
                            InputStream inputStream = socket.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                            String input = reader.readLine();// fonction bloquante ici
                            System.out.println("J'ai recu le message suivant : " + input);
                            System.out.println(input.split(" ")[0]);

                            OutputStream outputStream = socket.getOutputStream();
                            PrintWriter writer = new PrintWriter(outputStream, true);

                            if (input.split(" ")[0].equals("announce")) {
                                System.out.println("Renvoie ok");
                                writer.println("ok");
                            } else {
                                System.out.println("Renvoie peers");
                                String key = input.split(" ")[1];
                                writer.println("peers " + key + " [127.0.0.1:2222]");
                            }
                        }
                    } catch (IOException error) {
                        error.printStackTrace();
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
