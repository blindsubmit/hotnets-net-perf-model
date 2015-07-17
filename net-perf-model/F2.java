public class F2 {
  static int packetSize = 1500;
  public static void main(String[] args) {
    int n = 1000 * 1000;

    if (args.length == 1) { packetSize = Integer.parseInt(args[0]); }

    // Warmup the JVM...
    for (int i = 0; i < 5000; ++i) { double ignore = runStages(10); }

    double elapsedS = runStages(n);
    String pps = String.format("%.2f", n / elapsedS);
    System.out.println("fn w/copy " + n + " size-" + packetSize + " packets took " + elapsedS + "s for " + pps + " packets per second.");
  }

  public static double runStages(int n) {
    byte[][] packets = preMadePackets();
    long startTime = System.nanoTime();
    for (int i = 0; i < n; i++) {
      byte[] packet = packets[i % packets.length];
      byte[] result = stage3(stage2(stage1(packet)));
    }
    long endTime = System.nanoTime();
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


