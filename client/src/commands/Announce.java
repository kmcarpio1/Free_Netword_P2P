package src.commands;

import java.util.List;
import java.util.ArrayList;

import src.config.ConfigClient;
import src.connect.Connect;
import src.datatypes.FileDescription;


public class Announce implements ICommand {

    private String _port;
    private FileDescription[] _files_share; // Tableau des fichiers qui peuvent être fournis
    private String[] _keys_files_download; // Tableau des clés des fichiers en cours de téléchargement

    private String commandAsIs;

    Announce(CommandParsed command)  throws Exception{

        // Initialisation des fichiers à fournir    
        _port = command.getArg("listen");

        if(_port == "") throw new Exception("Commande erronée : Mot clef \"listen\" innexistant");

        try {
            int numberPort = Integer.parseInt(_port);
            if(numberPort < 0 || numberPort > 65535) throw new NumberFormatException();
        } catch(NumberFormatException e) {
            throw new Exception("Commande erronée : port d'écoute non valide");
        }

        // Initialisation des fichiers à fournir    
        String seedArg = command.getArg("seed");
        if(seedArg == "") throw new Exception("Commande erronée : Mot clef \"seed\" innexistant");

        String [] tf = seedArg.equals("") ? new String[0] : seedArg.split(" ");
        if(tf.length%4 != 0) throw new Exception("Commande erronée : Nombre d'arguments pour \"seed\" incorrect");

        List<FileDescription> temp = new ArrayList<>();

        int i = 0;
        while(i < tf.length) {
            String fileName = tf[i++];
            if(fileName == "") throw new Exception("Commande erronée : Nom d'un fichier vide");

            String length = tf[i++];
            try {
                Integer.parseInt(length);
            } catch(NumberFormatException e) {
                throw new Exception("Commande erronée : longueur d'un fichier non valide");
            }

            String pieceSize = tf[i++];
            try {
                Integer.parseInt(pieceSize);
            } catch(NumberFormatException e) {
                throw new Exception("Commande erronée : longueur de découpe d'un fichier non valide");
            }

            String key = tf[i++]; //TODO? vérifier le format attendu des clefs (base 16 ? 32 caractères ?)
            if(key == "") throw new Exception("Commande erronée : Clef de fichier à fournir vide");//vérifier que la clef n'est pas une chaine de caractère vide
            
            temp.add(new FileDescription(fileName, Integer.parseInt(length), ConfigClient.newpath, Integer.parseInt(pieceSize), true));
        }
        
        _files_share = temp.toArray(new FileDescription[0]);

        // Initialisation des fichiers en cours de téléchargement
        String leechArg = command.getArg("leech");
        _keys_files_download = leechArg.split(" ");
        for(int k=0;k<_keys_files_download.length; k++){
            if(_keys_files_download[k] == "") throw new Exception("Commande erronée : Clef de fichier de téléchargment vide");//vérifier que la clef n'est pas une chaine de caractère vide
        }

        commandAsIs = command.toString();
    }

    @Override
    public String execute(Connect connect){
        connect.sendMessage(commandAsIs);

        String res = connect.receiveMessage();
        
        return res;
    }

}