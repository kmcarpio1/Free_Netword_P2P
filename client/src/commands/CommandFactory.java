
package src.commands;

import java.util.List;

import src.client_class.Client;

import java.util.ArrayList;

/**
 * Patron de conception de la fabrique
 * Génère un foncteur de commande à partir d'une commande parsée
 * Possède des fonctions pour caractériser une commande
 */
public class CommandFactory {

    private static final String[] _toTracker = {"announce", "look", "getfile", "update"};
    private static final String[] _toPeer = {"interested", "getpieces", "have"};

    private CommandFactory(){}

    /**
     * Vérifie qu'une commande soit destinée au tracker
     * @param command : La commande parsée à vérifier
     * @return "True" si la commande est destinée au tracker, "False" sinon
     */
    public static boolean isCommandToTracker(CommandParsed command){
        String commandName = command.getCommandName();
        for (String cmd : _toTracker) {
            if (cmd.equals(commandName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie qu'une commande soit destinée à un autre pair
     * @param command : La commande parsée à vérifier
     * @return "True" si la commande est destinée à un autre pair, "False" sinon
     */
    public static boolean isCommandToPeer(CommandParsed command){
        String commandName = command.getCommandName();
        for (String cmd : _toPeer) {
            if (cmd.equals(commandName)) {
                return true;
            }
        }
        return false;
    }

    public static ICommand createCommand(CommandParsed command) throws Exception {
        switch (command.getCommandName()) {
            case "announce":
                return (new Announce(command));
            case "look":
                return (new Look(command));
            case "getfile":
                return (new GetFile(command));
            case "interested":
                return (new Interested(command));
            case "getpieces":
                return (new GetPieces(command));
            case "have":
                return (new Have(command));
            case "update":
                return (new Update(command));
            default:
                throw new Exception("Commande non reconnue");
        }
    }

    public static ICommand createHook(CommandParsed command, Client client) throws Exception {
        switch (command.getCommandName()) {
            case "interested":
                return (new OnInterested(command, client));
            case "getpieces":
                return (new OnGetPieces(command, client));
            default:
                throw new Exception("Commande non reconnue");
        }
    }
}