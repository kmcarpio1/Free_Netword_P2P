package src.utils;

import java.util.ArrayList;
import java.util.List;

import src.config.ConfigClient;
import src.datatypes.FileDescription;

import java.io.File;
import java.io.FileNotFoundException;


/*
 * TODO
 * On considère qu'il peut y avoir des fileDescritpion différents dans seeding et leeching (d'où le fait qu'on regarde les deux)
 * Si ce n'est pas le cas il suffit de retirer une partie du code.
 * Qu'est ce qui s'occupe de mettre a jour leeching ? 
 */


/*
 * FileChecker met a jour des listes de fichier.
 */
public class FileChecker {

    private static final String _pathToFile = ConfigClient.newpath;

    private FileChecker(){}

    /**
     * Retourne la liste des fichiers présents si leur taille reste raisonnable
     * @return la liste des fichiers contenus dans ConfigClient.newpath
     * @throws FileNotFoundException
     */
    public static List<File> scanForFiles() throws FileNotFoundException {
        List<File> filesList = new ArrayList<File>();

        File folder = new File(_pathToFile);
        if(folder.isDirectory()){
            File[] files = folder.listFiles();
            for (File file : files){
                long fileSize = file.length();
                if (fileSize <= Long.MAX_VALUE) {
                    filesList.add(file);
                }
            }
            return filesList;
        }
        else {
            throw new FileNotFoundException("Incorrect file path");
        }
    }

    /**
     * Il va regarder les différents fichiers précédemment référencés dans actualFiles, vérifier si ils sont bien présents
     * et les ajouter dans la nouvelle liste en conséquence. 
     * Il fait bien attention a ne pas les ajouter plusieurs fois à l'aide d'une liste des noms de fichier.
     * Ensuite, la méthode récupère tous les fichiers dans le dossier "files", et un à un vérifie si on l'a déjà référencer. Si on 
     * en trouve un qui ne l'est pas, ça veut dire que c'est un nouveu fichier, et on créé un FileDescription en conséquence.
     * @param newFiles
     * @param actualFiles
     * @return
     * @throws FileNotFoundException
     */
    public static List<FileDescription> updateFiles(List<File> newFiles, List<FileDescription> actualFiles) throws FileNotFoundException {
        List<FileDescription> newFDesc = new ArrayList<FileDescription>();
        List<String> alreadyGot = new ArrayList<String>();

        // Only keep existing files in actualFiles, and count alreadyGot files
        for(FileDescription fileDescription : actualFiles){
            File file = new File(fileDescription.getPath());
            if (file.exists()) {
                newFDesc.add(fileDescription);
                alreadyGot.add(fileDescription.getFileName());
            }
        }

        // Update newFDesc : if alreadyGot, change nothing, else, add a new FileDescription
        for(File file : newFiles){
            if(!(alreadyGot.contains(file.getName()))){
                alreadyGot.add(file.getName());
                long fileSize = file.length();
                newFDesc.add(new FileDescription(file.getName(), fileSize, _pathToFile , ConfigClient.getPieceSize(), true));
            }
        }

        return newFDesc;
    }

}
