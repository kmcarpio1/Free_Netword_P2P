package src.commands;

import java.util.ArrayList;
import java.util.List;

import src.connect.Connect;
import src.datatypes.IpPort;
import src.utils.BadResponseException;
import src.logger.Log;
import src.commands.CommandParsed;

public class GetFile implements ICommand {
    private String _key;
    private String commandAsIs;

    public List<IpPort> parseResponse(CommandParsed response) throws BadResponseException {
        // insert login here
        Log.newEntryLog(0, "GetFile.parseResponse", "client", "command parsed is: " + response.toString());


        if (!(response.getCommandName().equals("peers")
            && response.getArg(1).equals(_key))) {
                Log.newEntryLog(0, "GetFile.parseResponse", "client", "getcommandname malfunction");
                throw new BadResponseException(commandAsIs, response);
            }

        List<IpPort> toReturn = new ArrayList<IpPort>();
        for (int i=2 ; i<response.size() ; i++){
            toReturn.add(new IpPort(response.getArg(i)));
        }
        //debug purpose
        for(int i=0;i<toReturn.size();i++){
            Log.newEntryLog(0, "GetFile.parseResponse", "client", "getting the elements of the ipPortList : " + toReturn.get(i).toString());
        }
        //end debug 
        return toReturn;

    }

    GetFile(CommandParsed command) throws Exception {
        String key = command.getArg(1);
        _key = key;
        if(_key == "") throw new Exception("Commande erronée : Clef vide");//vérifier que la clef n'est pas une chaine de caractère vide

        commandAsIs = command.toString();
    }

    @Override
    public String execute(Connect connect){
        connect.sendMessage(commandAsIs);

        String res = connect.receiveMessage();
        
        return res;
    }
}