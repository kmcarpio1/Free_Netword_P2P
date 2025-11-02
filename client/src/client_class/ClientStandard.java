package src.client_class;

import java.util.HashMap;
import java.util.HashSet;

import src.commands.CommandFactory;
import src.commands.ICommand;
import src.commands.Interested;
import src.config.ConfigClient;
import src.commands.CommandParsed;
import src.datatypes.BufferMap;
import src.datatypes.FileDescription;
import src.datatypes.IpPort;
import src.datatypes.Key;
import src.utils.FileChecker;
import src.utils.MultiThreadOperations;
import src.connect.Connect;
import src.connect.Server;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import src.logger.Log;

/**
 * Classe qui porte la logique coeur de l'échange de données pair-à-pair
 */
public abstract class ClientStandard implements Client {
    private Connect toTracker;
    private List<Connect> peers;

    private List<FileDescription> leeching;
    private List<FileDescription> seeding;

    private Updater updater;

    public ClientStandard(Connect toTracker){
        this.toTracker = toTracker;
        peers = Server.getPeers();

        leeching = new ArrayList<FileDescription>();
        seeding = new ArrayList<FileDescription>();

        updateFiles();
        updater = new Updater(this, (peer) -> orSetPeers(peer), () -> updateFiles());
    }
    private void orSetPeers(List<Connect> newPeers){
        Set<Connect> union = new HashSet<>(peers);
        union.addAll(newPeers);
        List<Connect> unionList = new ArrayList<>(union);
        peers = unionList;
    }

    public void updateFiles() {
        try {
            seeding = FileChecker.updateFiles(FileChecker.scanForFiles(), seeding);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Méthode publique permettant de récupérer un fichier demandé par le client
     * @param key la clé du fichier que le client souhaite récupérer
     * @return le FileDescription du dit fichier, null s'il ne souhaite pas le récupérer
     */
    @Override
    public FileDescription getSeed(Key key){
        updateFiles();
        for (FileDescription fileDescription : seeding) {
            if (fileDescription.getSelfHash().equals(key)){
                return fileDescription;
            }
        }
        return null;
    }
    

    /**
     * Méthode publique permettant de récupérer tous les fichiers possédés
     * @return tous les FileDescription du client
     */
    @Override
    public List<FileDescription> getSeeds(){
        return new ArrayList<FileDescription>(seeding);
    }

    /**
     * Méthode publique permettant de récupérer tous les fichiers demandés
     * @return tous les FileDescription demandés par client
     */
    @Override
    public List<FileDescription> getLeech(){
        return new ArrayList<FileDescription>(leeching);
    }
    
    /**
     * Getter des peers (variable copiée)
     * @return 
     */
    public List<Connect> getPeers(){
        return new ArrayList<Connect>(peers);
    }

    /**
     * Setter des peers (utilisé comme callback dans le Updater)
     * @param newPeers
     */
    private void setPeers(List<Connect> newPeers){
        peers = newPeers;
    }

    /**
     * Méthode permettant au client de se connecter aux IpPorts fournis
     * @param ipPorts
     */
    @Override
    public void connectTo(List<IpPort> ipPorts){
        for(int i=0;i<ipPorts.size();i++){
            Log.newEntryLog(0, "ClientStandard.connectTo", "client", "IpPort I want to connect : " + ipPorts.get(i).toString());
        }
        List<Connect> newPeers = new ArrayList<Connect>();
        for (IpPort ip : ipPorts) {
            Connect newConn = new Connect();
            newConn.createConnection(ip);
            newPeers.add(newConn);
        }
        setPeers(Stream.concat(peers.stream(), newPeers.stream()).collect(Collectors.toList()));
    }

    /***********************************************************/
    /*************** PARTIE MAP-FILTER-REDUCE ******************/
    /***********************************************************/

    /**
     * Méthode MAP (MULTITHREAD) pour envoyer le interested à tous les pairs et récupérer leurs buffermaps
     * @param interested la commande interested à exécuter (contient la clé du fichier d'intérêt)
     * @param peers les pairs à qui exécuter le interested
     * @param bufferMaps les buffermaps à récupérer
     * @return la fonction callback à multithreader
     */
    abstract protected Consumer<Integer> mapOnPeers(ICommand interested, List<Connect> peers, List<BufferMap> out);

    /**
     * Méthode FILTER pour vérifier et mettre sous forme de dictionnaire les information reçues par un autre client sous forme d'une commande data
     * @param command : la commande reçue
     * @param key : la clef de fichier recherchée
     * @return la liste des couples Connect/BufferMap retenus
     */
    abstract protected HashMap<Connect,BufferMap> filterBuffermaps(List<BufferMap> bufferMaps, List<Connect> peerList, BufferMap mask);

    /**
     * Méthode REDUCE (MULTITHREAD) pour récupérer les pièces à chaque pair et reconstruire le fichier
     * @param peersToLeech les pairs à qui exécuter le getPiece
     * @param file le fichier à reconstruire
     * @return la fonction callback à multithreader
     */
    abstract protected Consumer<Connect> reduceOnPeers(HashMap<Connect,BufferMap> peersToLeech, int pieceSize, FileDescription file);


    /**
     * Méthode interne
     * Effectue Map-Filter-Reduce sur tous les pairs connectés
     * Multithread les portions Map et Reduce
     * 
     * Map : pour tout pair, le client lui envoie "interested" et récupère son buffermap
     * Filter : le client filtre tous les buffermaps de manière à remplir au maximum son buffermap
     * Reduce : pour tout pair, le client lui envoie "getpieces" et récupère les données pour reconstruire le fichier
     * @param peers Tous les pairs connectés
     * @param interested La ICommande
     * @param file Le fichier à remplier
     */
    private void innerMapReduce(List<Connect> peers, ICommand interested, FileDescription file) throws Exception{
        List<BufferMap> bufferMaps = new ArrayList<BufferMap>(Collections.nCopies(peers.size(), (BufferMap) null));
        file.open();
        
        try {
            // Map (MULTITHREAD)
            Consumer<Integer> setBufferMapForPeer = mapOnPeers(interested, peers, bufferMaps);
            MultiThreadOperations.forThread(peers, setBufferMapForPeer); 
            int pieceSize = bufferMaps.get(0).getPieceSize();
            int bufferSize = bufferMaps.get(0).size();
            // Filter
            if (!(file.hasBufferMap())){
                file.createBufferMap(bufferSize, pieceSize); //on appelle createBufferMap avec la taille du premier bufferMap de bufferMaps et la taille des pieces associées
            }
            HashMap<Connect,BufferMap> peersToLeech = filterBuffermaps(bufferMaps, peers, file.getBufferMap()); //peersToLeech est le dictionnaire des clients et des buffer des pieces qui nous interessent chez eux
            
            // Reduce (MULTITHREAD)
            Consumer<Connect> writePieceOfPeer = reduceOnPeers(peersToLeech, pieceSize, file);
            MultiThreadOperations.forEachThread(peersToLeech.keySet(), writePieceOfPeer);
            
        } catch (Exception e) { 
            e.printStackTrace();
        } finally {
            file.close();
        }
    
    }


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
    @Override
    public void mapReducePeers(CommandParsed commandParsed, FileDescription file) throws Exception{
        ICommand command = CommandFactory.createCommand(commandParsed);

        List<Connect> peers = new ArrayList<Connect>(this.peers);
        innerMapReduce(peers, command, file);
    }


    /**
     * Lance un Map-Filter-Reduce sur tous les pairs connectés
     * 
     * Map : pour tout pair, le client lui envoie "interested" et récupère son buffermap
     * Filter : le client filtre tous les buffermaps de manière à remplir au maximum son buffermap
     * Reduce : pour tout pair, le client lui envoie "getpieces" et récupère les données pour reconstruire le fichier
     * @param commandParsed la commande parsée
     */
    @Override
    public void mapReducePeers(CommandParsed commandParsed) throws Exception{
        ICommand command = CommandFactory.createCommand(commandParsed);
        String key = null;
        if (command instanceof Interested){
            key = ((Interested) command).getKey();
            
        }

        // Crée un nouveau fichier dans lequel on va stocker toutes les pieces
        // N'a pas pour l'instant de BufferMap
        FileDescription file = new FileDescription(key, ConfigClient.newpath, false); 

        List<Connect> peers = new ArrayList<Connect>(this.peers);
        innerMapReduce(peers, command, file);
    }
}
