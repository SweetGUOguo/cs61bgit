package timingtest;

import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        int addlastcalltimes = 0;
        int lastN = 0;
        AList<Integer> list = new AList<>();
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        for (int i = 0; i <= 7; i++) {
            Stopwatch sw = new Stopwatch();
            for (int j = lastN; j < 1000 * (int) Math.pow(2, i); j++) {
                list.addLast(0);
                addlastcalltimes++;
            }
            lastN = 1000 * (int) Math.pow(2, i);
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(lastN);
            times.addLast(timeInSeconds);
            opCounts.addLast(addlastcalltimes);
        }
        TimeAList.printTimingTable(Ns, times, opCounts);
    }

}
