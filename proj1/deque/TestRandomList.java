package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestRandomList {
//    @Test
//    public void maxTest() {
//        MaxArrayDeque<Integer> correct = new MaxArrayDeque<>();
//        ArrayDeque<Integer> broken = new ArrayDeque<>();
//
//        correct.addLast(0);
//        correct.addLast(1);
//        broken.addLast(0);
//        broken.addLast(1);
//        assertEquals((Integer) 0, broken.get(0));
//
//    }
    @Test
    public void randomizedTest() {
        LinkedListDeque<Integer> correct = new LinkedListDeque<>();
        ArrayDeque<Integer> broken = new ArrayDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
//                System.out.println("length"+ broken.size() +"array firsr and next(" + broken.nextfirst + ")" + broken.nextlast);
                correct.addLast(randVal);
                broken.addLast(randVal);
                System.out.println("000000000000000000000000");
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = correct.size();
                System.out.println("1111111111111111111111111");
                System.out.println("size: " + size);
                assertEquals(correct.size(), broken.size());
            } else if (operationNumber == 2) {
                if (correct.size() > 0) {
                    correct.get(1);
//                    System.out.println("Last: " + correct.get(1));
                    assertEquals(correct.get(correct.size()-1), broken.get(correct.size()-1));
                } else {
                    continue;
                }
            } else if (operationNumber == 3) {
                if (correct.size() > 0) {
                    System.out.println("3333333333333333333333333333333333333");
                    System.out.println("newLast: correctsize" + correct.size() + " brokensize" + broken.size());
                    assertEquals(correct.removeLast(), broken.removeLast());
                } else {
                    continue;
                }
            }
            /*compare*/
            assertEquals(correct.size(), broken.size());
        }
    }
}
