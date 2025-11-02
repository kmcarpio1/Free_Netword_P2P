#include "waitingConnections.h"
#include <stdlib.h>
#include <stdio.h>
 #include <unistd.h>

struct waitingConnections {
    int socketfd;
    struct waitingConnections * next;
};

struct waitingConnections * waiting;
struct waitingConnections * listened;
int main_socket;

void initWaitingList(){
    waiting = NULL;
    listened = NULL;
    main_socket = -1;
}

int setSocketAsMain(int socket) {
    main_socket = socket;
    return EXIT_SUCCESS;
}

int addSocketToWaiting(int socket) {
    struct waitingConnections * wc = malloc(sizeof (struct waitingConnections));
    wc->next = waiting;
    wc->socketfd = socket;
    waiting = wc;
    return EXIT_SUCCESS;
}

int popWaitingConnection() {
    if (waiting == NULL) return -1;

    // We extract connection to return and make waiting head his next
    struct waitingConnections * toreturn = waiting;
    waiting = waiting->next;

    // We prepend returned connection to listened list
    toreturn->next = listened;
    listened = toreturn;
    
    return toreturn->socketfd;

}

int recursiveFree(struct waitingConnections * wc){
    if(wc != NULL) {
        recursiveFree(wc->next);
        close(wc->socketfd);
        wc->socketfd = -1;
        free(wc);
    }
    return 0;
}

int freeWaitingConnectionsList() {
    // Free main socket
    if (main_socket != -1) {
        close(main_socket);
    }
    main_socket = -1;

    // Free waiting sockets
    int value_wait = recursiveFree(waiting);
    waiting = NULL;

    // Free used sockets
    int value_used = recursiveFree(listened);
    listened = NULL;
    return value_wait + value_used;
}

void printWaitingConnections() {
    
    struct waitingConnections * bf = waiting;

    if (bf == NULL) printf("End of list\n");
    while(bf != NULL) {
        printf("A NEW CLIENT IS WAITING WITH IDENTIFIER : %d at address %p\n", bf->socketfd, bf);
        bf = bf->next;
    }
}