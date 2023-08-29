package deque;

import java.util.Iterator;

//public class ArrayDeque<Item> implements Iterable<Item> {
public class ArrayDeque<Item> implements Deque<Item> {
    private int nextfirst;
    private int nextlast;
    private Item[] items;
    private int size;

    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        nextfirst = 0;
        nextlast = 0;
    }
    /*@Override
    *//**
     * Judge if the deque is empty.
     *//*
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }*/

    /**
     * Resizes the underlying array to the target capacity.
     */
    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        if ((nextfirst + 1) % items.length <= (nextlast - 1 + items.length) % items.length) {
            int i = (nextfirst + 1) % items.length;

//            System.arraycopy(items, (nextfirst + 1) % items.length, a, 0, size);
            System.arraycopy(items, i, a, 0, size);
        } else {
            System.arraycopy(items, (nextfirst + 1) % items.length, a, 0, items.length - nextfirst - 1);
            System.arraycopy(items, 0, a, items.length - nextfirst - 1, nextfirst);
        }
        items = a;
        nextfirst = a.length - 1;
        nextlast = size;
    }
    @Override
    /**
     * Adds x to the front of the list.
     */
    public void addFirst(Item i) {
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
    public void addLast(Item i) {
        if (size == items.length) {
            resize(size * 2);
        }

        System.out.print("   size  " + size + "  length  " + items.length + "   ");
        System.out.print("   nextlast  " + nextlast + "  nextfirst  " + nextfirst + "   ");

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
                System.out.print(items[i]);
                System.out.print(' ');
            }
            System.out.print('\n');
        }
    }

    @Override
    /**
     * Removes x from the front of the list.
     */
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            if ((size < items.length / 4) && (size > 4)) {
                resize(items.length / 4);
            }
            Item delete = items[(nextfirst + 1) % items.length];
            items[(nextfirst + 1) % items.length] = null;
            nextfirst = (nextfirst + 1) % items.length;
            size = size - 1;
            return delete;
        }
    }
    @Override
    /**
     * Removes x from the end of the list.
     */
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            if ((size < items.length / 4) && (size > 4)) {
                resize(items.length / 4);
            }
            Item delete = items[(nextlast - 1 + items.length) % items.length];
            items[(nextlast - 1 + items.length) % items.length] = null;
            nextlast = (nextlast - 1 + items.length) % items.length;
            size = size - 1;
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
    public Item get(int index) {
        return items[index];
    }

//    public Iterator<Item> iterator(){
//        return DequeIterator();
//    }
//
//    private class DequeIterator implements Iterator<Item> {
//        // 实现迭代器的相关方法
//        // ...
//    }
//
//    public boolean equals(Object o){
//
//    }

}
