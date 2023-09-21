package bstmap;

import org.hamcrest.Factory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
//import java.util.Comparable;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private int size = 0;

    public void printInOrder() {
        if (map != null) {
            printInOrderHelper(map);
        }
    }

    private void printInOrderHelper(Entry node) {
        if (node != null) {
            printInOrderHelper(node.leftn);
            System.out.println(node.key);
            printInOrderHelper(node.rightn);
        }
    }
    @Override
    public void clear() {
        map = null;
        size = 0;
    }

    @Override
    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        if(map==null){
            return false;
        }
        Entry lookup = map.get(key);
        if (lookup == null) {
            return false;
        } else {
            return true;
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (map == null) {
            return null;
        }
        Entry lookup = map.get(key);
        if (lookup == null) {
            return null;
        }
        return lookup.val;
    }

    @Override
    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    @Override
    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (map != null) {
            Entry lookup = map.getpos(key);
            if (key.compareTo(lookup.key) == 0) {
                lookup.val = value;
            } else if (key.compareTo(lookup.key) > 0) {
                lookup.rightn = new Entry(key, value, null, null);
                size = size + 1;
            } else if (key.compareTo(lookup.key) < 0) {
                lookup.leftn = new Entry(key, value, null, null);
                size = size + 1;
            }
        } else {
            map = new Entry(key, value, null, null);
            size = size + 1;
        }
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
//        return new BSTMapIter();
    }

    private Entry map;

    private class Entry {
        K key;
        V val;
        Entry leftn;
        Entry rightn;

        Entry(K k, V v, Entry left, Entry right) {
            key = k;
            val = v;
            leftn = left;
            rightn = right;
        }

        Entry getpos(K k) {
            if (k != null && k.equals(key)) {
                return this;
            }
            if (k.compareTo(key) > 0) {
                if (rightn == null) {
                    return this;
                }
                return rightn.getpos(k);
            } else if (k.compareTo(key) < 0) {
                if (leftn == null) {
                    return this;
                }
                return leftn.getpos(k);
            }
            return null;
        }

        Entry get(K k) {
            if (k != null && k.equals(key)) {
                return this;
            }
            if (k == null) {
                return null;
            }
//            if (leftn == null && rightn == null) {
//                return null;
//            }
            if (k.compareTo(key) > 0) {
                return rightn.get(k);
            } else if (k.compareTo(key) < 0) {
                return leftn.get(k);
            }
            return null;
        }

    }

    private class BSTMapIter implements Iterator<K> {
        private Entry cur;

        public BSTMapIter() {
            cur = map;
        }

        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public K next() {
            K ret = cur.key;
            cur = cur.leftn;
            return ret;
        }
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

}
