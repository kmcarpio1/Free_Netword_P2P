package src.logger;


/*
 * On utilise BufferdWriter pour améliorer les performance d'écriture 
 * (surtout quand on écrit une grande quantité de donéne (ici c'est peut etre un peu overkill)).
 * En effet, BufferedWriter va d'abord écrire les données en mémoire avant de 
 * les écrire sur le fichier.
*/
import java.io.BufferedWriter;  
import java.io.Writer;
import java.io.File;
import java.io.FileWriter;  // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    
    private static String[] securityLevels = {
        "EMERGENCY",
        "ALERT",
        "CRITICAL",
        "ERROR", 
        "WARNING",
        "NOTICE",
        "INFORMATIONAL",
        "DEBUG"
    };

    private static String DEFAULT_LOG_PATH = "./src/logger/logging.log";
    


    public static enum LOGGING_LEVELS{
        EMERGENCY,
        ALERT,
        CRITICAL,
        ERROR,
        WARNING,
        NOTICE,
        INFORMATIONAL,
        DEBUG
    }

    public static void deleteLoggerFile(){
        File logFile = new File(DEFAULT_LOG_PATH);
        logFile.delete();
    }


    public static void newEntryLog(int level, String functionName, String user, String message){
        // System.out.println("Répertoire de travail courant : " + System.getProperty("user.dir"));
        
        
        try(Writer logFile = new BufferedWriter(new FileWriter(DEFAULT_LOG_PATH, true))) {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            String log = "";
            log += functionName + "\nState: " + securityLevels[level] + " \nAt " + dtf.format(now) + " \nBy " + user;

            if(message != null){
                log += "\nMessage:" + message;
            }

            log += "\n\n";

            logFile.write(log);
        
            logFile.close();  
    
        
        } catch(IOException e){
            System.out.println("Erreur à l'ouverture/création du fichier de log");
            e.printStackTrace();
        }



    }



}
