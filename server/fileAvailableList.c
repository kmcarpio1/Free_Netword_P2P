#include "fileAvailableList.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Declare a structure for available files
struct fileAvailableList * files;

/**
 * Create a block for a file
*/
struct fileAvailableList * intern_createFileBlock() {

    struct fileAvailableList * fblock = malloc(sizeof(struct fileAvailableList));
    if(fblock == NULL) {
        perror("ERROR : When trying to allocate memory (file block)");
        exit(EXIT_FAILURE);
    }

    fblock->owners = malloc(10 * sizeof(struct peers *));
    if(fblock->owners == NULL) {
        perror("ERROR : When trying to allocate memory (owners table in file block)");
        exit(EXIT_FAILURE);
    }

    fblock->nbOwners = 10;
    fblock->filledOwners = 0;
    return fblock;

}

/**
 * Free a file block
*/
void intern_freeFileBlock(struct fileAvailableList * block) {
    free(block->owners);
    free(block);
}

/**
 * Free a list of file blocks
*/
void intern_recursiveFreeFileBlock(struct fileAvailableList * block) {

    if(block == NULL) return;

    intern_recursiveFreeFileBlock(block->next);

    free(block->owners);
    free(block);
    block->owners = NULL;
    block = NULL;
    
}

/**
 * Insert a new file in a list of files
*/
struct fileAvailableList * aux_insertFile(char * filename, int length, int pieceSize, char * key, struct fileAvailableList * fal, struct peers * owner) {
    
    // Create a new block for the file and fill with info
    struct fileAvailableList * fblock = intern_createFileBlock();
    strcpy(fblock->filename, filename);
    fblock->length = length;
    fblock->pieceSize = pieceSize;
    intern_addOwner(fblock, owner);
    strcpy(fblock->key, key);
    fblock->next = NULL;
    
    // If there is no files in the list then just return the block 
    if(fal == NULL) {
       return fblock; 
    // Else add the block at the end of the file list
    } else {
        struct fileAvailableList * bf = fal;
        while(bf->next != NULL) bf = bf->next;
        bf->next = fblock;
        return fal;
    }

}

/**
 * Insert a new file in a list of files but by cloning the provided block
*/
struct fileAvailableList * aux_insertFileByCloning(struct fileAvailableList * file, struct fileAvailableList * list) {
    
    // Create a new block for the file and fill with info
    struct fileAvailableList * fblock = intern_createFileBlock();
    if(fblock->owners == NULL) {
        perror("ERROR : When trying to allocate memory (file block)");
        exit(EXIT_FAILURE);
    }

    strcpy(fblock->filename, file->filename);
    fblock->length = file->length;
    fblock->pieceSize = file->pieceSize;
    for(int i = 0; i < file->filledOwners; i++) {
        intern_addOwner(fblock, file->owners[i]);
    }
    strcpy(fblock->key, file->key);
    
    // If there is no files in the list then just return the block 
    if(list == NULL) {
       return fblock; 

    // Else add the block at the end of the file list
    } else {
        struct fileAvailableList * bf = list;
        while(bf->next != NULL) bf = bf->next;
        bf->next = fblock;
        return list;
    }

}

/**
 * Merge the provided list of files to the core list
*/
void core_mergeFilesList(struct fileAvailableList * l) {

    // Iterate on each fileblock and add it to the core list if it is not existing
    struct fileAvailableList * buffer = l;
    struct fileAvailableList * tmp;
    while(buffer != NULL) {
        struct fileAvailableList * matches = core_search(KEY, buffer->key);
        tmp = buffer->next;
        if(matches == NULL) {
            buffer->next = files;
            files = buffer;
        } else {
            core_addOwnersToFile(buffer->key, buffer->owners, buffer->filledOwners);
            intern_freeFileBlock(buffer);
        }
        buffer = tmp;
        intern_recursiveFreeFileBlock(matches);
    }

}

/**
 * Print the content of a provided list
*/
void aux_printFilesList(struct fileAvailableList * fal) {
    struct fileAvailableList * bf = fal;
    printf("---------------------------\n");
    while(bf != NULL) {
        printf("Filename : %s\n", bf->filename);
        printf("Length : %d\n", bf->length);
        printf("Piece Size : %d\n", bf->pieceSize);
        printf("Key : %s\n", bf->key);
        printf("Clients (%d):\n", bf->filledOwners);
        for(int i = 0; i < bf->filledOwners; i++) {
            printf("- %d\n", bf->owners[i]->socket_id);
        }
        bf = bf->next;
    }
    printf("---------------------------\n");
}

/**
 * Print the content of the core list
*/
void core_printFilesList() {
    struct fileAvailableList * bf = files;
    printf("---------------------------\n");
    while(bf != NULL) {
        printf("Filename : %s\n", bf->filename);
        printf("Length : %d\n", bf->length);
        printf("Piece Size : %d\n", bf->pieceSize);
        printf("Key : %s\n", bf->key);
        printf("Clients (%d):\n", bf->filledOwners);
        for(int i = 0; i < bf->filledOwners; i++) {
            printf("- %d\n", bf->owners[i]->socket_id);
        }
        bf = bf->next;
    }
    printf("---------------------------\n");
}

/**
 * Return a new chained list with results
*/
struct fileAvailableList * core_search(enum criteria c, char * search) {

    struct fileAvailableList * results = NULL;

    if(c == KEY) {
        
        struct fileAvailableList * buffer = files;
        while(buffer != NULL) {
            if(strcmp(buffer->key, search) == 0) results = aux_insertFileByCloning(buffer, results);
            buffer = buffer->next;
        }

    }

    if(c == NAME) {
        
        struct fileAvailableList * buffer = files;
        while(buffer != NULL) {
            if(strcmp(buffer->filename, search) == 0) results = aux_insertFileByCloning(buffer, results);
            buffer = buffer->next;
        }

    }

    return results;

}

/**
 * Return a new chained list with results (restricted)
*/
struct fileAvailableList * core_search_restricted(enum criteria c, char * search, struct fileAvailableList * restrict_) {

    struct fileAvailableList * results = NULL;

    if(c == KEY) {
        
        struct fileAvailableList * buffer = restrict_;
        while(buffer != NULL) {
            if(strcmp(buffer->key, search) == 0) results = aux_insertFileByCloning(buffer, results);
            buffer = buffer->next;
        }

    }

    if(c == NAME) {
        
        struct fileAvailableList * buffer = restrict_;
        while(buffer != NULL) {
            if(strcmp(buffer->filename, search) == 0) results = aux_insertFileByCloning(buffer, results);
            buffer = buffer->next;
        }

    }

    return results;

}

/**
 * Add an owner to an existing fileblock
*/
void intern_addOwner(struct fileAvailableList * fblock, struct peers * owner) {

    if(fblock->filledOwners == fblock->nbOwners) {
        fblock->owners = realloc(fblock->owners, fblock->nbOwners * 2 * sizeof(struct peers *));
        if(fblock->owners == NULL) {
            perror("ERROR : When trying to REallocate memory (owners table in file block)");
            exit(EXIT_FAILURE);
        }
        fblock->nbOwners = fblock->nbOwners * 2;
    }

    fblock->owners[fblock->filledOwners] = owner;
    fblock->filledOwners++;

}

void intern_fillWithStringForBlock(struct fileAvailableList * fal, char * dest) {
    sprintf(dest, "%s %d %d %s", fal->filename, fal->length, fal->pieceSize, fal->key);
}

/**
 * Add the owners to the file with specified key
*/
void core_addOwnersToFile(char * key, struct peers ** owners, int nbFilled) {

    struct fileAvailableList * buffer = files;
    while(buffer != NULL) {
        if(strcmp(buffer->key, key) == 0) {
            for(int i = 0; i < nbFilled; i++) {
                intern_addOwner(buffer, owners[i]);
            }
            break; 
        }
        buffer = buffer->next;
    }

}

struct fileAvailableList * files_() {
    return files;
}

void core_freeFileList() {
    
    if(files == NULL) return;

    intern_recursiveFreeFileBlock(files);

    free(files->owners);
    free(files);
    files->owners = NULL;
    files = NULL;

}