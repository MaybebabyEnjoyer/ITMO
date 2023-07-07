package queue;

public class ArrayQueue extends AbstractQueue {
    private int t;
    private Object[] ele = new Object[2];

    @Override
    public void enqueue(Object element) {
        ele[(t + size) % ele.length] = element;
        size++;
        if (size == ele.length)
            ensureCapacity();
    }

    private void ensureCapacity() {
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

    @Override
    public Object dequeue() {
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
    //    public int size() {
    //        return size;
    //    }

    //Pred: true
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    // R = (n == 0)
    //    public boolean isEmpty() {
    //        return size == 0;
    //    }

//    //Pred: element != null
//    //Post: n' = n + 1
//    //forall i in (0, n]: a[i]' == a[i - 1]
//    //a[0]' = element
//    public void push(Object element) {
//        size++;
//        t = (t == 0) ? ele.length - 1 : t - 1;
//        ele[t] = element;
//        if (size == ele.length)
//            ensureCapacity();
//    }


    @Override
    public void set(int index, Object element) {
        ele[(t + index) % ele.length] = element;
    }

    @Override
    public Object get(int index) {
        return ele[(t + index) % ele.length];
    }

    @Override
    public void clear() {
        ele = new Object[2];
        size = 0;
        t = 0;
    }

//    //Pred: n > 0
//    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
//    //R = a[n - 1]
//    public Object peek() {
//        return ele[(t + size - 1) % ele.length];
//    }

    @Override
    public Object element() {
        return ele[t];
    }


    @Override
    public Object[] toArray() {
        Object[] temp = new Object[size];
        if (t + size < ele.length) {
            System.arraycopy(ele, t, temp, 0, size);
        } else {
            System.arraycopy(ele, t, temp, 0, ele.length - t);
            System.arraycopy(ele, 0, temp, ele.length - t, (t + size) % ele.length);
        }
        return temp;
    }


    @Override
    public void pop() {
        ele[(t + size - 1) % ele.length] = null;
        size--;
        if (size < ele.length / 4)
            ensureCapacity();
    }

    @Override
    public ArrayQueue getNth(int n) {
        ArrayQueue queue = new ArrayQueue();
        for (int i = n - 1; i < size; i += n) {
            queue.enqueue(get(i));
        }
        return queue;
    }

}
