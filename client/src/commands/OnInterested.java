package src.commands;

import src.client_class.Client;
import src.connect.Connect;
import src.datatypes.BufferMap;
import src.datatypes.FileDescription;
import src.datatypes.Key;

/**
 * Classe foncteur représentant le hook de "interested"
 * Cette classe est instanciée lorsque notre client RECOIT un "interested"
 * Elle sert à renvoyer la réponse à la dite requête
 */
public class OnInterested implements ICommand {
    private Client client;

    private Key key;
    
    /**
     * Constructeur du foncteur
     * @param command La commande parsée du "interested" reçu
     * @param client Le client possédant les fichiers à envoyer
     * @throws Exception Erreur de parsing
     */
    OnInterested(CommandParsed command, Client client) throws Exception {
        this.client = client;
        
        // Beginning of parsing
        String keyAsString = command.getArg(1);
        if (keyAsString == "" || keyAsString == null) throw new Exception("Commande reçue erronée : pas de clé à récupérer dans " + command.getCommandName());

        key = new Key(keyAsString, null);
    }

    /**
     * Envoie au pair le buffermap correspondant à la clé sur laquelle est construite le foncteur
     */
    @Override
    public String execute(Connect connect){

        // On récupère les informations sur le fichier à envoyer
        FileDescription fileToSend = client.getSeed(key);

        // Si on n'a pas le fichier, on renvoie un buffermap de 0, sinon on 
        // construit le message retour
        if (fileToSend == null) {
            connect.sendMessage("have " + key.toString() + " 0/0");
        }
        String message = "have " + key.toString() + " " + fileToSend.getBufferMap().toString();
        
        // On envoie notre réponse au pair interessé

        connect.sendMessage(message);

        // OnInterested renvoie une réponse, et n'attend donc pas de réponse
        // => Pas de connect.receiveMessage, on renvoie null
        return null;
    }
}
