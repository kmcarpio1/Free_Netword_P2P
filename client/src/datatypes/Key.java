package src.datatypes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Key {
    // Peut-être que le hash doit se présenter sous la forme d'un string et non d'un
    // byte[], c'est à voir en fonction des besoins.
    String _hash;


    public Key(String s){
        this._hash = md5Hash(s);
    }

    public Key(String keyAsString, Void asIs){
        this._hash = keyAsString;
    }

    private Key(){
        this._hash = md5Hash("");
    }

        /*note : chatGPT utilisé.
     car fonction de hachage bien connue, le codage de cette fonction est indépendant du contexte. Il demandee soit de réimplémenter l'algorithme, soit de connaître les méthodes spécifiques à Java pour son implémentation. 
     Cepedant, c'est important de comprendre l'algorithme dèrrière et de comprendre le code écrit.
    */
   //scope set to package-private
    static String md5Hash(String filename) {
            try {
                // Get MD5 MessageDigest instance
                MessageDigest md = MessageDigest.getInstance("MD5");
                // Update the digest using the bytes of the string
                //This line feeds the byte representation of the filename string into the MessageDigest instance md to be included in the hash calculation.
                md.update(filename.getBytes());
                // Perform the hash calculation and return the bytes
                byte[] digest = md.digest();

                // Convert the byte array to a hex string
                StringBuilder hexString = new StringBuilder();
                for (byte b : digest) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null; // Consider how to handle this exception properly
            }
        }

    public static boolean compare(Key a, Key b){
        return a.equals(b);
    }

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }

        if (!(o instanceof Key)) {
            return false;
        }

        Key other = (Key) o;

        return this._hash.equals(other._hash);
    }

    @Override
    public String toString(){
        return this._hash;
    }
}
