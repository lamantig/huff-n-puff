package domain;

/**
 * SimpleQueue implementation for HuffNode elements that internally uses an
 * array to store them.
 */
public class ArrayQueue implements SimpleQueue<HuffNode> {

    private HuffNode[] elements;
    private int head;
    private int tail;
    private int size;

    private static final int INITIAL_ARRAY_SIZE = (int) Math.pow(2, 9);
    private static final int EXPANDING_FACTOR = 2;

    /**
     * Returns an instance of ArrayQueue that uses a newly created array to
     * store the elements.
     */
    public ArrayQueue() {
        elements = new HuffNode[INITIAL_ARRAY_SIZE];
        head = 0;
        tail = 0;
        size = 0;
    }

    /**
     * Returns an instance of ArrayQueue prefilled with the elements of the
     * given array. It doesn't make a copy of the array, which can thus be
     * modified by calls to this ArrayQueue's methods.
     *
     * @param elements An array containing the elements that will be used to
     * fill the newly created ArrayQueue. It isn't copied, instead it's used
     * directly.
     */
    public ArrayQueue(HuffNode[] elements) {
        this.elements = elements;
        head = 0;
        tail = 0;
        size = elements.length;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean offer(HuffNode e) {
        if (isFull()) {
            expand();
        }
        elements[tail++] = e;
        tail %= elements.length;
        size++;
        return true;
    }

    @Override
    public HuffNode peek() {
        if (isEmpty()) {
            return null;
        }
        return elements[head];
    }

    @Override
    public HuffNode poll() {
        if (isEmpty()) {
            return null;
        }
        HuffNode e = elements[head++];
        head %= elements.length;
        size--;
        return e;
    }

    @Override
    public int size() {
        return size;
    }

    private boolean isFull() {
        return size == elements.length;
    }

    private void expand() {
        HuffNode[] newValues = new HuffNode[elements.length * EXPANDING_FACTOR];
        for (int i = 0; i < elements.length; i++) {
            newValues[i] = elements[head++];
            head %= elements.length;
        }
        head = 0;
        tail = elements.length;
        elements = newValues;
    }
}
