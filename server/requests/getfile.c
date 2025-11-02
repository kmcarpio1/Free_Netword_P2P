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
 * Handler for "getFile" event
*/
void getfileRequestHandler(char * request, struct ThreadArgs *args, int sockfd) {

    // Get the mutex
    pthread_mutex_t * mutex_files = args->mutex_files;
    pthread_mutex_t * mutex_pears = args->mutex_pears;

    // Format the initial requests
    for(int i = 0; i < strlen(request); i++) {
        if(request[i] == '\n') {
            request[i] = '\0';
        }
    }

    // Stack the first word
    char *token = strtok(request, " ");
    
    // Initialize placeholders
    struct fileAvailableList * result = NULL;

    // Mode of parsing
    int mode = 0;
    // O : Waiting for key
    // 1 : key to copy

    // Begin parsing
    while (token != NULL) {
        
        
        if(mode == 0 && strcmp(token, "getfile") == 0) mode = 1;

        // Copy key and search in the core file list
        else if(mode == 1) {
            pthread_mutex_lock(mutex_files);
            result = core_search(KEY, token);
            pthread_mutex_unlock(mutex_files);
        }

        // Redirect to the error handler
        else {
            anyRequestHandler(sockfd, NULL);
        }

        token = strtok(NULL, " ");

    }

    printf("\n");
    printf("----------------------------- Actions -----------------------------\n");
    printf("\n");
    printf("Request to get the following file :\n");
    aux_printFilesList(result);
    printf("\n");
    
    printf("------------------------------ DONE -------------------------------\n");

    // Format the text to return the peers
    char texttotal[600];
    char buffer[20];
    char * beginning = "peers ";
    strcpy(texttotal, beginning);
    strcat(texttotal, result->key);
    strcat(texttotal, " [");
    for(int i = 0; i < result->filledOwners; i++) {
        pthread_mutex_lock(mutex_pears);
        intern_fillWithStringForPeer(buffer, result->owners[i]);
        pthread_mutex_unlock(mutex_pears);
        strcat(texttotal, buffer);
        strcat(texttotal, " ");
    }
    texttotal[strlen(texttotal) - 1] = '\0';
    strcat(texttotal, "]");
    strcat(texttotal, "\n");

    // Send response
    if (send(sockfd, texttotal, strlen(texttotal), 0) < 0) {
        perror("Error when trying to send data.\n");
        exit(EXIT_FAILURE);
    }

}