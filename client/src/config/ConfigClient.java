package src.config;

import src.datatypes.IpPort;
import src.logger.Log;
/**
 * Classe statique servant à la gestion de la configuration du client
 */
public final class ConfigClient {

    private static IpPort trackerIpPort;
    private static int listenPort;
    private static int pieceSize = 2048; //faire set get
    public static String newpath = "./files/";


    private ConfigClient() {
    }

    /**
     * Setter de pieceSize
     * @param : nouvelle pieceSize du client
     */
    public static void setPieceSize(int size){
        pieceSize = size;
    }

    /**
     * Getter de pieceSize
     * @return pieceSize du client
     */
    public static int getPieceSize(){
        return pieceSize;
    }

    /**
     * Getter de l'IpPort du tracker (serveur)
     * @return IpPort du serveur
     */
    public static IpPort getTracker() {
        return (IpPort) trackerIpPort.clone();
    }

    /**
     * Setter de l'IpPort du tracker (serveur)
     */
    public static void setTracker(IpPort ipPort) {
        trackerIpPort = ipPort;
    }

    /**
     * Getter du port d'écoute du client (self)
     * @return port d'écoute
     */
    public static int getListenPort(){
        return listenPort;
    }

    

    /**
     * Setter du port d'écoute du client (self)
     */
    public static void setListenPort(int port){
        listenPort = port;
    }

}
