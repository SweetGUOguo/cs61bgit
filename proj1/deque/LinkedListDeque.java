package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private StuffNode sentinel;
    private int size = 0;

    /**
     * Creates an empty linked list deque.
     */
    public LinkedListDeque() {
        sentinel = new StuffNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    @Override
    /**
     * Adds x to the front of the list.
     */
    public void addFirst(T i) {
        sentinel.next.prev = new StuffNode(i, sentinel, sentinel.next);
        sentinel.next = sentinel.next.prev;
        size = size + 1;
    }

    @Override
    /**
     * Adds x to the end of the list.
     */
    public void addLast(T i) {
        sentinel.prev.next = new StuffNode(i, sentinel.prev, sentinel);
        sentinel.prev = sentinel.prev.next;
        size = size + 1;
    }
    @Override
    /**
     * Returns the number of items in the deque.
     */
    public int size() {
        return size;
    }

    @Override
    /**
     * Prints the items in the deque from first to last.
     */
    public void printDeque() {
        if (!isEmpty()) {
            StuffNode nowprint = sentinel.next;
            for (int i = 0; i < size; i++) {
                System.out.print(nowprint.item);
                nowprint = nowprint.next;
                System.out.print(' ');
            }
            System.out.print('\n');
        }
    }

    @Override
    /**
     * Removes the first item from the front of the list.
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            T nowitem = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            size = size - 1;
            return nowitem;
        }
    }

    @Override
    /**
     * Removes the first item from the end of the list.
     */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            T nowitem = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size = size - 1;
            return nowitem;
        }
    }

    @Override
    /**
     * Gets the item at the given index.
     */
    public T get(int index) {
        if (index >= size) {
            return null;
        } else {
            int i = 0;
            StuffNode nowget = sentinel;
            while (i <= index) {
                nowget = nowget.next;
                i++;
            }
            return nowget.item;
        }
    }

    /**
     * Same as get, but uses recursion.
     */
    public T getRecursive(int index) {
        StuffNode nowget = sentinel.next;
        return get(nowget, index);
    }

    private T get(StuffNode nowget, int index) {
        if (index == 0) {
            return nowget.item;
        } else {
            return get(nowget.next, index - 1);
        }
    }

    /**
     * equals
     */
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof Deque) {
            Deque<T> o = (Deque<T>) other;
            if (o.size() != this.size()) {
                return false;
            }
            for (int i = 0; i < this.size(); i++) {
                if (!o.get(i).equals(this.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class StuffNode {
        private StuffNode prev;
        private T item;
        private StuffNode next;

        private StuffNode(T i, StuffNode p, StuffNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private StuffNode p;

        LinkedListDequeIterator() {
            p = sentinel.next;
        }

        public boolean hasNext() {
            return p != sentinel;
        }

        public T next() {
            T item = p.item;
            p = p.next;
            return item;
        }
    }
}
