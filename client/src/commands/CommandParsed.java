package src.commands;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


/**
 * Parse une entrée String dans un tableau de terminaux
 * Les [arg1 ... argn] sont compté comme un terminal en enlevant les []
 * input: announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e]
 * -> _t = ["announce", "listen", "2222", "seed", "file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e"]
 */
public class CommandParsed{ 

    private String _originalCommand;
    private String[] _t;

    public CommandParsed(String str) throws Exception{
        if (str == null) throw new Exception("Response was null reference");
        String[] st = str.replace("[", "[ ").replace("]", " ]").split(" ");

        boolean inBrackets = false;
        StringBuilder terminal = new StringBuilder();
        List<String> temp = new ArrayList<>();
        
        for (int i = 0; i < st.length; i++){
            String w = st[i];
            if (!w.equals("")) {
                if (w.equals("[")) {
                    if(inBrackets) throw new Exception("Commande erronée : crochet ouvert mal placé ");
                    else{
                        inBrackets = true;
                        terminal.setLength(0); //vide terminal et met sa longueur à 0
                    }
                }
                else if (w.equals("]")) {
                    if(!inBrackets) throw new Exception("Commande erronée : crochet fermé mal placé ");
                    else{
                        temp.add(terminal.toString().trim()); //enlève les crochets et les espaces au début et a la fin de terminal
                        inBrackets = false;
                    }
                }
                else  
                    if (inBrackets) terminal.append(w).append(" ");
                    else temp.add(w.trim());
            }
        }
        
        _originalCommand = str;
        _t = temp.toArray(new String[0]);
    }


    public String getCommandName() {
        return _t[0];
    }

    public String getArg(String argName) {
        int index = Arrays.asList(_t).indexOf(argName);
        if (_t.length <= index + 1|| index == -1) return "";
        else return _t[index + 1];
    }

    public String getArg(int index) {
        if (_t.length <= index) return "";
        else return _t[index];
    }

    public int size(){
        return _t.length;
    }

    @Override
    public String toString(){
        String whole = getCommandName();
        for (int i=1 ; i<_t.length ; i++){
            whole += " " + _t[i];
        }
        return whole;
    }

    public String getOriginalCommand() {
        return _originalCommand;
    }

    public void print() {
        System.out.println(Arrays.toString(_t));
    }

}