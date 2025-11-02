#ifndef _FILE_AVAILABLE_LIST_H_
#define _FILE_AVAILABLE_LIST_H_

#include "peers.h"

struct fileAvailableList {
    char filename[255];
    int length;
    int pieceSize;
    char key[255];
    struct fileAvailableList * next;
    struct peers ** owners;
    int nbOwners;
    int filledOwners;
};

enum criteria {
    NAME,
    KEY
};

/**
 * Create a block for a file
*/
struct fileAvailableList * intern_createFileBlock();

/**
 * Free a file block
*/
void intern_freeFileBlock(struct fileAvailableList * block);

/**
 * Insert a new file in a list of files
*/
struct fileAvailableList * aux_insertFile(char * filename, int length, int pieceSize, char * key, struct fileAvailableList * fal, struct peers * owner);

/**
 * Insert a new file in a list of files but by cloning the provided block
*/
struct fileAvailableList * aux_insertFileByCloning(struct fileAvailableList * file, struct fileAvailableList * list);

/**
 * Merge the provided list of files to the core list
*/
void core_mergeFilesList(struct fileAvailableList * l);

/**
 * Print the content of a provided list
*/
void aux_printFilesList(struct fileAvailableList * fal);

/**
 * Print the content of the core list
*/
void core_printFilesList();

/**
 * Return a new chained list with results
*/
struct fileAvailableList * core_search(enum criteria c, char * search);

/**
 * Return a new chained list with results (restricted)
*/
struct fileAvailableList * core_search_restricted(enum criteria c, char * search, struct fileAvailableList * restrict_);

/**
 * Add an owner to an existing fileblock
*/
void intern_addOwner(struct fileAvailableList * fblock, struct peers * owner);

/**
 * Generate a string for file block
*/
void intern_fillWithStringForBlock(struct fileAvailableList * fal, char * dest);

/**
 * Free a list of file blocks
*/
void intern_recursiveFreeFileBlock(struct fileAvailableList * block);

/**
 * Add the owners to the file with specified key
*/
void core_addOwnersToFile(char * key, struct peers ** owners, int nbFilled);

/**
 * Free the list
*/
void core_freeFileList();

/**
 * Get the filelist
*/
struct fileAvailableList * files_();

#endif