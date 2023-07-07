package queue;

// a[0]..a[n-1]
//Invariant: n >= 0, forall i in [0, n): a[i] != null

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueueADT {
    private int t;
    private int size;
    private Object[] ele = new Object[2];

    //Pred: element != null && queue != null
    //Post: n' = n + 1
    // a[n]' = element && forall i in [0, n): a[i]' == a[i]
    public static void enqueue(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(queue);
        queue.ele[(queue.t + queue.size) % queue.ele.length] = element;
        queue.size++;
        if (queue.size == queue.ele.length)
            ensureCapacity(queue);
    }

    private static void ensureCapacity(ArrayQueueADT queue) {
        Object[] temp = new Object[(queue.size + 1) * 2];
        if (queue.t + queue.size < queue.ele.length) {
            System.arraycopy(queue.ele, queue.t, temp, 0, queue.size);
        } else {
            System.arraycopy(queue.ele, queue.t, temp, 0, queue.ele.length - queue.t);
            System.arraycopy(queue.ele, 0, temp, queue.ele.length - queue.t, (queue.t + queue.size) % queue.ele.length);
        }
        queue.t = 0;
        queue.ele = temp;
    }

    //Pred: n > 0 && queue != null
    //Post: n' = n - 1
    // forall i in [0, n): a[i]' == a[i+1]
    //R: a[0]
    public static Object dequeue(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        Object temp = queue.ele[queue.t];
        queue.ele[queue.t] = null;
        queue.t = (queue.t + 1) % queue.ele.length;
        queue.size--;
        if (queue.size < queue.ele.length / 4)
            ensureCapacity(queue);
        return temp;
    }

    //Pred: queue != null
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    //R = n
    public static int size(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        return queue.size;
    }

    //Pred: queue != null
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    // R = (n == 0)
    public static boolean isEmpty(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        return queue.size == 0;
    }

    //Pred: element != null && queue != null
    //Post: n' = n + 1
    //forall i in (0, n]: a[i]' == a[i - 1]
    //a[0]' = element
    public static void push(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(queue);
        queue.size++;
        queue.t = (queue.t == 0) ? queue.ele.length - 1 : queue.t - 1;
        queue.ele[queue.t] = element;
        if (queue.size == queue.ele.length)
            ensureCapacity(queue);
    }

    //Pred: index < n && index >= 0 && element != null && queue != null
    //Post: a[index]' = element && forall i in [0, n): i != index => a[i]' == a[i] && n' == n
    public static void set(ArrayQueueADT queue, int index, Object element) {
        Objects.requireNonNull(queue);
        queue.ele[(queue.t + index) % queue.ele.length] = element;
    }

    //Pred: index < n && index >= 0 && queue != null
    //Post: R = a[index] && forall i in [0, n): a[i]' == a[i] && n' == n
    public static Object get(ArrayQueueADT queue, int index) {
        Objects.requireNonNull(queue);
        return queue.ele[(queue.t + index) % queue.ele.length];
    }

    //Pred: n > 0 && queue != null
    //Post: n' = n - 1 && (forall i in [0, n): a[i]' == a[i+1]) && R = a[n - 1]
    public static Object remove(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        queue.size--;
        Object temp = queue.ele[(queue.t + queue.size) % queue.ele.length];
        queue.ele[(queue.t + queue.size) % queue.ele.length] = null;
        if (queue.size < queue.ele.length / 4)
            ensureCapacity(queue);
        return temp;
    }

    //Pred: queue != null
    //Post: n = 0
    public static void clear(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        queue.size = 0;
        queue.t = 0;
        queue.ele = new Object[2];
    }

    //Pred: n > 0 && queue != null
    //Post: R = a[n - 1] && (forall i in [0, n): a[i]' == a[i]) && n' == n
    public static Object peek(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        return queue.ele[(queue.t + queue.size - 1) % queue.ele.length];
    }

    //Pred: n > 0 && queue != null
    //Post: R = a[0] && (forall i in [0, n): a[i]' == a[i]) && n' == n
    public static Object element(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        return queue.ele[queue.t];
    }

    //Pred: queue != null
    //Post: R = tmp, where tmp is array representation of deck a, (forall i in [0, n): tmp[i] = a[i] && a[i]' == a[i]) && n' == n
    public static Object[] toArray(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        Object[] temp = new Object[queue.size];
        if (queue.t + queue.size < queue.ele.length) {
            System.arraycopy(queue.ele, queue.t, temp, 0, queue.size);
        } else {
            System.arraycopy(queue.ele, queue.t, temp, 0, queue.ele.length - queue.t);
            System.arraycopy(queue.ele, 0, temp, queue.ele.length - queue.t, (queue.t + queue.size) % queue.ele.length);
        }
        return temp;
    }
    ////Pred: queue != null
    //Post: R = strTmp, where strTmp is String representation of Array tmp (refers to toArray() method)
    // && (forall i in [0, n): a[i]' == a[i]) && n' == n
    public static String toStr(ArrayQueueADT queue) {
        return Arrays.toString(toArray(queue));
    }
}
