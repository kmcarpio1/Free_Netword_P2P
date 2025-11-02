#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <strings.h>
#include <unistd.h>
#include <sys/types.h> 
#include <pthread.h>

#include "verbose.h"
#include "waitingConnections.h"
#include "specificListener.h"
#include "newClientListener.h"
#include "peers.h"
#include "fileAvailableList.h"
#include "threadargs.h"
#include "sigHandle.h"

#define NB_LISTENING_THREADS 10

// Initialize list of files
struct seed * seeds;

// Create mutex and initialize args
pthread_mutex_t mutex_waiting = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t mutex_files = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t mutex_pears = PTHREAD_MUTEX_INITIALIZER;
struct ThreadArgs * args;

/**
 * Handler for CTRL+C
*/
void sigint_handler(int signal) {
    write(1, "\nInterrupt received, closing sockets...\n", 40);
    freeWaitingConnectionsList();
    core_freeFileList();
    core_freePeerList();
    exit(EXIT_SUCCESS);
}

/**
 * Handler for pipes
*/
void sigpipe_handler(int signum) {
    write(1, "\nSIGPIPE received\n", 19);
    
}

/**
 * Main loop
*/
int main(int argc, char ** argv) {

    // Use sig handlers
    sig_handleSigInt(sigint_handler);
    sig_handleSigPipe(sigpipe_handler);

    // Initialize seeds to NULL by default
    seeds = NULL;

    // Initialize list of waiting connections
    initWaitingList();

    // Set the args
    args = malloc(sizeof(struct ThreadArgs));
    args->seeds = seeds;
    args->mutex_waiting = &mutex_waiting;
    args->mutex_files = &mutex_files;
    args->mutex_pears = &mutex_pears;

    // Create specific threads for clients
    pthread_t thread_ids[NB_LISTENING_THREADS];
    int results[NB_LISTENING_THREADS];
    for(unsigned int i = 0; i < NB_LISTENING_THREADS; i++) {
        results[NB_LISTENING_THREADS] = pthread_create(&(thread_ids[i]), NULL, handleConnexion, args);
        if(results[NB_LISTENING_THREADS] != 0) {
            perror("pthread_create");
            exit(EXIT_FAILURE);
        }
    }
   
    // Create general thread for listening connexions
    pthread_t listening_thread_id;
    int result;
    result = pthread_create(&listening_thread_id, NULL, listenNewClients, args);
    if(result != 0) {
        perror("pthread_create");
        exit(EXIT_FAILURE);
    }
    result = pthread_join(listening_thread_id, NULL);
    if(result != 0) {
        perror("pthread_join");
        exit(EXIT_FAILURE);
    }

    // Free list of waiting connections
    freeWaitingConnectionsList();

    return 0;

}