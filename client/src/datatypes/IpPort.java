package src.datatypes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Classe représentant un couple Ip/Port (identifie une adresse de machine)
 */
public class IpPort {
    //class scope to test them 
    byte[] ip;
    int port;

    /**
     * Constructeur d'un IpPort à partir d'une ip et d'un numéro de port
     * @param IpAsString : Adresse ip pointée sous la forme d'une chaîne de caractères (x.x.x.x)
     * @param p : Port compris entre 0 et 65535
     */
    public IpPort(String IpAsString, int port){
        String[] parsedIp =  IpAsString.split("\\.");
        int n = parsedIp.length;
        if(n!=4){throw  new IllegalArgumentException("Bad Ipv4 format !");}
        byte[] b = new byte[n];
        try {
            for(int i=0;i<n;i++){
                b[i]= (byte) Integer.parseInt(parsedIp[i]);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid IPv4 format: " + e.getMessage());
        }
        if (port < 0 || port > 65535){throw new IllegalArgumentException("Port value out of range !");}
        ip = b;
        this.port = port;
    }

    /**
     * Constructeur d'un IpPort à partir d'un ip:port
     * @param IpPortAsString : Adresse ip pointée et port sous la forme d'une chaîne de caractères (x.x.x.x:yyyy)
     */
    public IpPort(String IpPortAsString){
        this(IpPortAsString.split(":")[0], Integer.parseInt(IpPortAsString.split(":")[1]));
    }

    /**
     * Getter retournant l'ip sous forme d'une InetAddress
     * @return l'ip de l'IpPort sous forme d'une InetAddress
     */
    public InetAddress getInetAddress(){
        InetAddress toReturn = null;
        try{
            
            toReturn = InetAddress.getByAddress(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * Getter retournant le port sous forme d'un entier
     * @return le port sous forme d'un entier
     */
    public int getPort() {
        return port;
    }

    /**
     * Redéfinition de equals pour comparer deux IpPort
     * @param o : Objet à comparer
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
 
        if (!(o instanceof IpPort)) {
            return false;
        }
         
        IpPort c = (IpPort) o;
         
        return c.port == this.port && Arrays.equals(c.ip, this.ip);
    }

    /**
     * Redéfinition de clone pour copier un IpPort
     */
    @Override
    public Object clone() {
        IpPort clone = new IpPort("0.0.0.0:0");
        clone.ip = Arrays.copyOf(this.ip, this.ip.length);
        clone.port = this.port;
        return clone;
    }

    /**
     * Redéfinition de toString pour afficher un IpPort
     */
    @Override
    public String toString() {
        String portStr = Integer.toString(port);
        String[] ipStr = new String[4];
        for (int i=0 ; i<4 ; i++) {
            ipStr[i] = Integer.toString((ip[i]+256)%256);
        }
        return ipStr[0] + "." + ipStr[1] + "." + ipStr[2] + "." + ipStr[3] + ":" +  portStr;
    }

    /**
     * Méthode statique permettant de vérifier la validité d'une chaîne de caractères représentant un IpPort
     * @param inputIpPort : Adresse ip pointée et port se voulant sous la forme d'une chaîne de caractères (x.x.x.x:yyyy)
     * @return : True si le format est valide, False sinon
     */
    public static boolean isValidIpPortString(String inputIpPort){
        // Regular expression to match IP address and port number format
        String regex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}$";

        // Check if the input string matches the regex pattern
        return inputIpPort.matches(regex);
    }

    public static boolean isValidPortInt(int port){
        return (port < 0 || port > 65535);
    }
}