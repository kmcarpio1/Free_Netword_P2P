#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "../utils/stringUtils.h"
#include "../fileAvailableList.h"
#include "../threadargs.h"
#include "../peers.h"
#include "any.h"

/**
 * Handler for "updateSeed" event
*/
void updateSeedRequestHandler(char * request, struct ThreadArgs *args, int sockfd) {

    // Get mutex
    pthread_mutex_t * mutex_files = args->mutex_files;
    pthread_mutex_t * mutex_pears = args->mutex_pears;

    // Parse initial request
    int cpt = 0;
    for(int i = 0; i < strlen(request) - 1; i++) {
        if(request[i] == ']' && cpt == 1) {
            request [i+1] = '\0';
            break;
        }
        if(request[i] == ']' && cpt == 0) cpt = 1;
    }
    request[strlen(request) - 1] = '\0';

    // Stack the first word
    char *token = strtok(request, " ");

    // Create empty placeholders
    struct fileAvailableList * fl = NULL;
    int noPort;
    char filename[255];
    int length;
    int pieceSize;
    char key[255];

    // Get the REGISTERED OWNER
    pthread_mutex_lock(mutex_pears);
    struct peers * owner = core_getOwner(sockfd);
    pthread_mutex_unlock(mutex_pears);

    // Redirect to error if owner is not registered
    if(owner == NULL) {
        anyRequestHandler(sockfd, "Error : Had you announce yourself on the network ?");
        return;
    }

    // Mode of parsing
    int mode = 0;
    // O : ...
    // 2 : Waiting for seeds
    // 3 : Filename to fill
    // 4 : Length to fill
    // 5 : Piece size to fill
    // 6 : Key to copy
    // 7 : Waiting for leechs
    // 8 : Key to copy

    // Begenning of parsing
    while (token != NULL) {
        
        if(mode == 0 && strcmp(token, "update") == 0) mode = 0;
        else if(mode == 2 && strcmp(token, "seed") == 0) mode = 3;

        // Parsing of filename
        else if(mode == 3) {
            if(token[0] == '[') {
                char * tmp = removeFirstChar(token);
                strcpy(filename, tmp);
            } else {
                strcpy(filename, token);
            }
            mode = 4;
        }

        // Parsing of length
        else if(mode == 4) {
            length = atoi(token);
            mode = 5;
        }

        // Parsing of piecesize
        else if(mode == 5) {
            pieceSize = atoi(token);
            mode = 6;
        }

        // Parsing of key and insertion of the file
        else if(mode == 6) {
            if(getLastCharacter(token) == ']') {
                char * tmp = removeLastChar(token);
                strcpy(key, tmp);
                mode = 7;
            } else {
                mode = 3;
                strcpy(key, token);
            }
            fl = aux_insertFile(filename, length, pieceSize, key, fl, owner);
        }

        else if(mode == 7 && strcmp(token, "leech") == 0) mode = 8;

        // Parsing of leechs
        else if(mode == 8) {
            char * key_updated = token;
            if(token[0] == '[') {
                key_updated = removeFirstChar(token);
            }
            if(getLastCharacter(token) == ']') {
                key_updated = removeLastChar(token);
            }
            struct peers * p[1];
            p[1] = owner;
            pthread_mutex_lock(mutex_files);
            core_addOwnersToFile(key_updated, p, 1);
            pthread_mutex_unlock(mutex_files);
        }

        // Redirect to error handler
        else {
            anyRequestHandler(sockfd);
        }

        token = strtok(NULL, " ");

    }

    printf("\n");
    printf("----------------------------- Actions -----------------------------\n");
    printf("\n");
    printf("Request to add the following files to the list :\n");
    aux_printFilesList(fl);
    printf("\n");

    pthread_mutex_lock(mutex_files);
    core_mergeFilesList(fl);
    pthread_mutex_unlock(mutex_files);

    fl = NULL;

    printf("Server data after insertion :\n");
    pthread_mutex_lock(mutex_files);
    core_printFilesList();
    pthread_mutex_unlock(mutex_files);
    printf("\n");

    printf("Owners :\n");
    pthread_mutex_lock(mutex_pears);
    core_printPeerList();
    pthread_mutex_unlock(mutex_pears);
    printf("\n");

    
    printf("------------------------------ DONE -------------------------------\n");

    // Send response
    char * message = "ok\n";
    if (send(sockfd, message, strlen(message), 0) < 0) {
        perror("Error when trying to send data.\n");
        exit(EXIT_FAILURE);
    }

}