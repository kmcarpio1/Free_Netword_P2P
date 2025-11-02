#ifndef _THREAD_ARGS_H_
#define _THREAD_ARGS_H_
#include <pthread.h>

struct ThreadArgs {
    struct seed *seeds;
    struct waitingConnections *waiting;
    pthread_mutex_t * mutex_waiting;
    pthread_mutex_t * mutex_files;
    pthread_mutex_t * mutex_pears;
};

#endif