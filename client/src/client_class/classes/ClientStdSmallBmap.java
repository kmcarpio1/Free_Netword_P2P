package src.client_class.classes;

import src.client_class.*;

import src.connect.Connect;
import src.commands.ICommand;
import src.commands.Interested;
import src.commands.CommandParsed;
import src.commands.CommandFactory;
import src.datatypes.BufferMap;
import src.datatypes.FileDescription;
import src.utils.BadResponseException;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe qui porte la logique coeur de l'échange de données pair-à-pair
 */
public class ClientStdSmallBmap extends ClientStandard {

    public ClientStdSmallBmap(Connect toTracker){
        super(toTracker);
    }

    @Override
    protected Consumer<Integer> mapOnPeers(ICommand interested, List<Connect> peers, List<BufferMap> bufferMaps){
        String key = ((Interested) interested).getKey();
        Consumer<Integer> setBufferMapForPeer = (Integer i) -> {
            try {
                String result = interested.execute(peers.get(i)); // Executes interested on every client
                CommandParsed haveResponse = new CommandParsed(result);
                if (!(haveResponse.getArg(1).equals(key))) throw new BadResponseException(haveResponse);
                BufferMap newBmap = ((Interested) interested).parseResponse(haveResponse);
                bufferMaps.set(i, newBmap); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        return setBufferMapForPeer;
    }

    /**
     * Méthode interne pour filtrer quels buffermaps garder
     * Le résultat pouvoir remplir au maximum un buffermap vide
     * @param bufferMaps : les buffermaps à filtrer
     * @return Liste des indices de tous les buffermaps à garder
     * stratégie retenue : maximiser l'échange entre pairs de petits buffermap -> maximise le multithreading mais facteur limitant = upload des paires bien souvent.
     * optimise la vitesse de téléchargement.
     */
    @Override
    protected HashMap<Connect,BufferMap> filterBuffermaps(List<BufferMap> bufferMaps, List<Connect> peerList, BufferMap mask){
        HashMap<Connect,BufferMap> toRet = new HashMap<Connect,BufferMap>();
        List<Integer> alreadyConsidered = new ArrayList<Integer>();
        int l = bufferMaps.get(0).size();
        int g = bufferMaps.size();
        //BufferMap mask = new BufferMap(l,false);
        int index = 0;
        //boucle principale, on va remplir le mask ici.
        for (int j = 0 ; j < g ; j++){
            int min = Integer.MAX_VALUE;
            //recherche le buffermap toujours candidat de taille minimum dans le tableau
            for (int i = 0; i< g ; i++ ){
                int val = bufferMaps.get(i).nPieces();
                if (val < min && !(alreadyConsidered.contains(i))) {min = val; index = i;}
            }
            alreadyConsidered.add(index);
            // checker le mask
            try{ // handling different max number of pieces of buffermaps
                BufferMap result = mask.addAndCompareTrueness(bufferMaps.get(index)); //result est le buffer dif entre mask, et mask est mis a jour
                if (result.or()){//if or reduce operation is true then buffermaps[index] adds trueness to mask 
                toRet.put(peerList.get(index),result); // on ajoute alors le client conserné et le buffer dif
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toRet;
    }

    /**
     * Méthode pour vérifier et mettre sous forme de dictionnaire les information reçues par un autre client sous forme d'une commande data
     * @param command : la commande reçue
     * @param key : la clef de fichier recherchée
     * @return Liste des indices de tous les buffermaps à garder
     * stratégie retenue : maximiser l'échange entre pairs de petits buffermap -> maximise le multithreading mais facteur limitant = upload des paires bien souvent.
     * optimise la vitesse de téléchargement.
     */
    private HashMap<Integer,String> filterData(CommandParsed command, String searchedKey) throws Exception {
        String key = command.getArg(1);
        //TODO? vérifier le format attendu des clefs (base 16 ? 32 caractères ?)
        if(key == "") throw new Exception("Commande erronée : Clef vide");//vérifier que la clef n'est pas une chaine de caractère vide
        else if (!(key.equals(searchedKey))) throw new Exception("Commande erronée : Clef différente que celle recherchée");
        String indexPieces = command.getArg(2);
        if(indexPieces == "") throw new Exception("Commande erronée : Aucun argument");

        //String[] cuttedIndexPieces = indexPieces.replaceAll(":", " : ").split(" ");
        String[] cuttedIndexPieces = indexPieces.split(" ");
//
        HashMap<Integer,String> indexPiecesDic = new HashMap<>();
        //boolean inPiece = false;
        //StringBuilder piece = new StringBuilder();
        //List<String> indexPiecesList = new ArrayList<>();

        //Map<Integer, String> dictionnaire = new HashMap<>();


        // Expression régulière pour valider le format de la chaîne
        String regex = "(\\d+):\"([^\"]+)\"";

        // Créer un pattern à partir de l'expression régulière
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(indexPieces);

        // Parcourir la chaîne et extraire les paires clé-valeur
        while (matcher.find()) {
            int k = Integer.parseInt(matcher.group(1));
            String value = matcher.group(2);
            indexPiecesDic.put(k, value);
        }

        // Vérifier s'il n'y a rien d'autre que le motif dans la chaîne
        String cleanedInput = indexPieces.replaceAll(regex, "");
        if (!cleanedInput.trim().isEmpty()) {
            throw new Exception("Erreur: Le format de la chaîne est incorrect");
        }


    
        //for(int i=0;i<cuttedIndexPieces.length;i++){
        //    String el = cuttedIndexPieces[i];
        //    if(el.equals("\"")){
        //        if(!inPiece){
        //            inPiece = true; //on commence la piece
        //            piece.setLength(0);
        //        }
        //        else{
        //            inPiece = false; //on ferme la piece
        //            indexPiecesList.add(piece.toString());
        //        }
        //    }
        //    else{
        //        if (inPiece) piece.append(el).append(" ");
        //        else{
        //            if(el!="") indexPiecesList.add(el);
        //        }
//
        //    }
        //} //on est sensé avoir un truc de la forme ["index",":","blablabl", etc]
//
        //for(int i=0;i<indexPiecesList.size();i++){
        //    String el = indexPiecesList.get(i);
        //    if(el.equals(":")) {
        //        if(i == 0 || i == indexPiecesList.size()) throw new Exception("Commande erronée : La commande commence ou finit par un \":\"");
        //        if(parts.length != 2) throw new Exception("Commande erronée : Un élément contient trop de \":\"");
        //        try {
        //            Integer.parseInt(parts[0]); //on verifie que la taille du fichier est bien un int valide
        //        } catch(NumberFormatException e) {
        //            throw new Exception("Commande erronée : Indice non valide");
        //        }
        //        indexPiecesDic.put(Integer.parseInt(parts[0]),parts[1]);
        //    }
        //    else throw new Exception("Commande erronée : Un élément ne contient pas de \":\"");
        //}
        return indexPiecesDic;

    }

    @Override
    protected Consumer<Connect> reduceOnPeers(HashMap<Connect,BufferMap> peersToLeech, int pieceSize, FileDescription file){
        String key = file.getSelfHash().toString();
        Consumer<Connect> writePieceOfPeer = (Connect connect) -> {
            try {
                BufferMap bufferMapDif = peersToLeech.get(connect);
                boolean[] bufferToGet = bufferMapDif.getBufferMap();
                int bufferSize = bufferMapDif.size();
                String getPiecesCommand = "getpieces " + key + " [";
                for(int i=0;i<bufferSize;i++){
                    if(bufferToGet[i] == true){
                        getPiecesCommand += i;
                        if(i!=bufferSize-1) getPiecesCommand += " ";
                    }
                }

                getPiecesCommand += "]"; //la commande vient d'être écrite
                ICommand leechCommand = CommandFactory.createCommand(new CommandParsed(getPiecesCommand)); //on va donc la créer
                String result = leechCommand.execute(connect); // Executes getpieces, result secrura "data $key [$index1:$Piece1 ...]"
                HashMap<Integer,String> receivedPieces = filterData(new CommandParsed(result), key);
                for (int pieceIndex : receivedPieces.keySet()){
                    String piece = receivedPieces.get(pieceIndex);
                    file.writeInFile(piece, pieceSize, pieceIndex); //ecrit dans le fichier et met a jour les infos de file
                }
            } catch (Exception e) { 
                e.printStackTrace();
            }
        };
        return writePieceOfPeer;
    }


}
