package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int nextfirst=0;
    private int nextlast=0;
    private T[] items;
    private int size=0;

    public ArrayDeque() {
        items = (T[]) new Object[8];
    }

    /**
     * Resizes the underlying array to the target capacity.
     */
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        if ((nextfirst + 1) % items.length <= (nextlast - 1 + items.length) % items.length) {
            int i = (nextfirst + 1) % items.length;

//            System.arraycopy(items, (nextfirst + 1) % items.length, a, 0, size);
            System.arraycopy(items, i, a, 0, size);
        } else {
            System.arraycopy(items, (nextfirst + 1) % items.length, a, 0, items.length - nextfirst - 1);
            System.arraycopy(items, 0, a, items.length - nextfirst - 1, nextfirst + 1);
        }
        items = a;
        nextfirst = a.length - 1;
        nextlast = size;
    }

    @Override
    /**
     * Adds x to the front of the list.
     */
    public void addFirst(T i) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextfirst] = i;
        size = size + 1;

        if (size == 1 && nextfirst == nextlast) {
            nextlast = (nextlast + 1) % items.length;
        }
        nextfirst = (nextfirst - 1 + items.length) % items.length;
    }

    @Override
    /**
     * Adds x to the end of the list.
     */
    public void addLast(T i) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextlast] = i;
        size = size + 1;
        if (size == 1 && nextfirst == nextlast) {
            nextfirst = (nextfirst - 1 + items.length) % items.length;
        }
        nextlast = (nextlast + 1) % items.length;
    }

    @Override
    /**
     * Prints the items in the deque from first to last.
     */
    public void printDeque() {
        if (!isEmpty()) {
            for (int i = (nextfirst + 1) % items.length; i != nextlast; i = (i + 1) % items.length) {
                System.out.print(items[i] + " ");
            }
            System.out.print('\n');
        }
    }

    @Override
    /**
     * Removes x from the front of the list.
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            if ((size < items.length / 4) && (size > 4)) {
                resize(items.length / 4);
            }
            T delete = items[(nextfirst + 1) % items.length];
            items[(nextfirst + 1) % items.length] = null;
            nextfirst = (nextfirst + 1) % items.length;
            size = size - 1;
            if (size == 0) {
                nextfirst = 0;
                nextlast = 0;
            }
            return delete;
        }
    }

    @Override
    /**
     * Removes x from the end of the list.
     */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            if ((size < items.length / 4) && (size > 4)) {
                resize(items.length / 4);
            }
            T delete = items[(nextlast - 1 + items.length) % items.length];
            items[(nextlast - 1 + items.length) % items.length] = null;
            nextlast = (nextlast - 1 + items.length) % items.length;
            size = size - 1;
            if (size == 0) {
                nextfirst = 0;
                nextlast = 0;
            }
            return delete;
        }
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
     * Gets the item at the given index.
     */
    public T get(int index) {
        return items[(nextfirst + 1 + index) % items.length];
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
            } else {
                for (int i = 0; i < this.size(); i++) {
                    if (o.get(i) != this.get(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;

        ArrayDequeIterator() {
            index = 0;
        }

        public boolean hasNext() {
            return index < size;
        }

        public T next() {
            T item = get(index);
            index += 1;
            return item;
        }
    }
}
