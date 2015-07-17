#include <stdio.h>
#include <string.h>

#include <time.h>
// #include <sys/time.h>

#include "coro.h"

coro_context c1 , c2 , c3 , mainctx;
struct coro_stack s1, s2, s3;

int sz;

void shared_buf(void* b1, void* b2, int sz) { return; }
void copied_buf(void* b1, void* b2, int sz) { memcpy(b1, b2, sz); }

void stage3(void* ba, void* bb)
{
    shared_buf(ba, bb, sz);
}

void stage2(void* ba, void* bb)
{
    shared_buf(ba, bb, sz);
    stage3(ba, bb);
}

void stage1(void* ba, void* bb)
{
    shared_buf(ba, bb, sz);
    stage2(ba, bb);
}

int main(int argc, char **argv)
{
    int i, nbufs = 100;
    int packetsize = 1500;
    int iters = 1000 * 1000;

    if (argc == 2) { packetsize = atoi(argv[1]); }

    sz = packetsize;

    char** bufs = malloc(sizeof(char*) * nbufs);
    for (i = 0; i < nbufs; ++i) { bufs[i] = malloc(packetsize); memset(bufs[i], 0, packetsize); }

    char** bufs2 = malloc(sizeof(char*) * nbufs);
    for (i = 0; i < nbufs; ++i) { bufs2[i] = malloc(packetsize); memset(bufs2[i], 0, packetsize); }

    struct timespec ts;
    struct timespec te;
    clock_gettime(CLOCK_REALTIME, &ts);

    for (i = 0; i < iters; ++i) {
      void* ba = bufs[i % nbufs];
      void* bb = bufs2[i % nbufs];

      stage1(ba, bb);
    }

    clock_gettime(CLOCK_REALTIME, &te);

    time_t ds = te.tv_sec  - ts.tv_sec;
    long   dn = te.tv_nsec - ts.tv_nsec;
    double s  = (double) ds + (((double) dn) / 1e9);

    printf("c-fn nocopy throughput in 'packets' of size %d per second: %.2lf\n", packetsize, (double) iters / s);

    return 0;
}
