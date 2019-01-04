package domain;

import java.util.Queue;

public class JavasQueue<E> implements SimpleQueue<E> {

    private final Queue<E> q;

    public JavasQueue(Queue<E> q) {
        this.q = q;
    }

    @Override
    public boolean isEmpty() {
        return q.isEmpty();
    }

    @Override
    public boolean offer(E e) {
        return q.offer(e);
    }

    @Override
    public E peek() {
        return q.peek();
    }

    @Override
    public E poll() {
        return q.poll();
    }

    @Override
    public int size() {
        return q.size();
    }
}
