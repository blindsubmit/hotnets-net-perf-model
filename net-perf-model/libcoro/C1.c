#include <stdio.h>
#include <string.h>

#include <time.h>
// #include <sys/time.h>

#include "coro.h"

coro_context c1 , c2 , c3 , mainctx;
void* ba;
void* bb;
struct coro_stack s1, s2, s3;

int sz;

void shared_buf(void* b1, void* b2, int sz) { return; }
void copied_buf(void* b1, void* b2, int sz) { memcpy(b1, b2, sz); }

void stage1(void *arg)
{
  while (1) {
    shared_buf(ba, bb, sz);
    coro_transfer(&c1, &c2);
    coro_transfer(&c1, &mainctx);
  }
}

void stage2(void *arg)
{
  while (1) {
    shared_buf(ba, bb, sz);
    coro_transfer(&c2, &c3);
    coro_transfer(&c2, &c1);
  }
}

void stage3(void *arg)
{
  while (1) {
    shared_buf(ba, bb, sz);
    coro_transfer(&c3, &c2);
  }
}

int main(int argc, char **argv)
{
    int i, nbufs = 100;
    int packetsize = 1500;
    int iters = 1000 * 1000;

    if (argc == 2) { packetsize = atoi(argv[1]); }

    sz = packetsize;

    coro_create(&mainctx, NULL, NULL, NULL, 0);
    coro_stack_alloc(&s1, 0);
    coro_stack_alloc(&s2, 0);
    coro_stack_alloc(&s3, 0);
    coro_create(&c1, stage1, NULL, s1.sptr, s1.ssze);
    coro_create(&c2, stage2, NULL, s2.sptr, s2.ssze);
    coro_create(&c3, stage3, NULL, s3.sptr, s3.ssze);

    char** bufs = malloc(sizeof(char*) * nbufs);
    for (i = 0; i < nbufs; ++i) { bufs[i] = malloc(packetsize); memset(bufs[i], 0, packetsize); }

    char** bufs2 = malloc(sizeof(char*) * nbufs);
    for (i = 0; i < nbufs; ++i) { bufs2[i] = malloc(packetsize); memset(bufs2[i], 0, packetsize); }

    struct timespec ts;
    struct timespec te;
    clock_gettime(CLOCK_REALTIME, &ts);

    for (i = 0; i < iters; ++i) {
      ba = bufs[i % nbufs];
      bb = bufs2[i % nbufs];

      coro_transfer(&mainctx, &c1);
    }

    clock_gettime(CLOCK_REALTIME, &te);

    time_t ds = te.tv_sec  - ts.tv_sec;
    long   dn = te.tv_nsec - ts.tv_nsec;
    double s  = (double) ds + (((double) dn) / 1e9);

    printf("coro nocopy throughput in 'packets' of size %d per second: %.2lf\n", packetsize, (double) iters / s);

    return 0;
}
