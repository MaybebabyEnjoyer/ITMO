package queue;

public class LinkedQueue extends AbstractQueue {
    private Node h;
    private Node t;

    @Override
    public void enqueue(Object element) {
        if (h == null) {
            h = new Node(element);
            t = h;
        }
        else {
            Node h1 = new Node(element);
            h.setNext(h1);
            h = h1;
        }
        size++;
    }


    @Override
    public Object dequeue() {
        Object value = t.getValue();
        if (h == t) {
            h = null;
        }
        t = t.getNext();
        size--;
        return value;
    }

    @Override
    public Object element() {
        return t.getValue();
    }

    @Override
    public void clear() {
        h = null;
        t = null;
        size = 0;
    }


    @Override
    public void set(int index, Object element) {
        Node h1 = t;
        for (int i = 0; i < index; i++) {
            h1 = h1.getNext();
        }
        h1.setValue(element);
    }

    @Override
    public Object get(int index) {
        Node h1 = t;
        for (int i = 0; i < index; i++) {
            h1 = h1.getNext();
        }
        return h1.getValue();
    }

    @Override
    public void pop() {
        Node h1 = t;
        for (int i = 0; i < size - 2; i++) {
            h1 = h1.getNext();
        }
        h1.setNext(null);
        h = h1;
        size--;
    }

    @Override
    public LinkedQueue getNth(int n) {
        LinkedQueue queue = new LinkedQueue();
        Node h1 = t;
        int initialSize = size;
        for (int i = 0; i < initialSize; i++) {
            if ((i+1) % n == 0) {
                queue.enqueue(h1.getValue());
            }
            h1 = h1.getNext();
        }
        return queue;
    }
}
