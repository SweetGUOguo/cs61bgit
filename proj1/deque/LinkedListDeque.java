package deque;

public class LinkedListDeque<Item> {
    private StuffNode sentinel;
    private int size;

    /**
     * Creates an empty linked list deque.
     */
    public LinkedListDeque() {
        sentinel = new StuffNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
//        sentinel = new StuffNode(null, sentinel, sentinel);
        size = 0;
    }

    public LinkedListDeque(Item i) {
        sentinel = new StuffNode(null, null, null);
        sentinel.next = new StuffNode(i, sentinel, sentinel);
        size = 1;
    }

    /**
     * Adds x to the front of the list.
     */
    public void addFirst(Item i) {
        sentinel.next.prev = new StuffNode(i, sentinel, sentinel.next);
        sentinel.next = sentinel.next.prev;
        size = size + 1;
    }

    /**
     * Adds x to the end of the list.
     */
    public void addLast(Item i) {
        sentinel.prev.next = new StuffNode(i, sentinel.prev, sentinel);
        sentinel.prev = sentinel.prev.next;
        size = size + 1;
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
     * Returns the number of items in the deque.
     */
    public int size() {
        return size;
    }

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

    /**
     * Removes the first item from the front of the list.
     */
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            Item nowitem = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            size = size - 1;
            return nowitem;
        }
    }

    /**
     * Removes the first item from the end of the list.
     */
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            Item nowitem = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size = size - 1;
            return nowitem;
        }
    }

    /**
     * Gets the item at the given index.
     */
    public Item get(int index) {
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
    public Item getRecursive(int index) {
        StuffNode nowget = sentinel.next;
        return get(nowget, index);
    }

    private Item get(StuffNode nowget, int index) {
        if (index == 0) {
            return nowget.item;
        } else {
            return get(nowget.next, index - 1);
        }
    }


    private class StuffNode {
        public StuffNode prev;
        public Item item;
        public StuffNode next;

        public StuffNode(Item i, StuffNode p, StuffNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }
}
