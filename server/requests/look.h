#include "../threadargs.h"
#include "../fileAvailableList.h"  

/**
 * Handler for "look" event
*/
void lookRequestHandler(char * request, struct ThreadArgs *args, int sockfd);

/**
 * Fill criteria for searching
*/
void getCriteria(enum criteria *c, char * search, char * criteria);