package tst.logger;

import src.logger.Log;
import src.logger.Log.LOGGING_LEVELS;

public class LoggerTest {
    public static void main(String[] args) {
        // Test different logging levels
        Log.deleteLoggerFile();
        //ordinal() est une méthode de la classe enum qui permet d'avoir la position de l'énumeration de la déclaration
        Log.newEntryLog(Log.LOGGING_LEVELS.EMERGENCY.ordinal(), "main", "user1", "Emergency message"); 
        Log.newEntryLog(Log.LOGGING_LEVELS.ALERT.ordinal(), "main", "user2", "Alert message");
        Log.newEntryLog(Log.LOGGING_LEVELS.CRITICAL.ordinal(), "main", "user3", "Critical message");
        Log.newEntryLog(Log.LOGGING_LEVELS.ERROR.ordinal(), "main", "user4", "Error message");
        Log.newEntryLog(Log.LOGGING_LEVELS.WARNING.ordinal(), "main", "user5", "Warning message");
        Log.newEntryLog(Log.LOGGING_LEVELS.NOTICE.ordinal(), "main", "user6", "Notice message");
        Log.newEntryLog(Log.LOGGING_LEVELS.INFORMATIONAL.ordinal(), "main", "user7", "Informational message");
        Log.newEntryLog(Log.LOGGING_LEVELS.DEBUG.ordinal(), "main", "user8", "Debug message");
    }
}
