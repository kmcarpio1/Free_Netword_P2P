package tst.datatypes;

import java.io.FileNotFoundException;
import java.io.IOException;

import src.datatypes.FileDescription;

public class TestFileDescription {
    
    public static void testFileDescription() {
        testConstructor();
        testWriteInFile();
        testGetPiece();

    }

    
    public static void testConstructor() {
        try {
            FileDescription fileDescription = new FileDescription("fichierTest.tex",20,"./files/",4,false);
            fileDescription.open();
            fileDescription.close();
            System.out.println("Constructor test passed");
        } catch (FileNotFoundException e) {
            System.out.println("Constructor test failed: FileNotFoundException");
            e.printStackTrace();
        }
    }

    public static void testWriteInFile() {
        try {
            FileDescription fileDescription = new FileDescription("fichierTest.tex",20,"./files/",4,false);
            fileDescription.open();
            fileDescription.writeInFile("12345", 5, 2);
    
            boolean[] tableau = new boolean[4]; // Crée un tableau de taille 10
            // Initialise chaque élément du tableau avec false
            for (int i = 0; i < tableau.length; i++) {
                tableau[i] = false;
            }
            tableau[2] = true;
            
            for (int i = 0; i < tableau.length; i++) {
                assert fileDescription.getBufferMap().getBufferMap()[i] == (tableau[i]) : "BufferMap not updated" ;
            }


            fileDescription.close();
            System.out.println("Write in file test passed");



        } catch (FileNotFoundException e) {
            System.out.println("Write in file test failed: FileNotFoundException");
        } catch (IOException e) {
            System.out.println("Write in file test failed: IOException");
        }
    }

    public static void testGetPiece() {
        try {
            FileDescription fileDescription = new FileDescription("fichierTest.tex",20,"./files/",4,false);
            fileDescription.open();
            fileDescription.writeInFile("12345", 5, 2);
    
            // Crée un tableau pour représenter les pièces récupérées
            String[] pieces = new String[4];
            
            // Récupère chaque pièce et stocke dans le tableau
            for (int i = 0; i < 2; i++) {
                pieces[i] = fileDescription.getPiece(i, 5); // Supposant que la taille de la pièce est 5
            }

            String vide = "\0\0\0\0\0";
            // Vérifie si les pièces récupérées correspondent aux valeurs attendues
            assert pieces[0].equals(vide) : "Piece 0 incorrect";
            assert pieces[1].equals(vide) : "Piece 1 incorrect";
            assert pieces[2].equals("12345") : "Piece 2 incorrect";
            assert pieces[3].equals(vide) : "Piece 3 incorrect";

            fileDescription.close();
            System.out.println("Get piece in file test passed");
            
        } catch (FileNotFoundException e) {
            System.out.println("Get piece in file test failed: FileNotFoundException");
        } catch (IOException e) {
            System.out.println("Get piece in file test failed: IOException");
        }

    }   

}



