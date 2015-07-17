import java.io.*;
import java.lang.Process;
import java.lang.ProcessBuilder;

public class F5 {
  public static void main(String[] args) throws IOException {
    int n = 1000 * 10;

    // Warmup the JVM...
    for (int i = 0; i < 50; ++i) { double ignore = runStages(10); }

    double elapsedS = runStages(n);
    String pps = String.format("%.2f", n / elapsedS);
    System.out.println(n + " packets took " + elapsedS + "s for " + pps + " packets per second.");
  }

  public static double runStages(int n) throws IOException {
    byte[][] packets = preMadePackets();

    ProcessBuilder cat1b = new ProcessBuilder("cat");
    ProcessBuilder cat2b = new ProcessBuilder("cat");
    ProcessBuilder cat3b = new ProcessBuilder("cat");

    cat1b.redirectOutput(cat2b.redirectInput());
    cat2b.redirectOutput(cat3b.redirectInput());
    cat3b.redirectOutput(new File("/dev/null"));

    Process cat1 = cat1b.start();
    Process cat2 = cat2b.start();
    Process cat3 = cat3b.start();

    OutputStream s = cat1.getOutputStream(); // output for us means input for the process

    long startTime = System.nanoTime();
    for (int i = 0; i < n; i++) {
      byte[] packet = packets[i % packets.length];
      s.write(packet);
      System.out.println("wrote packet " + i);
    }
    long endTime = System.nanoTime();

    cat1.destroy();
    cat2.destroy();
    cat3.destroy();

    double elapsedS = (endTime - startTime) / 1e9;
    return elapsedS;
  }

  public static byte[][] preMadePackets() {
    int n = 100;
    byte[][] packets = new byte[n][];
    for (int i = 0; i < n; ++i) {
      packets[i] = new byte[1500];
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


