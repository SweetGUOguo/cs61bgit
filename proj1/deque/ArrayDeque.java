package deque;

public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int nextfirst;
    private int nextlast;

    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        nextfirst = 0;
        nextlast = 0;
    }

    /**
     * Judge if the deque is empty.
     */
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Resizes the underlying array to the target capacity.
     */
    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }

    /**
     * Adds x to the front of the list.
     */
    public void addFirst(Item i) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextfirst] = i;
        size = size + 1;
        nextfirst = (nextfirst - 1 + items.length) % items.length;
    }

    /**
     * Adds x to the end of the list.
     */
    public void addLast(Item i) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextlast] = i;
        size = size + 1;
        nextlast = (nextlast + 1) % items.length;
    }

    /**
     * Prints the items in the deque from first to last.
     */
    public void printDeque() {
        if (!isEmpty()) {
            for (int i = (nextfirst + 1) % items.length; i != nextlast; i = (i + 1) % items.length) {
                System.out.print(items[i]);
                System.out.print(' ');
            }
            System.out.print('\n');
        }
    }


    /**
     * Removes x from the front of the list.
     */
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            Item delete = items[(nextfirst + 1) % items.length];
            items[(nextfirst + 1) % items.length] = null;
            nextfirst = (nextfirst + 1) % items.length;
            size = size - 1;
            return delete;
        }
    }

    /**
     * Removes x from the end of the list.
     */
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            Item delete = items[(nextlast - 1) % items.length];
            items[(nextlast - 1 + items.length) % items.length] = null;
            nextlast = (nextlast - 1 + items.length) % items.length;
            size = size - 1;
            return delete;
        }
    }

    /**
     * Returns the number of items in the deque.
     */
    public int size() {
        return size;
    }

    /**
     * Gets the item at the given index.
     */
    public Item get(int index) {
        return items[index];
    }


}
