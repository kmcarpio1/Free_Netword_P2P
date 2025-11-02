#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "../utils/stringUtils.h"
#include "any.h"
#include "../fileAvailableList.h"
#include "../threadargs.h"
#include "../peers.h"

/**
 * Fill criteria for searching
*/
void getCriteria(enum criteria *c, char *search, const char *criteria) {

    // Initialize placeholders to paste parsed data
    char search_parsed[50];
    char criteria_parsed[50];
    char * ptx_string = criteria_parsed;

    // Counters
    int mode = 0;
    int step = 0;

    // Parsing
    for(int i = 0; i < strlen(criteria); i++) {
        if(mode == 0 && criteria[i] != '=' && criteria[i] != '\"') { ptx_string[step] = criteria[i]; step++; }
        else if(mode == 0 && criteria[i] == '=') {mode = 1; ptx_string = search_parsed; step = 0; }
        else if(mode == 1 && criteria[i] == '\"') { mode = 2; }
        else if(mode == 2 && criteria[i] == '\"') { ptx_string[step] = '\0'; }
        else if(mode == 2 && criteria[i] != '\"') { ptx_string[step] = criteria[i]; step++; }
        else { fprintf(stderr, "Error\n"); exit(EXIT_FAILURE); }
    }

    // Copy criteria
    if(strcmp(criteria_parsed, "filename") == 0) *c = NAME;
    if(strcmp(criteria_parsed, "key") == 0) *c = KEY;

    // Copy search keyword
    strcpy(search, search_parsed);

}

/**
 * Handler for "look" event
*/
void lookRequestHandler(char * request, struct ThreadArgs *args, int sockfd) {

    // Get mutex
    pthread_mutex_t * mutex_files = args->mutex_files;
    //pthread_mutex_t * mutex_pears = args->mutex_pears;

    // Format initial request
    for(int i = 0; i < strlen(request) - 1; i++) {
        if(request[i] == ']') {
            request [i+1] = '\0';
            break;
        }
    }

    // Stack the first word
    char *token = strtok(request, " ");

    // Initialize placeholders
    struct fileAvailableList * results = NULL;
    char search[50];
    enum criteria c = NAME;

    // Mode of parsing
    int mode = 0;
    // O : Waiting
    // 1 : First criteria to parse
    // 2 : Other criteria to parse

    // Beginning of parsing
    while (token != NULL) {
        
        if(mode == 0 && strcmp(token, "look") == 0) mode = 1;

        // Parsing of first criteria
        else if(mode == 1 && token[0] == '[') {
            char * criteria = removeFirstChar(token);
            if(getLastCharacter(criteria) == ']') {
                criteria = removeLastChar(criteria);
                getCriteria(&c, search, criteria);
                pthread_mutex_lock(mutex_files);
                results = core_search(c, search);
                pthread_mutex_unlock(mutex_files);
                mode = 3;
            } else {
                getCriteria(&c, search, criteria);
                pthread_mutex_lock(mutex_files);
                results = core_search(c, search);
                pthread_mutex_unlock(mutex_files);
                mode = 2;
            }

        }

        // Parsing of following criteria
        else if(mode == 2) {
            if(getLastCharacter(token) == ']') {
                token = removeLastChar(token);
                getCriteria(&c, search, token);
                pthread_mutex_lock(mutex_files);
                results = core_search_restricted(c, search, results);
                pthread_mutex_unlock(mutex_files);
                mode = 3;
            } else {
                getCriteria(&c, search, token);
                pthread_mutex_lock(mutex_files);
                results = core_search_restricted(c, search, results);
                pthread_mutex_unlock(mutex_files);
                mode = 2;
            }
        }

        // Redirect to error handler
        else {
            anyRequestHandler(sockfd, NULL);
        }

        token = strtok(NULL, " ");

    }

    printf("\n");
    printf("----------------------------- Actions -----------------------------\n");
    printf("\n");
    printf("Search results :\n");
    aux_printFilesList(results);
    printf("\n");
    
    printf("------------------------------ DONE -------------------------------\n");

    // Create the string formatted with file informations
    char textfile[100];
    char texttotal[600];
    char * beginning = "list [";
    char * ending = "]";
    strcpy(texttotal, beginning);
    struct fileAvailableList * buffer = results;
    while(buffer != NULL) {
        pthread_mutex_lock(mutex_files);
        intern_fillWithStringForBlock(buffer, textfile);
        pthread_mutex_unlock(mutex_files);
        strcat(texttotal, textfile);
        strcat(texttotal, " ");
        buffer = buffer->next;
    }
    strcat(texttotal, ending);
    texttotal[strlen(texttotal) - 2] = ']';
    texttotal[strlen(texttotal) - 1] = '\n';

    // Send response
    if (send(sockfd, texttotal, strlen(texttotal), 0) < 0) {
        perror("Error when trying to send data.\n");
        exit(EXIT_FAILURE);
    }

}