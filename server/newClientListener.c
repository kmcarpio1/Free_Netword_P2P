#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <strings.h>
#include <unistd.h>
#include <sys/types.h> 

#include "verbose.h"
#include "waitingConnections.h"
#include "threadargs.h"

#define BACKLOG 100

int get_port_number() {

    FILE *file = fopen("config.ini", "r");

    if (file == NULL) {
        perror("Erreur lors de l'ouverture du fichier");
        return -1;
    }

    char line[100];
    int port_number = -1;

    while (fgets(line, sizeof(line), file)) {
        if (strncmp(line, "tracker-listen-port = ", 20) == 0) {
            sscanf(line + 7, "%d", &port_number);
            break;
        }
    }

    fclose(file);
    return port_number;
    
}

/**
 * Function listening new clients connection
*/
void * listenNewClients(void * arg) {

    // Get the mutex
    struct ThreadArgs *args = (struct ThreadArgs *)arg;
    pthread_mutex_t * mutex_waiting = args->mutex_waiting;

    // Todo parse INI file
    int portno = get_port_number() == -1 ? 6666 : get_port_number();
    char * servaddr = "0.0.0.0";

    // Network configuration
    int sockfd;
    struct sockaddr_in serv_addr;
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(portno);
    inet_pton(AF_INET, servaddr, &serv_addr.sin_addr.s_addr);

    // Create socket
    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("ERROR opening socket");
        exit(EXIT_FAILURE);
    }
    setSocketAsMain(sockfd);

    // Bind socket to network config
    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
        perror("ERROR on binding");
        exit(EXIT_FAILURE);
    }

    // Start listening
    if (listen(sockfd, BACKLOG) < 0) {
        perror("ERROR on listen");
        close(sockfd);
        exit(EXIT_FAILURE);
    }
    
    // Print server start message
    serverStartup(servaddr, portno);
    
    // Accept incoming
    struct sockaddr_in cli_addr;
    socklen_t clilen = sizeof(cli_addr);
    int newsockfd;
    
    while (1) {
        
        // Waiting for a new connection to arrive
        if ((newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen)) < 0) {
            perror("Erreur lors de l'acceptation de la connexion");
            close(sockfd);
        }

        // Socket accepted, adding it to connection waiting list
        pthread_mutex_lock(mutex_waiting);
        addSocketToWaiting(newsockfd);
        pthread_mutex_unlock(mutex_waiting);
        
    }

    return EXIT_SUCCESS;
    
}