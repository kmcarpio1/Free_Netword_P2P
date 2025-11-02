package src.datatypes;

import java.io.Closeable;
import java.io.File;
import java.io.RandomAccessFile ;
import java.nio.charset.StandardCharsets;

import src.config.ConfigClient;

import java.io.FileOutputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID; // permet de générer un nom aléatoire unique 

/**
 * Idées : 
 * Un FileDescription a 2 bufferMap, un dont les pieces font la taille standard des pieces du client qui possède ce fichier, et un 
 * faisant la taille du fichier (on garde trace de chaque caractère).
 * Ainsi, lorsqu'on get/set une piece de ce fichier, si la taille de la piece demandée/proposée est celle du client, alors il est
 * simple de la donner/récupérer, sinon, c'est toujours possible, mais plus compliqué.
 * 
 * Point positif : permet de gérer les cas ou des clients ont des tailles de piece différents
 * Point négatif : nécessite un buffer de la taille du fichier, pouvant être à la fois lourd, et long a gérer.
 * 
 * Autre alternative : Le Client2 demande au Client1 si il possède la pièce numero 3 de taille 1000 (il veut les caractères 2000 
 * à 2999). Le Client1 lui se base sur des pièces de taille 900. Ainsi, ces caractères sont à cheval entre les pièces 3 et 4.
 * Le Client1 ne confirmera la disponibilité de ce que demande le Client2, non pas si il possède les 1000 caractères demandés,
 * mais si il possède entierement (vérifiable grace au bufferMap) les pièces les possédant.
 * Donc même si pour une raison obscure, le Client1 avait les caractères 2000 à 2999, si il ne possède pas entièrement les caractères
 * de 1800 à 3599 (les pièces 3 et 4 pour lui), il lui dira qu'il ne les a pas, car n'a aucun moyen de le vérifier.
 * Par contre, si le Client1 possède bien les caractères 1800 à 3599, (les pièces 3 et 4), il informera Client2 qu'il possède ce 
 * qu'il demande.
 * Par la suite, si le Client2 demande au Client1 cette pièce, le Client1 enverra uniquement ce que le Client2 demande.
 * 
 * Autre autre altérnative : 
 * On peut ne pas se prendre la tete, si la taille des pièces demandées ne sont pas celle habituelles, on peut 
 * juste aller regarder dans le fichier les caractères consernées et vérifier qu'aucun n'est un caractère vide "\0".
 * Ca ne marche que si il est "impossible" d'avoir des caractère vide dans un fichier complet. (du style des caractères vides 
 * au milieu du fichier).
 * Ca pose néammoins un problème pour la dernière pièce qui va surement avoir des caractères vides si la taille des pièces n'est 
 * un diviseur de la taille du fichier
 */


public class FileDescription implements Closeable {
    private String fileName;
    private long length;
    private Key selfHash;
    private BufferMap bufferMap;
    private String path;

    private RandomAccessFile randomAccessFile;
    private static final String _pathToFile = ConfigClient.newpath;


    /**
     * Appelé par tout constructeur, écrit à vide dans le fichier créé
     * @param path Chemin du fichier
     */
    private void initialWrite(String path){
        try {
            FileOutputStream fos = new FileOutputStream(path);
            long lenCopy = length;
            while (lenCopy > ((long) Integer.MAX_VALUE)){
                fos.write(new byte[Integer.MAX_VALUE]);
                lenCopy -= Integer.MAX_VALUE;
            }

            fos.write(new byte[(int) lenCopy]);

            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace(); 
        } catch (IOException e){
            e.printStackTrace(); 
        }
    }

    /**
     * Appelé par les constructeur n'ayant pas eu de nom spécifié, écrit à vide dans le fichier créé
     * @return name Nom de ficheir unique 
     */
    private String generateRandomName(){
        String name = UUID.randomUUID().toString() + ".tex"; //ceci créé un nom aléatoire unique sur le systeme
        File file = new File(_pathToFile, name);
        
        // Vérifier si le fichier existe déjà, sinon, réessayer jusqu'à ce qu'un nom unique soit trouvé
        while (file.exists()) {
            name = UUID.randomUUID().toString() + ".tex";
            file = new File(_pathToFile, name);
        }

        return name;
    }
    
    /**
     * Constructeur complet du FileDescription
     * @param name Nom du fichier
     * @param len Taille du fichier
     * @param newpath Chemin du fichier
     * @param sizeBufferMap Taille des piecesSize
     * @param have Vrai si le client possède le fichier, faux s'il souhaite le recevoir
     * @throws FileNotFoundException
     * @throws IOException
     */
    public FileDescription(String name, long len, String newpath, int pieceSize, boolean have) throws FileNotFoundException{ 
        this.fileName = name;
        this.length = len;
        this.selfHash = new Key(name);
        this.bufferMap = new BufferMap(len, pieceSize, have);
        this.path = newpath+name;

        if (have == false) initialWrite(path);

        randomAccessFile = null;
    }

    /**
     * Constructeur sans taille du fichier
     * @param name Nom du fichier
     * @param newpath Chemin du fichier
     * @param sizeBufferMap Taille du buffermap du fichier
     * @param have Vrai si le client possède le fichier, faux s'il souhaite le recevoir
     * @throws FileNotFoundException
     * @throws IOException
     */
    public FileDescription(String name, String newpath, int sizeBufferMap, boolean have) throws FileNotFoundException, IOException{ 
        this.fileName = name;
        this.length = sizeBufferMap * ConfigClient.getPieceSize();
        this.selfHash = new Key(name);
        this.bufferMap = new BufferMap(sizeBufferMap, have);
        this.path = newpath+name;

        if (have == false) initialWrite(path);
        
        randomAccessFile = null;
    }


    /**
     * Constructeur sans taille du fichier ni du buffermap
     * @param name Nom du fichier
     * @param newpath Chemin du fichier
     * @param have Vrai si le client possède le fichier, faux s'il souhaite le recevoir
     * @throws FileNotFoundException
     * @throws IOException
     */
    public FileDescription(String newpath, boolean have) throws FileNotFoundException, IOException{ 
        //String keyString = new Key(this.fileName);
        //this.fileName = keyString.substring(0, Math.min(keyString.length(), 10));
        this.fileName = generateRandomName();
        this.length = -1;
        this.selfHash = new Key(this.fileName);
        this.bufferMap = null;
        this.path = newpath+fileName;

        
        randomAccessFile = null;
    }

    /**
     * Constructeur sans taille du fichier ni du buffermap
     * @param name Nom du fichier
     * @param newpath Chemin du fichier
     * @param have Vrai si le client possède le fichier, faux s'il souhaite le recevoir
     * @throws FileNotFoundException
     * @throws IOException
     */
    public FileDescription(String key, String newpath, boolean have) throws FileNotFoundException, IOException{ 
        //String keyString = new Key(this.fileName);
        //this.fileName = keyString.substring(0, Math.min(keyString.length(), 10));
        this.fileName = generateRandomName();
        this.length = -1;
        this.selfHash = new Key(key, null);
        this.bufferMap = null;
        this.path = newpath+fileName;

        
        randomAccessFile = null;
    }


    /**
     * Constucteur sans nom et sans chemin du fichier
     * @param len Taille du fichier
     * @param sizeBufferMap Taille du buffermap du fichier
     * @param have Vrai si le client possède le fichier, faux s'il souhaite le recevoir
     * @throws FileNotFoundException
     * @throws IOException
     */
    public FileDescription(long len, int sizeBufferMap, boolean have) throws FileNotFoundException, IOException{ 
        this.fileName =  generateRandomName();
        this.length = len;
        this.selfHash = new Key(this.fileName);
        this.bufferMap = new BufferMap(sizeBufferMap, have);
        this.path = _pathToFile+fileName;

        if (have == false) initialWrite(path);
        
        randomAccessFile = null;
    }


    public String getPath(){
        return path;
    }

    /************ Methods on FileDescription buffermap ***************/

    public boolean hasBufferMap(){
        return bufferMap != null;
    }

    public BufferMap getBufferMap(){
        return bufferMap;
    }

    public void createBufferMap(int size, int pieceSize){
        this.length = ((long) size) * ((long) pieceSize); 
        this.bufferMap = new BufferMap(size, pieceSize, false);
        initialWrite(this.path);
    }

    /*****************************************************************/

    public String getFileName(){
        return fileName;
    }

    public long getLength(){
        return length;
    }

    public Key getSelfHash(){
        return selfHash;
    }

    @Override
    public String toString(){
        return fileName + " " 
            + Long.toString(length) + " " 
            + Integer.toString(ConfigClient.getPieceSize()) + " "
            + selfHash.toString();
    }

    /************** I/O Methods on FileDescriptions  *****************/

    /**
     * Récupère la "piece" de fichier demandée sous forme de String
     * Opération synchronisée à l'objet
     * Fait appel à un RandomAccessFile
     * @param index l'indice dans le buffermap de la "piece" à récupérer
     * @param pieceSize la taille de la "piece"
     * @return la "piece" de fichier
     * * @throws IOException l'opération de lecture a échoué
     */
    public synchronized String getPiece(int index, int pieceSize) throws IOException{ //a tester
        try{
            randomAccessFile.seek(index * pieceSize);
            byte[] bytes = new byte[pieceSize];
            if (randomAccessFile.read(bytes) == -1){
                randomAccessFile.close();
                throw new IOException("File end has been reached");
            }
            //randomAccessFile.close();
            return new String(bytes).replaceAll("\n", "\\\\n");
        } catch (IOException e){
            throw new IOException(e);
        }
    }

    /**
     * Ecrit la "piece" passée en paramètre dans le fichier à l'endroit demandé
     * Opération synchronisée à l'objet
     * Fait appel à un RandomAccessFile
     * @param piece la "piece" à écrire
     * @param pieceSize la taille de "piece" à écrire
     * @param pieceIndex l'indice dans le buffermap où écrire le fichier
     * @throws IOException l'opération d'écriture a échoué
     */
    public synchronized void writeInFile(String piece, int pieceSize, int pieceIndex) throws IOException{
        try {
            randomAccessFile.seek(pieceIndex * pieceSize); 
            randomAccessFile.write(piece.replaceAll("\\\\n", "\n").replaceAll("\\\'\\\'", "\\\"").getBytes());
            this.bufferMap.setReceiveBufferMap(pieceIndex);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Ecrit la "piece" passée en paramètre dans le fichier à l'endroit demandé
     * La taille de la "piece" est inférée sur la config cliente
     * Opération synchronisée à l'objet
     * Fait appel à un RandomAccessFile
     * @param piece la "piece" à écrire
     * @param pieceIndex l'indice dans le buffermap où écrire le fichier
     * @throws IOException l'opération d'écriture a échoué
     */
    public synchronized void writeInFile(String piece, int pieceIndex) throws IOException { 
        try {
            writeInFile(piece, ConfigClient.getPieceSize(), pieceIndex);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public void open(){
        File file = new File(path);
        try {
            if (randomAccessFile == null) randomAccessFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }   
    }

    @Override
    public void close(){
        try {
            if (randomAccessFile != null) randomAccessFile.close();
            randomAccessFile = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
