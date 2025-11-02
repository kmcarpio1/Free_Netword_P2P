#include <stdlib.h>
#include <stdio.h>
#include <signal.h>
#include <sys/types.h>

#include "sigHandle.h"

void sig_handleSigInt(void (*sighandler_t)(int)){
    struct sigaction sa;
    sa.sa_handler = sighandler_t;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;
    if (sigaction(SIGINT, &sa, NULL) == -1) {
        perror("sigaction");
        exit(EXIT_FAILURE);
    }
}

void sig_handleSigPipe(void (*sighandler_t)(int)){
    struct sigaction sa;
    sa.sa_handler = sighandler_t;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;
    if (sigaction(SIGPIPE, &sa, NULL) == -1) {
        perror("sigaction");
        exit(EXIT_FAILURE);
    }
}
