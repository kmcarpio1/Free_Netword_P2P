package src.commands;

import src.connect.Connect;

public class GetPieces implements ICommand {

    private String _key; 
    private String[] _pieces;

    GetPieces(CommandParsed command) throws Exception{
        String key = command.getArg(1);
        _key = key; //TODO? vérifier le format attendu des clefs (base 16, 32 caractères ?)
        if(_key == "") throw new Exception("Commande erronée : Clef vide");//vérifier que la clef n'est pas une chaine de caractère vide

        String[] pieces = command.getArg(2).split(" ");
        _pieces = pieces;

        for(int k=0;k<_pieces.length; k++){
            try {
                Integer.parseInt(_pieces[k]); //on verifie que la taille du fichier est bien un int valide
            } catch(NumberFormatException e) {
                throw new Exception("Commande erronée : Indice non valide");
            }
        }
    }

    @Override
    public String execute(Connect connect){
        String message = "getpieces " + _key + " ";
        message += "[";
        for (int i=0 ; i<_pieces.length ; i++){
            message += _pieces[i];
            if(i != _pieces.length-1) message += " ";
        }
        message+= "]";
        connect.sendMessage(message);

        String response = connect.receiveMessage(); // Là dedans y'a data (à priori, à vérifier : concurrence)
        System.out.println("> " + response);
        return response;
    }


}