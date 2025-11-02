package tst.utils;

import java.util.ArrayList;
import java.util.List;
import src.utils.FileChecker;
import src.datatypes.FileDescription;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;

public class TestFileChecker {
    
    private static final String TEST_FILES_PATH = "./files/";

    public static void testFileChecker(){
        // testCheckFiles();
    }

    public static void cleanUp() {
        // Supprime les fichiers créés pendant le test
        File folder = new File(TEST_FILES_PATH); 
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                // Supprimer uniquement les fichiers créés pendant le test
                if (file.getName().startsWith("test_file_")) {
                    file.delete();
                }
            }
        }
    }

    /*
    public static void testCheckFiles() {
        try {
            List<FileDescription> actualSeeding = new ArrayList<>();
            List<FileDescription> actualLeeding = new ArrayList<>();
            //Création de quelques fichiers 
            FileDescription file1 = new FileDescription("test_file_file1.txt", 100, TEST_FILES_PATH, 10, false); // file1 sera dans les 2
            FileDescription file2 = new FileDescription("test_file_file2.txt", 100, TEST_FILES_PATH, 10, false); // file2 sera deux fois dans seeking
            FileDescription file3 = new FileDescription("test_file_file3.txt", 100, TEST_FILES_PATH, 10, false); // file3 sera dans leeking
            FileDescription file4 = new FileDescription("test_file_file4.txt", 100, TEST_FILES_PATH, 10, false); // file4 sera dans leeking, mais sera supprimé
            FileDescription file5 = new FileDescription("test_file_file5.txt", 100, TEST_FILES_PATH, 10, false); // file5 ne sera dans aucun

            // Ajout de quelques fichiers de seeding existants
            actualSeeding.add(file1);
            actualSeeding.add(file2);
            actualSeeding.add(file2);

            // Ajout de quelques fichiers de leeching existants
            actualLeeding.add(file1); 
            actualLeeding.add(file3); 
            actualLeeding.add(file4);
            
            // Simulation de la création de nouveaux fichiers dans le dossier
            // "file5.txt" est ajouté dans le dossier
            // et qu'il n'est pas encore référencé ni dans seeding ni dans leeching
            // Sa taille est de 300 bytes

            File test_file_file4 = new File(file4.getPath());
            test_file_file4.delete();

            List<FileDescription> updatedSeeding = FileChecker.checkFiles(actualSeeding, actualLeeding);
            try (FileInputStream ignored = new FileInputStream(file5.getPath())) {
                updatedSeeding = FileChecker.checkFiles(actualSeeding, actualLeeding);
            }
            assert updatedSeeding.contains(file1) : "Erreur : file1 référencé dans Seeking et Leeching n'est pas présent";
            assert Collections.frequency(updatedSeeding, file1)!=1 : "Erreur : file1 a été référencé plusieurs fois dans la nouvelle liste car il est référencé et dans leeching et dans seeking";
            assert updatedSeeding.contains(file2) : "Erreur : file2 référencé dans Seeking n'est pas présent";
            assert updatedSeeding.contains(file3) : "Erreur : file3 référencé dans Leeching n'est pas présent";
            assert !updatedSeeding.contains(file4) : "Erreur : file4 référencé dans Leeking mais supprimé du répertoire files est présent";
            assert updatedSeeding.contains(file5) : "Erreur : file5 référencé dans aucun des deux mais présent dans le repertoire files n'est pas présent";

            cleanUp();
            System.out.println("Check files test passed");

        } catch (Exception e) {
            cleanUp();
            System.out.println("Check files test failed: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
    */
}