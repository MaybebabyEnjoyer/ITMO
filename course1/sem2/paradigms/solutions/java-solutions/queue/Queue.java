package queue;

public interface Queue {
    //Pred: element != null
    //Post: n' = n + 1
    // a[n]' = element && forall i in [0, n): a[i]' == a[i]
    void enqueue(Object element);

    //Pred: n > 0
    //Post: n' = n - 1
    // forall i in [0, n): a[i]' == a[i+1]
    //R = a[0]
    Object dequeue();


    //Pred: n > 0
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    //R = a[0]
    Object element();

    //Pred: true
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    //R = n
    int size();

    //Pred: true
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    //R = (n == 0)
    boolean isEmpty();

    //Pred: true
    //Post: n = 0
    void clear();

    //Pred: true
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    //R = (element in a)
    boolean contains(Object element);

    //Pred: true
    //Post: | n' = n - 1 && forall i in [0, n): a[i]' == a[i] && a[i] != element until first occurrence
    //      | forall i in [0, n): a[i]' == a[i]) && n' == n
    // R == contains
    boolean removeFirstOccurrence(Object element);

    //Pred: n1 > 0
    //Post: n' = n - (n // n1) && forall i in [0, n'): a[i+1]' == a[i+1] if i from [0, n): (i+1) % n1 != 0
    void dropNth(int n1);

    //Pred: n1 > 0
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    // R = Queue: (i -> i' : a[i'] = a[i] if i % n1 == 0)
    Queue getNth(int n1);

    //Pred: n > 0
    //Post dropNth(n1) && getNth(n1)
    Queue removeNth(int n1);

    //Pred: true
    //Post: (forall i in [0, n): tmp[i] = a[i] && a[i]' == a[i]) && n' == n
    //R = tmp, where tmp is array representation of deck a
    Object[] toArray();

    //Pred: true
    //Post: R = strTmp, where strTmp is String representation of Array tmp (refers to toArray() method)
    // && (forall i in [0, n): a[i]' == a[i]) && n' == n
    String toStr();


    //Pred: index < n && index >= 0
    //Post: forall i in [0, n): a[i]' == a[i] && n' == n
    //R = a[index]
    void set(int index, Object element);

    //Pred: index < n && index >= 0 && element != null
    //Post: a[index] = element && forall i in [0, n): i != index => a[i]' == a[i] && n' == n
    Object get(int index);

    //Pred: n > 0
    //Post: n' = n - 1
    // forall i in [0, n): a[i]' == a[i-1]
    void pop();

    //Pred: element != null
    //Post: (forall i in [0, n): a[i]' == a[i]) && n' == n
    //R = number of i in [0, n) : a[i] == element
    int count(Object element);
}
