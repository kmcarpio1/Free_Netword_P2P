package src.utils;

import src.commands.CommandParsed;

/**
 * Exception dédiée au projet indiquant une mauvaise réponse de la part d'un acteur du réseau (tracker ou pair)
 */
public class BadResponseException extends Exception {

    /**
     * Constructeur par défaut : aucune indication
     */
    public BadResponseException() {
        super("La réponse reçue est incorrecte.");
    }

    /**
     * Constructeur indiquant à quelle requête la mauvaise réponse fait echo
     */
    public BadResponseException(String request, CommandParsed response) {
        super("La réponse reçue à " + request + " est " + response.getCommandName() );
    }

    /**
     * Constructeur indiquant la mauvaise réponse
     */
    public BadResponseException(CommandParsed response) {
        super("La réponse reçue est " + response.getCommandName() );
    }

    /**
     * Constructeur affichant un message custom
     */
    public BadResponseException(String message) {
        super(message);
    }

    /**
     * Constructeur chaînant les exceptions avec message
     */
    public BadResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur chaînant les exceptions sans message
     */
    public BadResponseException(Throwable cause) {
        super("La réponse reçue est incorrecte.", cause);
    }
}