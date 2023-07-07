package queue;

// a[0]..a[n-1]
//Invariant: n >= 0, forall i in [0, n): a[i] != null

import java.util.Arrays;

public class ArrayQueueModule {
    private static int t;
    private static int size;
    private static Object[] ele = new Object[2];

    //Pred: element != null
    //Post: n' = n + 1
    // a[n]' = element && forall i in [0, n): a[i]' == a[i]
    public static void enqueue(Object element) {
        ele[(t + size) % ele.length] = element;
        size++;
        if (size == ele.length)
            ensureCapacity();
    }

    private static void ensureCapacity() {
        Object[] temp = new Object[(size + 1) * 2];
        if (t + size < ele.length) {
            System.arraycopy(ele, t, temp, 0, size);
        } else {
            System.arraycopy(ele, t, temp, 0, ele.length - t);
            System.arraycopy(ele, 0, temp, ele.length - t, (t + size) % ele.length);
        }
        t = 0;
        ele = temp;
    }

    //Pred: n > 0
    //Post: n' = n - 1
    // forall i in [0, n'): a[i]' == a[i+1]
    //R = a[0]
    public static Object dequeue() {
        Object temp = ele[t];
        ele[t] = null;
        t = (t + 1) % ele.length;
        size--;
        if (size < ele.length / 4)
            ensureCapacity();
        return temp;
    }

    //Pred: true
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    //R = n
    public static int size() {
        return size;
    }

    //Pred: true
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    // R = (n == 0)
    public static boolean isEmpty() {
        return size == 0;
    }

    //Pred: element != null
    //Post: n' = n + 1
    //forall i in (0, n]: a[i]' == a[i - 1]
    //a[0]' = element
    public static void push(Object element) {
        size++;
        t = (t == 0) ? ele.length - 1 : t - 1;
        ele[t] = element;
        if (size == ele.length)
            ensureCapacity();
    }

    //Pred: index < n && index >= 0 && element != null
    //Post: a[index] = element && forall i in [0, n): i != index => a[i]' == a[i] && n' == n
    public static void set(int index, Object element) {
        ele[(t + index) % ele.length] = element;
    }

    //Pred: index < n && index >= 0
    //Post: forall i in [0, n): a[i]' == a[i] && n' == n
    //R = a[index]
    public static Object get(int index) {
        return ele[(t + index) % ele.length];
    }

    //Pred: n > 0
    //Post: (forall i in [0, n'): a[i]' = a[i]) && n' = n - 1
    //R = a[n - 1]
    public static Object remove() {
        Object temp = ele[(t + size - 1) % ele.length];
        ele[(t + size - 1) % ele.length] = null;
        size--;
        if (size < ele.length / 4)
            ensureCapacity();
        return temp;
    }

    //Pred: true
    //Post: n == 0
    public static void clear() {
        ele = new Object[2];
        size = 0;
        t = 0;
    }

    //Pred: n > 0
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    //R = a[n - 1]
    public static Object peek() {
        return ele[(t + size - 1) % ele.length];
    }

    //Pred: n > 0
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    //R = a[0]
    public static Object element() {
        return ele[t];
    }

    //Pred: true
    //Post: (forall i in [0, n): tmp[i] = a[i] && a[i]' == a[i]) && n' == n
    //R = tmp, where tmp is array representation of deck a
    public static Object[] toArray() {
        Object[] temp = new Object[size];
        if (t + size < ele.length) {
            System.arraycopy(ele, t, temp, 0, size);
        } else {
            System.arraycopy(ele, t, temp, 0, ele.length - t);
            System.arraycopy(ele, 0, temp, ele.length - t, (t + size) % ele.length);
        }
        return temp;
    }

    //Pred: true
    //Post: R = strTmp, where strTmp is String representation of Array tmp (refers to toArray() method) &&
    // (forall i in [0, n): a[i]' == a[i]) && n' == n
    public static String toStr() {
        return Arrays.toString(toArray());
    }
}