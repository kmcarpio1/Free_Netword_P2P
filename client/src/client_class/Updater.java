package src.client_class;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import src.commands.CommandFactory;
import src.commands.CommandParsed;
import src.commands.ICommand;
import src.connect.Connect;
import src.connect.Server;

public class Updater {

    private ClientStandard client;
    private ScheduledExecutorService scheduledExecutorService;

    Updater(ClientStandard client, Consumer<List<Connect>> peerRefreshFunc, Runnable filesRefreshFunc){
        this.client = client;

        startScheduledUpdate(() -> networkUpdateFunc(), 30, 60);

        startScheduledUpdate(() -> refreshPeers(peerRefreshFunc), 20, 30);

        startScheduledUpdate(filesRefreshFunc, 10, 10);
    }

    /**
     * Inner function that holds 2 reponsibilities :
     * - updates the list of peers of ClientStandard by getting it from the Server
     * - for each new peer, start a thread listening to its requests
     */
    private void refreshPeers(Consumer<List<Connect>> callback){
        List<Connect> newPeers = Server.getPeers();
        List<Connect> oldPeers = client.getPeers();
        for (Connect peer : newPeers) {
            if (!(oldPeers.contains(peer))){
                Thread peerListenerThread = new Thread(() -> listenToPeer(peer));
                peerListenerThread.start();
            }
        }
        callback.accept(newPeers);;
    }




    /**
     * Function meant to be put in a thread
     * Listens for incoming requests from a specified thread, and answers it
     */
    private void listenToPeer(Connect peer){ //le pb viendrait pas de la ?? A VERIFIER
        while (true){
            try {
                String request = peer.receiveMessage();
                CommandParsed parsed = new CommandParsed(request);    
                if (CommandFactory.isCommandToPeer(parsed)){
                    ICommand hook = CommandFactory.createHook(parsed, this.client);
                    hook.execute(peer);
                }  
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startScheduledUpdate(Runnable func, long initialDelay, long periodInSeconds) {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(func, initialDelay, periodInSeconds, TimeUnit.SECONDS); 
    }

    private void networkUpdateFunc(){
        // try {
        //     ICommand update = CommandFactory.createCommand(new CommandParsed("update")); // TODO : compute leech and seed (keys)
        //     ICommand have = CommandFactory.createCommand(new CommandParsed("have")); // TODO : compute key:buffermap of downloading
        //     update.execute(toTracker);
        //     for (Connect peer : peers) {
        //         String res = have.execute(peer);
        //     }

            
        // } catch (Exception e){
        //     e.printStackTrace();
        // }
    }
}
