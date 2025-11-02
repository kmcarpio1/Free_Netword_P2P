#include "specificListener.h"

#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>

#include "verbose.h"
#include "log/log.h"
#include "waitingConnections.h"
#include "look.h"
#include "getfile.h"
#include "any.h"
#include "threadargs.h"
#include "announce.h"

/**
 * Get the first world of a string
*/
char* getFirstWord(const char *str) {

    if (str == NULL || *str == '\0') {
        // If the string is empty or NULL, return NULL
        return NULL;
    }

    // Find the length of the first word
    size_t word_length = 0;
    while (str[word_length] != '\0' && str[word_length] != ' ') {
        word_length++;
    }

    // Allocate memory to store the first word
    char *first_word = (char*)malloc((word_length + 1) * sizeof(char));
    if (first_word == NULL) {
        // Handle memory allocation failure
        return NULL;
    }

    // Copy the first word into the buffer
    strncpy(first_word, str, word_length);
    first_word[word_length] = '\0'; // Terminate the string with a null character

    return first_word;

}

/**
 * Handle connexion by parsing content and send it to different handlers 
*/
void * handleConnexion(void * arg) {

    // Get the mutex and the different parameters
    struct ThreadArgs *args = (struct ThreadArgs *)arg;
    struct waitingConnections *waiting = args->waiting;
    pthread_mutex_t * mutex_waiting = args->mutex_waiting;

    while(1) {

        // Ask for a new connection to manage
        int sockfd;

        while(1) {
            pthread_mutex_lock(mutex_waiting);
            sockfd = popWaitingConnection(waiting);
            pthread_mutex_unlock(mutex_waiting);
            if(sockfd > 0) break;
            sleep(1);
        }

        printf("Log: New client (%d) handled by specificListener.\n\n", sockfd);

        // Infinite loop
        while (1) {

            char buffer[2048];
            int n;
            
            n = recv(sockfd, buffer, sizeof(buffer), 0);
            if (n < 0) {
                perror("Error when listening ");
                break;
            } else if (n == 0) {
                printf("Connection closed.\n");
                break;
            }
            
            sleep(1);

            char* first_word = getFirstWord(buffer);

            if(strcmp(first_word, "announce") == 0) {
                printf("Type of request : announce | Received from: %d\n", sockfd);
                //new_entry_log(INFORMATIONAL, "announce" , 1, "announce received");
                announceRequestHandler(buffer, args, sockfd);
            }

            else if(strcmp(first_word, "look") == 0) {
                printf("Type of request : look | Received from: %d\n", sockfd); 
                lookRequestHandler(buffer, args, sockfd);
            }

            else if(strcmp(first_word, "getfile") == 0) {
                printf("Type of request : getfile | Received from: %d\n", sockfd); 
                getfileRequestHandler(buffer, args, sockfd);
            }

            else if(strcmp(first_word, "update") == 0) {
                printf("Type of request : update | Received from: %d\n", sockfd); 
                getfileRequestHandler(buffer, args, sockfd);
            }

            else {
                anyRequestHandler(sockfd, "Not in the list of possible commands.");
            }

            free(first_word);

        }

        // Fermeture du socket
        close(sockfd);
        sockfd = -1;

    }

    return EXIT_SUCCESS;

}