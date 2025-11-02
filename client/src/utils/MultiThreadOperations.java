package src.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Classe utilitaire pour manipuler les threads
 */
public class MultiThreadOperations {

    /**
     * Fonction pour effectuer une opération "forEach" en multithreadé
     * La fonction à appliquer prend en paramètre un élémént de la liste et ne renvoie rien
     * Appel bloquant (utilisation de join sur chaque thread)
     * 
     * @param <V> Le type des éléments la liste sur laquelle itérer
     * @param list La liste des éléments sur laquelle itérer
     * @param func La fonction (sans input, sans valeur de retour) à effectuer sur chaque élément
     */
    public static <V> void forEachThread(Iterable<V> list, Consumer<V> func){
        List<Thread> mapThreads = new ArrayList<>();
        for (V elem : list) {
            Thread mapThread = new Thread(() -> func.accept(elem));
            mapThreads.add(mapThread);
            mapThread.start();
        }

        // Wait for all threads to finish
        for (Thread mapThread : mapThreads) {
            try {
                mapThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fonction pour effectuer une opération "forEach" en multithreadé
     * La fonction à appliquer prend en paramètre l'indice de l'élément de la liste
     * Appel bloquant (utilisation de join sur chaque thread)
     * 
     * @param <V> Le type des éléments la liste sur laquelle itérer
     * @param list La liste des éléments sur laquelle itérer
     * @param func La fonction (input entier, sans valeur de retour) à effectuer sur chaque élément
     */
    public static <V> void forThread(List<V> list, Consumer<Integer> func){
        List<Thread> mapThreads = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            final int index = i;
            Thread mapThread = new Thread(() -> func.accept(index));
            mapThreads.add(mapThread);
            mapThread.start();
        }

        // Wait for all threads to finish
        for (Thread mapThread : mapThreads) {
            try {
                mapThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fonction pour effectuer une opération "forEach" en multithreadé
     * La fonction à appliquer ne prend rien et ne renvoie rien
     * Appel bloquant (utilisation de join sur chaque thread)
     * 
     * @param <V> Le type des éléments la liste sur laquelle itérer
     * @param list La liste des éléments sur laquelle itérer
     * @param func La fonction (sans input, sans valeur de retour) à effectuer sur chaque élément
     */
    public static <V> void forEachThread(List<V> list, Runnable func){
        List<Thread> mapThreads = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Thread mapThread = new Thread(func);
            mapThreads.add(mapThread);
            mapThread.start();
        }

        // Wait for all threads to finish
        for (Thread mapThread : mapThreads) {
            try {
                mapThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
