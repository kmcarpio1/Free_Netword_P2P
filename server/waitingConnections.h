#ifndef _WAITING_CONNECTIONS_H_
#define _WAITING_CONNECTIONS_H_

/**
 * Initializises the waiting list and returns it
*/
void initWaitingList();

/**
 * Set a new socket to main listening socket
*/
int setSocketAsMain(int socket);

/**
 * Add a new socket to waiting connections list
*/
int addSocketToWaiting(int socket);

/**
 * Pops the next connection to handle
*/
int popWaitingConnection();

/**
 * Free the list
*/
int freeWaitingConnectionsList();

/**
 * Print the waiting connections (static variable)
*/
void printWaitingConnections();

#endif