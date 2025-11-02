
package src.commands;

import src.connect.Connect;

/**
 * ICommand : interface de foncteur
 * Chaque classe implémentant cette interface sera un foncteur représentant une commande du protocole P2P
 */
public interface ICommand {

    /*
     * Execute la commande
     */
    public String execute(Connect connect);
}