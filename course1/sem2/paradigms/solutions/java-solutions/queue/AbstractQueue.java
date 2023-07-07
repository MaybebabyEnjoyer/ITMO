package queue;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractQueue implements Queue {
    protected int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public abstract Object dequeue();

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        for (int i = 0; i < size; i++) {
            array[i] = dequeue();
            enqueue(array[i]);
        }
        return array;
    }

    @Override
    public String toStr() {
        return Arrays.toString(toArray());
    }

    @Override
    public boolean contains(Object element) {
        Objects.requireNonNull(element);
        return Arrays.asList(toArray()).contains(element);
    }

    @Override
    public boolean removeFirstOccurrence(Object element) {
        Objects.requireNonNull(element);
        int index = Arrays.asList(toArray()).indexOf(element);
        if (index == -1) {
            return false;
        }
        remove(index);
        return true;
    }

    protected void remove(int index) {
        if (index == 0) {
            dequeue();
            return;
        }
        if (index == size - 1) {
            pop();
            return;
        }
        if (index < size / 2) {
            for (int i = index; i > 0; i--) {
                set(i, get(i - 1));
            }
            dequeue();
        } else {
            for (int i = index; i < size - 1; i++) {
                set(i, get(i + 1));
            }
            pop();
        }
    }

    @Override
    public abstract void set(int index, Object element);

    @Override
    public abstract Object get(int index);

    @Override
    public abstract void pop();

    @Override
    public void dropNth(int n) {
        if (n <= 1) {
            clear();
        }
        final int initialSize = size;
        if (n > initialSize) {
            return;
        }
        for (int i = 1; i <= initialSize; i++) {
            if (i % n != 0) {
                enqueue(dequeue());
            }
            else {
                dequeue();
            }
        }
    }

    @Override
    public abstract Queue getNth(int n);

    @Override
    public Queue removeNth(int n) {
        Queue queue = getNth(n);
        dropNth(n);
        return queue;
    }

    @Override
    public int count(Object element) {
        Objects.requireNonNull(element);
        return (int) Arrays.stream(toArray()).filter(e -> e.equals(element)).count();
    }
}
