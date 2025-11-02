#ifndef __SIG_HANDLE_H__
#define __SIG_HANDLE_H__

void sig_handleSigInt(void (*sighandler_t)(int));

void sig_handleSigPipe(void (*sighandler_t)(int));

#endif