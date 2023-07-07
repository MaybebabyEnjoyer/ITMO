package queue;
import java.util.*;

public class QueueTestXD {
    static ArrayList<Object> a = new ArrayList<>();
    static Random random = new Random();
    static ArrayQueue ahkj = new ArrayQueue();
    static LinkedQueue ahkj2 = new LinkedQueue();

    public static void fill() {
        for (int i = 1; i <= 25; i++) {
            a.add(i);
            ArrayQueueModule.enqueue(i);
        }
    }

    public static void dump() {
        int i = 0;
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println("Size: " + ArrayQueueModule.size() + " Element: " + ArrayQueueModule.element());
            assert a.get(i).equals(ArrayQueueModule.dequeue()) : "Error in enqueue/dequeue. Expected: " + a.get(i) + " Actual: " + ArrayQueueModule.element();
            i++;
        }
        a.clear();
    }

    public static void dequeTest() {
        Deque<Object> dequeExample = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            int coin = random.nextInt(2);
            if (coin == 0) {
                long j = random.nextLong();
                dequeExample.addFirst(j);
                ArrayQueueModule.push(j);
            } else {
                if (!dequeExample.isEmpty()) {
                    assert dequeExample.peekLast().equals(ArrayQueueModule.peek()) : "Error in peek. Expected: " + dequeExample.peekLast() + " Actual: " + ArrayQueueModule.peek();
                    assert dequeExample.removeLast().equals(ArrayQueueModule.remove()) : "Error in dequeue. Expected: " + dequeExample.removeLast() + " Actual: " + ArrayQueueModule.dequeue();
                }
                else {
                    assert ArrayQueueModule.isEmpty() : "Error in isEmpty. Expected: true Actual: false";
                }
            }
        }
        dequeExample.clear();
        ArrayQueueModule.clear();
    }

    //Тут можно дописать чтобы также тестило штуки по типу стрингов/чаров/пустых мап (как в тестах ГК)
    //Но общую идею которую я хотел использовать для этих тестов я постарался передать.
    public static void toArrayTest(Boolean testingLinkedQueue) {
        Object[] arr = new Object[0];
        assert Arrays.equals(ArrayQueueModule.toArray(), arr) : "Error in toArray. Expected: " + Arrays.toString(arr) + " Actual: "
                + Arrays.toString(ArrayQueueModule.toArray());
        arr = new Object[]{1};
        ArrayQueueModule.enqueue(1);
        assert Arrays.equals(ArrayQueueModule.toArray(), arr) : "Error in toArray. Expected: " + Arrays.toString(arr) + " Actual: "
                + Arrays.toString(ArrayQueueModule.toArray());
        ArrayQueueModule.clear();
        arr = new Object[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int i = 1; i <= 10; i++) {
            ArrayQueueModule.enqueue(i);
            if (testingLinkedQueue) {
                ahkj2.enqueue(i);
            }
        }
            assert Arrays.equals(ArrayQueueModule.toArray(), arr) : "Error in toArray. Expected: " + Arrays.toString(arr) + " Actual: "
                    + Arrays.toString(ArrayQueueModule.toArray());
        assert !testingLinkedQueue || Arrays.equals(ahkj2.toArray(), arr) : "Error in toArray. Expected: " + Arrays.toString(arr) + " Actual: "
                + Arrays.toString(ahkj2.toArray());
        ArrayQueueModule.clear();
        ahkj2.clear();
        int k = 20;
        for (int i = 1; i <= k; i++) {
            int j = random.nextInt(100);
            arr = new Object[j];
            for (int l = 0; l < j; l++) {
                long n = random.nextLong();
                arr[l] = n;
                ArrayQueueModule.enqueue(n);
                if (testingLinkedQueue) {
                    ahkj2.enqueue(n);
                }
            }
            assert Arrays.equals(ArrayQueueModule.toArray(), arr) : "Error in toArray. Expected: " + Arrays.toString(arr) + System.lineSeparator()
                    + "Actual " + Arrays.toString(ArrayQueueModule.toArray());
            assert !testingLinkedQueue || Arrays.equals(ahkj2.toArray(), arr) : "Error in toArray. Expected: " + Arrays.toString(arr) + System.lineSeparator();
            ArrayQueueModule.clear();
            ahkj2.clear();
        }
        ArrayQueueModule.clear();
        a.clear();
        ahkj2.clear();
    }

    public static void setGetTest(boolean testingLinkedQueue) {
        for (int i = 0; i < 10; i++) {
            long j = random.nextLong();
            ArrayQueueModule.enqueue(j);
            a.add(j);
            if (testingLinkedQueue) {
                ahkj2.enqueue(j);
            }
        }
        for (int i = 0; i < 1000; i++) {
            int coin = random.nextInt(2);
            if (coin == 0) {
                long j = random.nextLong();
                int index = random.nextInt(ArrayQueueModule.size());
                ArrayQueueModule.set(index, j);
                a.set(index, j);
                if (testingLinkedQueue) {
                    ahkj2.set(index, j);
                }
                assert Arrays.equals(ArrayQueueModule.toArray(), a.toArray()) : "Error in set. Expected: " + Arrays.toString(a.toArray()) + System.lineSeparator()
                        + "Actual " + Arrays.toString(ArrayQueueModule.toArray());
                assert !testingLinkedQueue || Arrays.equals(ahkj2.toArray(), a.toArray()) : "Error in set. Expected: " + Arrays.toString(a.toArray()) + System.lineSeparator();
            }
            if (coin == 1) {
                int index = random.nextInt(ArrayQueueModule.size());
                assert ArrayQueueModule.get(index).equals(a.get(index)) : "Error in get. Expected: " + a.get(index) + " Actual: " + ArrayQueueModule.get(index);
                assert !testingLinkedQueue || ahkj2.get(index).equals(a.get(index)) : "Error in get. Expected: " + a.get(index) + " Actual: " + ahkj2.get(index);
            }
        }
        a.clear();
        ArrayQueueModule.clear();
        ahkj2.clear();
    }

    public static void containsTest(boolean testingLinkedQueue) {
        for (int i = 0; i < 1000; i++) {
            long j = random.nextLong();
            ahkj.enqueue(j);
            a.add(j);
            if (testingLinkedQueue) {
                ahkj2.enqueue(j);
            }
        }
        for (int i = 0; i < 1000; i++) {
                long j = random.nextLong();
                assert (ahkj.contains(j) == a.contains(j)) : "Error in contains. Expected: " + a.contains(j) + " Actual: " + ahkj.contains(j);
                assert !testingLinkedQueue || (ahkj2.contains(j) == a.contains(j)) : "Error in contains. Expected: " + a.contains(j) + " Actual: " + ahkj2.contains(j);
        }
        a.clear();
        ahkj.clear();
        ahkj2.clear();
    }

    public static void removeFirstOccurrenceTest(boolean testingLinkedQueue) {
        for (int i = 0; i < 1000; i++) {
            long j = random.nextLong();
            ahkj.enqueue(j);
            a.add(j);
            if (testingLinkedQueue) {
                ahkj2.enqueue(j);
            }
        }
        for (int i = 0; i < 1000; i++) {
            long j = random.nextLong();
            if (Arrays.asList(a).contains(j)) {
                a.remove(j);
                ahkj.removeFirstOccurrence(j);
                if (testingLinkedQueue) {
                    ahkj2.removeFirstOccurrence(j);
                }
                assert Arrays.equals(ahkj.toArray(), a.toArray()) : "Error in removeFirstOccurrence. Expected: " + Arrays.toString(a.toArray()) + System.lineSeparator()
                        + "Actual " + Arrays.toString(ahkj.toArray());
                assert !testingLinkedQueue || Arrays.equals(ahkj2.toArray(), a.toArray()) : "Error in removeFirstOccurrence. Expected: " + Arrays.toString(a.toArray()) + System.lineSeparator();
            }
            else {
                assert !ahkj.removeFirstOccurrence(j) : "Error in removeFirstOccurrence. Expected: false Actual: true";
                assert !testingLinkedQueue || !ahkj2.removeFirstOccurrence(j) : "Error in removeFirstOccurrence. Expected: false Actual: true";
            }
        }
        a.clear();
        ahkj.clear();
        ahkj2.clear();
    }

// Я не смог придумать нормальные тесты для двух последних методов, но я пытался))
    public static void NthTest(boolean testingLinkedQueue) {
        for (int i = 0; i < 1000; i++) {
            long j = random.nextLong();
            ahkj.enqueue(j);
            a.add(j);
            ahkj2.enqueue(j);
        }
        for (int i = 0; i < 1000; i++) {
            int d3 = random.nextInt(3);
            if (d3 == 0) {
                ArrayQueue getTest = ahkj.getNth(2);
                LinkedQueue getTest1 = ahkj2.getNth(2);
                ArrayList<Object> getTest2 = new ArrayList<>();
                for (int j = 0; j < a.size(); j+= 2) {
                    getTest2.add(a.get(j));
                }
                assert Arrays.equals(getTest.toArray(), getTest2.toArray()) : "Error in getNth. Expected: " + Arrays.toString(getTest2.toArray()) + System.lineSeparator()
                        + "Actual " + Arrays.toString(getTest.toArray());
                assert !testingLinkedQueue || Arrays.equals(getTest1.toArray(), getTest2.toArray()) : "Error in getNth. Expected: " + Arrays.toString(getTest2.toArray()) + System.lineSeparator();
            }
//            if (d3 == 1) {
//                Queue remTest = ahkj.removeNth(2);
//                Queue remTest1 = ahkj2.removeNth(2);
//                ArrayList<Object> remTest2 = new ArrayList<>();
//                for (int j = 0; j < a.size(); j+= 2) {
//                    remTest2.add(a.get(j));
//                }
//                assert Arrays.equals(remTest.toArray(), remTest2.toArray()) : "Error in removeNth. Expected: " + Arrays.toString(remTest2.toArray()) + System.lineSeparator()
//                        + "Actual " + Arrays.toString(remTest.toArray());
//            }
        }
    }



    public static void fill2(ArrayQueue queue) {
        for (int i = 1; i <= 25; i++) {
            a.add(i);
            queue.enqueue(i);
        }
    }

    public static void dump2(ArrayQueue queue) {
        int i = 0;
        while (!queue.isEmpty()) {
            System.out.println("Size: " + queue.size() + " Element: " + queue.element());
            assert a.get(i).equals(queue.dequeue()) : "Error in enqueue/dequeue. Expected: " + a.get(i) + " Actual: " + queue.element();
            i++;
        }
        a.clear();
    }

    public static void fill3(ArrayQueueADT queue) {
        for (int i = 1; i <= 25; i++) {
            a.add(i);
            ArrayQueueADT.enqueue(queue, i);
        }
    }

    public static void dump3(ArrayQueueADT queue) {
        int i = 0;
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println("Size: " + ArrayQueueADT.size(queue) + " Element: " + ArrayQueueADT.element(queue));
            assert a.get(i).equals(ArrayQueueADT.dequeue(queue)) : "Error in enqueue/dequeue. Expected: " + a.get(i) + " Actual: " + ArrayQueueADT.element(queue);
            i++;
        }
        a.clear();
    }

    public static void main(String[] args) {
        //Note: почитать про JUnit, шоб красиво было)
        System.out.println("Testing Module");
        System.out.println();
        System.out.println("Testing enqueue/dequeue");
        fill();
        dump();
        System.out.println("----------------------------------------");
        System.out.println("Testing clear/isEmpty");
        fill();
        ArrayQueueModule.clear();
        assert ArrayQueueModule.isEmpty() : "Error in clear/isEmpty. Expected: true Actual: false";
        System.out.println("----------------------------------------");
        System.out.println("Testing dequeue");
        dequeTest();
        System.out.println("Testing toArray");
        toArrayTest(false);
        System.out.println("Testing set/get");
        setGetTest(false);
        System.out.println("Testing contains");
        containsTest(false);
        System.out.println("Testing removeFirstOccurrence");
        removeFirstOccurrenceTest(false);
        System.out.println("Testing Linked");
        System.out.println("Testing toArray");
        toArrayTest(true);
        System.out.println("Testing set/get");
        setGetTest(true);
        System.out.println("Testing contains");
        containsTest(true);
        System.out.println("Testing removeFirstOccurrence");
        removeFirstOccurrenceTest(true);
    }
}
