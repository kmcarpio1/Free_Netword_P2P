#include "verbose.h"
#include <stdio.h>
#include <stdlib.h>

int serverStartup(char * ip, int portno) {

    printf("----------------------------------------------\n");
    printf("-                                            -\n");
    printf("-       ENSEIRB NETWORK PROJECT SERVER       -\n");
    printf("-                                            -\n");
    printf("----------------------------------------------\n");
    printf("\n");
    printf("Listening on port %d on interface %s \n", portno, ip);

    return EXIT_SUCCESS;

}