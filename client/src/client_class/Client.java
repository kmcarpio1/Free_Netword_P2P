package src.client_class;

import java.util.List;

import src.commands.CommandParsed;
import src.connect.Connect;
import src.datatypes.FileDescription;
import src.datatypes.IpPort;
import src.datatypes.Key;

/**
 * Classe qui porte la logique coeur de l'échange de données pair-à-pair
 */
public interface Client {

    /**
     * Méthode permettant au client de se connecter aux IpPorts fournis
     * @param ipPorts
     */
    public void connectTo(List<IpPort> ipPorts);

    /**
     * Méthode publique permettant de récupérer un fichier possédé par le client
     * @param key la clé du fichier que le client possède
     * @return le FileDescription du dit fichier, null s'il ne souhaite pas le récupérer
     */
    public FileDescription getSeed(Key key);

    /**
     * Méthode publique permettant de récupérer tous les fichiers possédés
     * @return tous les FileDescription du client
     */
    public List<FileDescription> getSeeds();

    /**
     * Méthode publique permettant de récupérer tous les fichiers demandés
     * @return tous les FileDescription demandés par le client
     */
    public List<FileDescription> getLeech();

    /**
     * Lance un Map-Filter-Reduce sur tous les pairs connectés
     * Ici, on met en paramètre un FileDescription (il est donc possible que le fichier soit deja en partie rempli, ou non)
     * 
     * Map : pour tout pair, le client lui envoie "interested" et récupère son buffermap
     * Filter : le client filtre tous les buffermaps de manière à remplir au maximum son buffermap
     * Reduce : pour tout pair, le client lui envoie "getpieces" et récupère les données pour reconstruire le fichier
     * @param commandParsed la commande parsée
     * @param file le fichier déjà en partie rempli
     */
    public void mapReducePeers(CommandParsed commandParsed, FileDescription file) throws Exception;


    /**
     * Lance un Map-Filter-Reduce sur tous les pairs connectés
     * 
     * Map : pour tout pair, le client lui envoie "interested" et récupère son buffermap
     * Filter : le client filtre tous les buffermaps de manière à remplir au maximum son buffermap
     * Reduce : pour tout pair, le client lui envoie "getpieces" et récupère les données pour reconstruire le fichier
     * @param commandParsed la commande parsée
     */
    public void mapReducePeers(CommandParsed commandParsed) throws Exception;
}
