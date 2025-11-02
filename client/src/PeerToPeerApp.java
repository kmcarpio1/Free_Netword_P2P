package src;

import java.util.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
// import java.text.ParseException; THIS SHOULD NEVER BE IMPORTED, CONFLICT WITH ORG.APACHE.COMMONS

import org.apache.commons.cli.*;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import src.client_class.Client;
import src.client_class.classes.ClientStdSmallBmap;
import src.commands.*;
import src.config.ConfigClient;
import src.config.SigIntHandler;
import src.connect.Connect;
import src.connect.Server;
import src.datatypes.FileDescription;
import src.datatypes.IpPort;
// import src.client_class.classes.ClientTerminalManager;
import src.utils.BadResponseException;

import java.util.function.*;
import java.util.stream.Stream;
import src.logger.Log;



/**
 * Programme principal
 * 
 * Fait tourner une boucle principale qui capte les interactions utilisateur
 */
public class PeerToPeerApp {

    /**
     * Méthode statique conjointe à main qui parse le fichier de configuration config.ini
     */
    private static Map<String,String> parseConfigFile(){
        System.out.println("Reading config.ini...");
        
        Map<String, String> configMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("config.ini"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(":");
                if (splitLine.length == 2) configMap.put(splitLine[0].trim(), splitLine[1].trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading config.ini: " + e.getMessage());
            System.exit(1);
        }

        return configMap;
    }

    /**
     * Méthode statique conjointe à main qui parse les arguments en ligne de commande
     * @param args : Arguments en ligne de commande
     * 
     * Options :
     * //--verbose : Affiche plus d'informations sur le programme
     * --ip-port : Spécifie un ip et un port pour le tracker (serveur), écrase le fichier de configuration
     */
    private static void argParse(String[] args) {
        Options options = new Options();
        options.addOption("i", "tracker-ip", true, "Specifies ip of the tracker (overrides config file)");
        options.addOption("p", "tracker-port", true, "Specifies port of the tracker (overrides config file)");
        options.addOption("l", "listen-port", true, "Specifies listen port of (self) client (overrides config file)");
    
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            Map<String, String> configMap = parseConfigFile();

            String inputTrackerIp = cmd.getOptionValue("tracker-ip");
            String inputTrackerPort = cmd.getOptionValue("tracker-port");

            if (inputTrackerIp == null){
                inputTrackerIp = configMap.get("tracker-ip");
            }
            if (inputTrackerPort == null){
                inputTrackerPort = configMap.get("tracker-port");
            }

            String inputIpPort = inputTrackerIp+":"+inputTrackerPort;
            if (IpPort.isValidIpPortString(inputIpPort)) {
                ConfigClient.setTracker(new IpPort(inputIpPort));
            } else {
                throw new ParseException("Parse error : " + inputIpPort);
            }

            String inputListenPort = cmd.getOptionValue("listen-port");

            if (inputListenPort == null){
                inputListenPort = configMap.get("listen-port");
            }

            ConfigClient.setListenPort(Integer.parseInt(inputListenPort));
    
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    

    public static void main(String[] args){
        // Config
        argParse(args);

        SigIntHandler.listenForSigInt();
        
        // Connecting to tracker
        System.out.println("Connecting to tracker " + ConfigClient.getTracker().toString() + " ...");
        Log.newEntryLog(0, "confing.ConfigClient","client","creation of the client with fields :\n trackerIport : "+ConfigClient.getTracker().toString()+"\nlistenPort : "+ ConfigClient.getListenPort()+"\npieceSize : "+ ConfigClient.getPieceSize()+ "\nnewpath : " + ConfigClient.newpath);
        IpPort trackerAddr = ConfigClient.getTracker();
        Connect toTracker = new Connect();

        try {
            toTracker.createConnection(trackerAddr);
        } catch (Exception e) {
            
            e.printStackTrace();
        }

        // Creating server endpoint
        Server.createServer(ConfigClient.getListenPort());

        // Creating peer-to-peer logic
        Client clientStandard = new ClientStdSmallBmap(toTracker);

        // Launching first announce
        String announceStr = "announce listen " + Integer.toString(ConfigClient.getListenPort()) + " seed [";
        for (FileDescription file : clientStandard.getSeeds()){
            announceStr += file.toString() + " ";
        }
        announceStr += "] leech [";
        for (FileDescription file : clientStandard.getLeech()){
            announceStr += file.toString() + " ";
        }
        announceStr += "]";

        System.out.println(announceStr);
        try {
            CommandParsed announce = new CommandParsed(announceStr);   
            ICommand announceComm = CommandFactory.createCommand(announce);
            String response = announceComm.execute(toTracker);
            if (response == null || !(response.equals("ok"))) throw new BadResponseException(announceStr, new CommandParsed(response));
        } catch (BadResponseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Interaction loop variables
        boolean continueLoop = true;
        Scanner scanInput = new Scanner(System.in);
        List<String> notForCLI = Arrays.asList("announce", "interested_", "getpieces", "have", "update");
        List<String> specialCommands = Arrays.asList("exit", "quit", "_show", "", null);

        while (continueLoop){

            System.out.println();
            System.out.print("< ");

            String request = scanInput.nextLine();// bloquant
            String response;

            if (specialCommands.contains(request)) {
                if (request.equals("exit") || request.equals("quit")) {
                    continueLoop = false;
                } else if (request.equals("_show")) {
                    System.err.println(toTracker.displayDataQueues());
                    System.err.println(">");
                } else {
                    System.err.println(">");
                }
            } else {
                try {
                    CommandParsed parsed = new CommandParsed(request);  
    
                    // Test if command suited for CLI
                    if (notForCLI.contains(parsed.getCommandName())){
                        System.out.println("> Invalid command for user : " + parsed.getCommandName());
                    } else { 

                        // Send user command
                        ICommand command = CommandFactory.createCommand(parsed);
                        
                        if (command instanceof Look){
                            response = command.execute(toTracker);
                            System.out.println("> " + response);

                        } else if (command instanceof GetFile){
                            response = command.execute(toTracker);
                            System.out.println("> " + response);
                            
                            List<IpPort> newPeersIp = ((GetFile) command).parseResponse(new CommandParsed(response));

                            clientStandard.connectTo(newPeersIp);
                        } else if (command instanceof Interested){
                            // Map reduce to all peers
                            clientStandard.mapReducePeers(parsed);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        }
        
        toTracker.freeConnection();
        Server.freeServer();
        scanInput.close();

        SigIntHandler.printProgramEnd();

        return;
    }
}
