package src.commands;

import src.connect.Connect;

public class Update implements ICommand {

    private String[] _keys_files_share; // Tableau des clés des fichiers qui peuvent être fourni
    private String[] _keys_files_download; // Tableau des clés des fichiers en cours de téléchargement

    Update(CommandParsed command) throws Exception{

        // Initialisation des fichiers à fournir    
        String seedArg = command.getArg("seed");
        _keys_files_share = seedArg.split(" ");
        for(int k = 0; k<_keys_files_share.length;k++){
            if( _keys_files_share[k] == "") throw new Exception("Commande erronée : Clef partagée vide"); //vérifier que la clef n'est pas une chaine de caractère vide
        }
    
        // Initialisation des fichiers en cours de téléchargement
        String leechArg = command.getArg("leech");
        _keys_files_download = leechArg.split(" ");
        for(int k = 0; k<_keys_files_download.length;k++){
            if( _keys_files_download[k] == "") throw new Exception("Commande erronée : Clef de fichier en cours de téléchargement vide");//vérifier que la clef n'est pas une chaine de caractère vide
        }
    }

    public String get_keyShare(int index) {
        return _keys_files_share[index];
    }

    public int getCount_keyShare() {
        return _keys_files_share.length;
    }

    public String get_keyDownload(int index) {
        return _keys_files_download[index];
    }

    public int getCount_keyDownload() {
        return _keys_files_download.length;
    }

    @Override
    public String execute(Connect connect){
        return null;
    }

}