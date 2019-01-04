package domain;

public interface SimpleQueue<E> {
    /**
     *
     * @return
     */
    boolean isEmpty();
    /**
     *
     * @param e
     * @return
     */
    boolean offer(E e);
    /**
     *
     * @return
     */
    E peek();
    /**
     *
     * @return
     */
    E poll();
    /**
     * 
     * @return
     */
    int size();
}
