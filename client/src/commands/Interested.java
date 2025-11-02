package src.commands;

import src.connect.Connect;
import src.datatypes.BufferMap;
import src.utils.BadResponseException;

public class Interested implements ICommand {

    private String _key; 

    /**
     * Méthode statique publique pour parser la réponse à un interested (have $Key $Buffermap)
     * @param response
     * @return
     */
    public BufferMap parseResponse(CommandParsed response) throws BadResponseException {
        if (!(response.getCommandName().equals("have")
            && response.getArg(1).equals(_key))) throw new BadResponseException("interested " + _key, response);
        String buffAsString = response.getArg(2);
        return new BufferMap(buffAsString);
    }

    Interested(CommandParsed command) throws Exception{
        String key = command.getArg(1);
        _key = key;//TODO? vérifier le format attendu des clefs (base 16 ? 32 caractères ?)

        if(_key == "") throw new Exception("Commande erronée : Clef vide");//vérifier que la clef n'est pas une chaine de caractère vide

    }

    public String getKey(){
        return _key;
    }

    @Override
    public String execute(Connect connect){
        connect.sendMessage("interested " + _key);

        String res = connect.receiveMessage();
        
        return res;
    }

}