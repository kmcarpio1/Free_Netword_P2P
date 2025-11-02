#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

/**
 * Handler for error event
*/
void anyRequestHandler(int sockfd, char * error) {

    // Define a default message
    char * def = "Invalid command \n";

    // Replace by a custom message if precised
    char message[100];
    if(error == NULL) strcpy(message, def);
    else strcpy(message, error);

    // Add a return after the request
    message[strlen(message) - 1] = '\n';

    // Send the request
    if (send(sockfd, message, strlen(message), 0) < 0) {
        perror("Error when trying to send data.\n");
        exit(EXIT_FAILURE);
    }

}