package src.commands;

import src.connect.Connect;
import java.util.Arrays;

public class Look implements ICommand {

    private String commandAsIs;
    private String[] _criterions; 

    Look(CommandParsed command) throws Exception{
        String criterionsArg = command.getArg(1);

        if(criterionsArg.isEmpty()) throw new Exception("Commande erronée : Aucun argument");

        String[] l = criterionsArg.split("="); 

        if(!(l[0].equals("filename"))) throw new Exception("Commande erronée : Filtre non reconnu");

        commandAsIs = command.getOriginalCommand();
    }

    @Override
    public String execute(Connect connect){
        connect.sendMessage(commandAsIs);

        String res = connect.receiveMessage();
        
        return res;
    }

}

//TODO ?? le cas ou on a seulement un filename ou un filesize ? ou ça peut marcher ? 