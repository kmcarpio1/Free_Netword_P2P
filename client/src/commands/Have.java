package src.commands;

import src.connect.Connect;

public class Have implements ICommand {

    private String _key; 
    private String _buffer_map;

    Have(CommandParsed command) throws Exception{
        String key = command.getArg(1);
        _key = key;
        if(_key == "") throw new Exception("Commande erronée : Clef vide");//vérifier que la clef n'est pas une chaine de caractère vide
        

        String buffer_map = command.getArg(2);
        _buffer_map = buffer_map;
        if(_buffer_map == "") throw new Exception("Commande erronée : Buffer map vide");//vérifier que le buffer map n'est pas une chaine de caractère vide

    }

    @Override
    public String execute(Connect connect){
        return null;
    }

}