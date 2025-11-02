#ifndef _PEERS_H_
#define _PEERS_H_
#include "fileAvailableList.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct peers {
    int socket_id;
    int dest_port;
    struct peers * next;
};

/**
 * Allocate a block for a peer
*/
struct peers * intern_createPeerBlock();

/**
 * Insert a peer to the core list
*/
struct peers * core_insertPeer(int socket_id, int port);

/**
 * Print the list of current connected peers
*/
void core_printPeerList();

/**
 * Free a peer block
*/
void intern_freePeerBlock(struct peers * p);

/**
 * Get peers list
*/
struct peers * peers_();

/**
 * Recursive free peer block
*/
void intern_recursiveFreePeerBlock(struct peers * p);

/**
 * Recursive free for peer core list
*/
void core_freePeerList();

void intern_fillWithStringForPeer(char * b, struct peers * p);

/**
 * Get the owner
*/
struct peers * core_getOwner(int sockfd);

#endif