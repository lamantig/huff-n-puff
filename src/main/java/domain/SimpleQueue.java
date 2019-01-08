package domain;

public interface SimpleQueue<E> {

    /**
     * Returns true if the queue is empty (there are no elements in it), false
     * if it has at least one element.
     *
     * @return True if the queue is empty, false if it has at least one element.
     */
    boolean isEmpty();

    /**
     * Tries to put the given element at the tail of this queue, returning true
     * if successful, false otherwise.
     *
     * @param e The element to insert at this queue's tail.
     * @return Success of insertion operation.
     */
    boolean offer(E e);

    /**
     * Returns the element at the head of this queue, without removing it.
     *
     * @return The element at the head of this queue, or null if it's empty.
     */
    E peek();

    /**
     * Returns the element at the head of this queue, removing it.
     *
     * @return The element at the head of this queue, or null if it's empty.
     */
    E poll();

    /**
     * Returns the number of elements in this queue.
     *
     * @return Number of elements in this queue.
     */
    int size();
}
