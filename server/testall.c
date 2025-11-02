#include <stdio.h>
#include <stdlib.h>
#include "peers.h"
#include "fileAvailableList.h"

int peersTest() {

    printf("--- Step 1 : Peers ---\n");

    struct peers * p = intern_createPeerBlock();
    if(p != NULL) {
        printf("OK - intern_createPeerBlock() - Allocate a new pair\n");
    } else {
        printf("FAILED - intern_createPeerBlock() - Allocate a new pair\n");
        return EXIT_FAILURE;
    }

    struct peers * inserted = core_insertPeer(5000, 10);
    if(inserted != NULL && peers_() != NULL && peers_()->socket_id == 5000 && peers_() == inserted && peers_()->next == NULL) {
        printf("OK - core_insertPeer() - Insert a new pair in the pair list\n");
    } else {
        printf("FAILED - core_insertPeer() - Insert a new pair in the pair list\n");
    }

    struct peers * inserted2 = core_insertPeer(10000, 10);
    if(inserted2 != NULL && peers_() != NULL && peers_()->socket_id == 10000 && peers_()->next != NULL && peers_()->next->socket_id == 5000) {
        printf("OK - core_insertPeer() - Insert a second new pair in the pair list\n");
    } else {
        printf("FAILED - core_insertPeer() - Insert a second new pair in the pair list\n");
    }

    core_freePeerList();

    return EXIT_SUCCESS;

}

int filesTest() {

    printf("--- Step 2 : Files ---\n");

    struct fileAvailableList * fal = intern_createFileBlock();
    if(fal != NULL) {
        printf("OK - intern_createFileBlock() - Allocate a new file\n");
    } else {
        printf("FAILED - intern_createFileBlock() - Allocate a new file\n");
        return EXIT_FAILURE;
    }

    struct fileAvailableList * list = NULL;

    struct peers * p = core_insertPeer(5000, 555);
    list = aux_insertFile("toto", 5000, 500, "exampleofkey", list, p);
    if(list != NULL && strcmp(list->filename, "toto") == 0 && list->filledOwners == 1 && list->owners[0] != NULL && list->owners[0] == p && peers_()->socket_id == 5000 && peers_()->next == NULL && list->next == NULL && strcmp(list->key, "exampleofkey") == 0) {
        printf("OK - aux_insertFile() - Insert a new file to an existing empty list\n");
    } else {
        printf("FAILED - aux_insertFile() - Insert a new file to an existing empty list\n");
        return EXIT_FAILURE;
    }

    struct peers * p2 = core_insertPeer(6000, 777);
    list = aux_insertFile("toto2", 5000, 500, "exampleofkey2", list, p2);
    if(list != NULL && strcmp(list->filename, "toto") == 0 && list->filledOwners == 1 && list->owners[0] != NULL && list->owners[0] == p && list->next != NULL && strcmp(list->next->filename, "toto2") == 0 && strcmp(list->next->key, "exampleofkey2") == 0 && list->next->filledOwners == 1 && list->next->owners[0] != NULL && list->next->owners[0] == p2 && list->next->next == NULL) {
        printf("OK - aux_insertFile() - Insert a new file to an existing not empty list\n");
    } else {
        printf("FAILED - aux_insertFile() - Insert a new file to an existing not empty list\n");
        return EXIT_FAILURE;
    }

    list = aux_insertFileByCloning(list, list);
    if(list != NULL && strcmp(list->filename, "toto") == 0 && list->filledOwners == 1 && list->owners[0] != NULL && list->owners[0] == p && list->next != NULL && strcmp(list->next->filename, "toto2") == 0 && list->next->filledOwners == 1 && list->next->owners[0] != NULL && list->next->owners[0] == p2 && list->next->next != NULL && strcmp(list->next->next->filename, "toto") == 0 && list->next->next->filledOwners == 1 && list->next->next->owners[0] != NULL && list->next->next->owners[0] == p) {
        printf("OK - aux_insertFileByCloning() - Insert a new file to an existing not empty list by cloning file block\n");
    } else {
        printf("%d", list->next->next->filledOwners);
        printf("FAILED - aux_insertFileByCloning() - Insert a new file to an existing not empty list by cloning file block\n");
        return EXIT_FAILURE;
    }

    core_freePeerList();

    struct fileAvailableList * to_merge = NULL;
    struct peers * p4 = core_insertPeer(5000, 500);
    to_merge = aux_insertFile("first", 100, 10, "azerty", to_merge, p4);
    to_merge = aux_insertFile("second", 100, 10, "uiopqs", to_merge, p4);

    core_mergeFilesList(to_merge);

    if(files_() != NULL && strcmp(files_()->filename, "second") == 0 && files_()->next != NULL && strcmp(files_()->next->filename, "first") == 0) {
        printf("OK - core_mergeFilesList() - Merge a created list with the core list\n");
    } else {
        printf("FAILED - core_mergeFilesList() - Merge a created list with the core list\n");
        return EXIT_FAILURE;
    }

    struct fileAvailableList * results = core_search(NAME, "first");


    if(results != NULL && results->next == NULL && strcmp(results->filename, "first") == 0 && results != files_()->next && results->filledOwners == 1 && results->owners[0] == p4) {
        printf("OK - core_search() - Search with name with only one result\n");
    } else {
        printf("FAILED - core_search() - Search with name with only one result\n");
    }

    struct fileAvailableList * results5 = core_search(NAME, "fierthrst");

    if(results5 == NULL) {
        printf("OK - core_search() - Search with name with no result\n");
    } else {
        printf("FAILED - core_search() - Search with name with no result\n");
    }

    to_merge = aux_insertFile("first", 100, 10, "hklkjj", to_merge, p4);

    struct fileAvailableList * results2 = core_search(NAME, "first");

    if(results2 != NULL && results2->next != NULL && results2->next->next == NULL && strcmp(results2->filename, "first") == 0 && results != files_()->next && results2->filledOwners == 1 && results2->owners[0] == p4 && strcmp(results2->next->filename, "first") == 0 && results2 != files_()->next->next && results2->next->filledOwners == 1 && results2->next->owners[0] == p4 && strcmp(results2->key, "azerty") == 0 && strcmp(results2->key, "hklkjj")) {
        printf("OK - core_search() - Search with name with many results\n");
    } else {
        printf("FAILED - core_search() - Search with name with many results\n");
    }

    struct fileAvailableList * results3 = core_search(KEY, "azerty");


    if(results3 != NULL && results3->next == NULL && strcmp(results3->filename, "first") == 0 && results3 != files_()->next && results3->filledOwners == 1 && results3->owners[0] == p4) {
        printf("OK - core_search() - Search with key with only one result\n");
    } else {
        printf("FAILED - core_search() - Search with key with only one result\n");
    }

    struct fileAvailableList * scd = NULL;
    scd = aux_insertFile("nanana", 100, 10, "hklkjj", scd, p4);

    struct fileAvailableList * results4 = core_search_restricted(KEY, "hklkjj", scd);

    if(results4 != NULL && results4->next == NULL && strcmp(results4->filename, "nanana") == 0 && results4 != scd && results4->filledOwners == 1 && results4->owners[0] == p4) {
        printf("OK - core_search_restricted() - Search with key with only one result with restriction\n");
    } else {
        printf("FAILED - core_search_restricted() - Search with key with only one result with restriction\n");
    }

    struct fileAvailableList * results6 = core_search_restricted(KEY, "azertghgfg", scd);

    if(results6 == NULL) {
        printf("OK - core_search_restricted() - Search with key with no result with restriction\n");
    } else {
        printf("FAILED - core_search_restricted() - Search with key with no result with restriction\n");
    }

    struct peers * p_add_1 = core_insertPeer(1000, 10);
    struct peers * p_add_2 = core_insertPeer(2000, 20);
    struct peers * p_add_3 = core_insertPeer(3000, 30);
    struct peers * p_add_4 = core_insertPeer(4000, 40);
    struct peers * p_add_5 = core_insertPeer(5000, 50);
    struct peers * p_add_6 = core_insertPeer(6000, 60);
    struct peers * p_add_7 = core_insertPeer(7000, 70);
    struct peers * p_add_8 = core_insertPeer(8000, 80);
    struct peers * p_add_9 = core_insertPeer(9000, 90);
    struct peers * p_add_10 = core_insertPeer(10000, 100);
    
    intern_addOwner(scd, p_add_1);

    if(scd->filledOwners == 2 && scd->nbOwners == 10 && scd->owners[1] == p_add_1 && scd->owners[0]->socket_id == 1000) {
        printf("OK - intern_addOwner() - Add an owner to an existing file\n");
    } else {
        printf("OK - intern_addOwner() - Add an owner to an existing file\n");
    }

    intern_addOwner(scd, p_add_2);

    if(scd->filledOwners == 3 && scd->nbOwners == 10 && scd->owners[1] == p_add_1 && scd->owners[1]->socket_id == 1000 && scd->owners[2] == p_add_2 && scd->owners[2]->socket_id == 2000) {
        printf("OK - intern_addOwner() - Add a second owner to an existing file\n");
    } else {
        printf("OK - intern_addOwner() - Add a second owner to an existing file\n");
    }

    intern_addOwner(scd, p_add_3);
    intern_addOwner(scd, p_add_4);
    intern_addOwner(scd, p_add_5);
    intern_addOwner(scd, p_add_6);
    intern_addOwner(scd, p_add_7);
    intern_addOwner(scd, p_add_8);
    intern_addOwner(scd, p_add_9);
    intern_addOwner(scd, p_add_10);

    if(scd->nbOwners == 20 && scd->filledOwners == 11 && scd->owners[10]->socket_id == 10000) {
        printf("OK - intern_addOwner() - Add multiple owners to an existing file\n");
    } else {
        printf("FAILED - intern_addOwner() - Add multiple owners to an existing file\n");
    }

    char stringToFill[100];
    intern_fillWithStringForBlock(scd, stringToFill);

    if(strcmp(stringToFill, "nanana 100 10 hklkjj") == 0) {
        printf("OK - intern_fillWithStringForBlock() - Generate a string for a file\n");
    } else {
        printf("FAILED - intern_fillWithStringForBlock() - Generate a string for a file\n");
    }

    struct peers * o[5];
    o[0] = p_add_3;
    o[1] = p_add_4;
    o[2] = p_add_5;
    o[3] = p_add_6;
    o[4] = p_add_7;

    core_addOwnersToFile("azerty", o, 5);

    struct fileAvailableList * getf = core_search(KEY, "azerty");

    if(getf != NULL && getf->next == NULL && getf->filledOwners == 6 && getf->nbOwners == 10) {
        printf("OK - core_addOwnersToFile() - Add multiple owners for an existing file in core list\n");
    } else {
        printf("FAILED - core_addOwnersToFile() - Add multiple owners for an existing file in core list\n");
    }

    return EXIT_SUCCESS;

}

int main() {

    printf("----- Network project - Server test suites -----\n\n");

    if(peersTest()) return EXIT_SUCCESS;
    if(filesTest()) return EXIT_SUCCESS;


}