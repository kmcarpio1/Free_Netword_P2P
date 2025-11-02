package src.connect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Track;

import src.datatypes.IpPort;
import src.client_class.ClientStandard;
import src.config.SigIntHandler;
import java.net.BindException;

import src.logger.Log;

public class Server {
    private static ServerSocket selfEndpoint;
    private static List<Connect> connectedPeers;

    private static Listener listener;
    private static Thread listeningThread;

    private static ClientStandard client;

    private long timeout;

    private Server() {}

    static private void addConnectToPeers(Connect conn){
        System.out.println("New peer connected : " + conn.toString());
        connectedPeers.add(conn);
    }

    static public void removeConnectFromPeers(Connect conn){
        System.out.println("Peer timed out : " + conn.toString());
        connectedPeers.remove(conn);
    }

    static public List<Connect> getPeers(){
        return new ArrayList<Connect>(connectedPeers);
    }

    static public void createServer(int selfPort){
        try {
            connectedPeers = new ArrayList<Connect>();
            selfEndpoint = new ServerSocket(selfPort);

            SigIntHandler.add(selfEndpoint);

            listener = new Listener(selfEndpoint, (conn) -> addConnectToPeers(conn));
            listeningThread = new Thread(listener);
            listeningThread.start();

            System.out.println("Server listening on port " + String.valueOf(selfPort));
    }catch (IOException error) {
        Log.newEntryLog(0,"Server.createServer","client","Listen adress already used !");
        error.printStackTrace();
    }
}

    static public void freeServer(){
        try {
            System.out.println("Socket (serveur) libérée sur le port " + selfEndpoint.getLocalPort());
            SigIntHandler.remove(selfEndpoint);
            selfEndpoint.close();   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
