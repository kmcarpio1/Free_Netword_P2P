#include "fileAvailableList.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "peers.h"
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>


struct peers * pr;

struct peers * intern_createPeerBlock() {
    struct peers * p = malloc(sizeof(struct peers));
    if(p == NULL) {
        perror("ERROR : When trying to allocate memory (peer block)");
        exit(EXIT_FAILURE);
    }
    return p;
}

struct peers * core_insertPeer(int socket_id, int port) {
    
    // Allocate a new block with the infos
    struct peers * peerblock = intern_createPeerBlock();
    peerblock->socket_id = socket_id;
    peerblock->dest_port = port;
    
    // If the core list if not empty let say that the next element of our new block is the list
    if(pr != NULL) {
        peerblock->next = pr;
        pr = peerblock;
        return peerblock;
    } else {
        pr = peerblock;
        return pr;
    }
    
}

void core_printPeerList() {
    struct peers * prbuf = pr;
    printf("Structure : (socketfd)\n");
    while(prbuf != NULL) {
        printf("PEER(%d)\n", prbuf->socket_id);
        prbuf = prbuf->next;
    }
}

struct peers * peers_() {
    return pr;
}

void intern_freePeerBlock(struct peers * p) {
    free(p);
}

void intern_recursiveFreePeerBlock(struct peers * p) {
    if(p->next != NULL) free(p->next);
    free(p);
}

void core_freePeerList() {
    if(pr == NULL) return;
    if(pr->next != NULL) free(pr->next);
    free(pr);
    pr = NULL;
}

void intern_fillWithStringForPeer(char * b, struct peers * p) {

    struct sockaddr_in addr;
    socklen_t len = sizeof(addr);

    int sockfd = p->socket_id;

    if (getsockname(sockfd, (struct sockaddr *)&addr, &len) == -1) {
        perror("getsockname");
        return;
    }

    char ip[INET_ADDRSTRLEN];
    char portAsString[10];
    inet_ntop(AF_INET, &(addr.sin_addr), ip, INET_ADDRSTRLEN);
    int port = p->dest_port;
    sprintf(portAsString, "%d", port);

    char dest[20];
    memset(dest, 0, 20);

    strcat(dest, ip);
    strcat(dest, ":");
    strcat(dest, portAsString);

    strcpy(b, dest);

}

struct peers * core_getOwner(int sockfd) {
    struct peers * buffer = pr;
    while(buffer != NULL) {
        if(buffer->socket_id == sockfd) {
            return buffer;
            break;
        }
    }
    return NULL;
}