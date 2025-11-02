package src.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import src.client_class.Client;
import src.config.ConfigClient;
import src.connect.Connect;
import src.datatypes.FileDescription;
import src.datatypes.Key;

/**
 * Classe foncteur représentant le hook de "getpieces"
 * Cette classe est instanciée lorsque notre client RECOIT un "getpieces"
 * Elle sert à renvoyer la réponse à la dite requête
 */
public class OnGetPieces implements ICommand {
    private Client client;

    private Key key;
    List<Integer> indexes;

    /**
     * Constructeur du foncteur
     * @param command La commande parsée du "getpieces" reçu
     * @param client Le client possédant les fichiers à envoyer
     * @throws Exception Erreur de parsing
     */
    OnGetPieces(CommandParsed command, Client client) throws Exception {
        this.client = client;

        // Beginning of parsing
        String keyAsString = command.getArg(1);
        if (keyAsString == "" || keyAsString == null) throw new Exception("Commande reçue erronée : pas de clé à récupérer dans " + command.getCommandName());

        key = new Key(keyAsString, null);

        String allIndexes = command.getArg(2);
        if (allIndexes == "" || allIndexes == null) throw new Exception("Commande reçue erronée : pas de clé à récupérer dans " + command.getCommandName());

        // Parsing all indexes
        String[] stringArray = allIndexes.split(" ");
        indexes = new ArrayList<Integer>();
        for (String str : stringArray) {
            indexes.add(Integer.parseInt(str));
        }
    }

    /**
     * Envoie au pair les couples indice:partie_de_fichier correspondant à la requête
     * précédente
     */
    @Override
    public String execute(Connect connect){
        
        // On récupère les informations sur le fichier à envoyer
        FileDescription fileToSend = client.getSeed(key);
        fileToSend.open();

        String message = "data " + key.toString() + " [";

        try {
            for (int i=0 ; i<indexes.size() ; i++){
                message += Integer.toString(indexes.get(i));
                message += ":";
                message += "\"";
                message += fileToSend.getPiece(indexes.get(i), ConfigClient.getPieceSize()).replaceAll("\"", "\\\'\\\'");
                message += "\"";
                if(i!=indexes.size()-1) message+= " ";
            }
            message += "]";
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileToSend.close();
        connect.sendMessage(message);
        
        return null;
    }
}