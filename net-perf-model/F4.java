import java.io.*;
import java.lang.Process;
import java.lang.Thread;
import java.lang.ProcessBuilder;
import java.util.concurrent.*;

class Stage extends Thread {
LinkedBlockingQueue<byte[]> in;
LinkedBlockingQueue<byte[]> out;
volatile boolean done = false;
  public Stage(LinkedBlockingQueue<byte[]> in,
               LinkedBlockingQueue<byte[]> out) {
    this.in = in;
    this.out = out;
  }

  public void fin() { done = true; }

  public void run() {
    try {
    while (!done) {
      out.put(in.take().clone());
    }
    } catch (InterruptedException e) {
    }
  }
}

class Consumer extends Thread {
LinkedBlockingQueue<byte[]> in;
volatile boolean done = false;
  public Consumer(LinkedBlockingQueue<byte[]> in) {
    this.in = in;
  }

  public void fin() { done = true; }

  public void run() {
    try {
    while (!done) {
      in.take().clone();
    }
    } catch (InterruptedException e) {
    }
  }
}

public class F4 {
  static int packetSize = 1500;
  public static void main(String[] args) throws IOException, InterruptedException {
    int n = 1000 * 1000;

    if (args.length == 1) { packetSize = Integer.parseInt(args[0]); }

    // Warmup the JVM...
    for (int i = 0; i < 500; ++i) { double ignore = runStages(10); }

    double elapsedS = runStages(n);
    String pps = String.format("%.2f", n / elapsedS);
    System.out.println("threads w/copy " + n + " packets took " + elapsedS + "s for " + pps + " packets per second.");
  }

  public static double runStages(int n) throws IOException, InterruptedException {
    byte[][] packets = preMadePackets();

    LinkedBlockingQueue<byte[]> q1 = new LinkedBlockingQueue<byte[]>(100);
    LinkedBlockingQueue<byte[]> q2 = new LinkedBlockingQueue<byte[]>(100);
    LinkedBlockingQueue<byte[]> q3 = new LinkedBlockingQueue<byte[]>(100);
    LinkedBlockingQueue<byte[]> q4 = new LinkedBlockingQueue<byte[]>(100);

    Stage s1 = new Stage(q1, q2);
    Stage s2 = new Stage(q2, q3);
    Stage s3 = new Stage(q3, q4);
    Consumer sf = new Consumer(q4);

    s1.start();
    s2.start();
    s3.start();
    sf.start();

    long startTime = System.nanoTime();
    for (int i = 0; i < n; i++) {
      byte[] packet = packets[i % packets.length];
      q1.put(packet);
    }
    long endTime = System.nanoTime();

    s1.interrupt();
    s2.interrupt();
    s3.interrupt();
    sf.interrupt();
    s1.fin();
    s2.fin();
    s3.fin();
    sf.fin();

    double elapsedS = (endTime - startTime) / 1e9;
    return elapsedS;
  }

  public static byte[][] preMadePackets() {
    int n = 100;
    byte[][] packets = new byte[n][];
    for (int i = 0; i < n; ++i) {
      packets[i] = new byte[packetSize];
    }
    return packets;
  }

  public static byte[] stage1(byte[] in0) {
    return in0.clone();
  }

  public static byte[] stage2(byte[] in1) {
    return in1.clone();
  }

  public static byte[] stage3(byte[] in2) {
    return in2.clone();
  }

}


