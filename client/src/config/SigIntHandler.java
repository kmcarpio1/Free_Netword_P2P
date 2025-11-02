package src.config;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

/**
 * Classe statique servant à la réception à la volée de SIGINT (Ctrl+C)
 */
public final class SigIntHandler {
    private static List<Socket> allSockets;
    private static List<ServerSocket> allServerSockets;

    static {
        allSockets = new ArrayList<Socket>();
        allServerSockets = new ArrayList<ServerSocket>();
    }

    private SigIntHandler(){}

    public static void listenForSigInt() {
        System.out.println("\n LISTENING FOR SHUTDOWNS \n");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("  Shutdown hook ran!\n");
                try {
                    for (ServerSocket serverSocket : allServerSockets) {
                        serverSocket.close();
                    }
                    for (Socket socket : allSockets) {
                        socket.close();
                    }

                    printProgramEnd();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Ajoute une socket à la liste des socket à libérer sur interruption
     * @param socket
     */
    public static void add(Socket socket) {
        allSockets.add(socket);
    }

    /**
     * Ajoute une serverSocket à la liste des socket serveurs à libérer sur interruption
     * @param socket
     */
    public static void add(ServerSocket serverSocket) {
        allServerSockets.add(serverSocket);
    }

    /**
     * Retire une socket à la liste des socket à libérer sur interruption
     * @param socket
     */
    public static void remove(Socket socket) {
        allSockets.remove(socket);
    }

     /**
     * Retire une serverSocket à la liste des socket serveurs à libérer sur interruption
     * @param socket
     */
    public static void remove(ServerSocket serverSocket) {
        allServerSockets.remove(serverSocket);
    }

    /**
     * Affiche la fin du programme
     */
    public static void printProgramEnd(){
        System.out.println("*************************************");
        System.out.println("********** EXITING PROGRAM **********");
        System.out.println("*************************************");
    }
}
