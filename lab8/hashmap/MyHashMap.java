package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author gguo
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Removes all of the mappings from this map.
     */
    private int size = 0;
    private Node n;
    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int initial = 16;
    private double max = 0.75;
    private int bucketsnum = initial;

    /**
     * Constructors
     */
    public MyHashMap() {
        buckets = createTable(initial);
    }

    public MyHashMap(int initialSize) {
        initial = initialSize;
        buckets = createTable(initial);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        initial = initialSize;
        max = maxLoad;
        buckets = createTable(initial);
    }

    @Override
    public void clear() {
        size = 0;
        buckets = null;
    }

    @Override
    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        if (buckets == null) {
            return false;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[index];
        if (bucket == null) {
            return false;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        if (buckets == null) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[index];
        if (bucket == null) {
            return null;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    @Override
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[index];
        if (bucket != null) {
            for (Node node : bucket) {
                if (node.key.equals(key)) {
                    node.value = value;
                    return;
                }
            }
        } else {
            bucket = createBucket();
            buckets[index] = bucket;
        }
        Node newNode = createNode(key, value);
        bucket.add(newNode);
        size++;
    }

    @Override
    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            if (bucket != null) {
                for (Node node : bucket) {
                    keySet.add(node.key);
                }
            }
        }
        return keySet;
    }

    @Override
    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[index];
        if (bucket != null) {
            for (Node node : bucket) {
                if (node.key.equals(key)) {
                    V tmpv = node.value;
                    bucket.remove(node);
                    return tmpv;
                }
            }
        }
        return null;
//        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[index];
        if (bucket != null) {
            for (Node node : bucket) {
                if (node.key.equals(key) && node.value.equals(value)) {
                    V tmpv = node.value;
                    bucket.remove(node);
                    return tmpv;
                }
            }
        }
        return null;
//        throw new UnsupportedOperationException();
    }

    // You should probably define some more!

    @Override
    public Iterator<K> iterator() {
        return new HashMapIter();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        Node node = new Node(key, value);
        return node;
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {

        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        buckets = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            buckets[i] = createBucket();
        }
        return buckets;
    }

    private class HashMapIter implements Iterator<K> {
        int i = 0;
        private Node cur;

        public HashMapIter() {
            cur = n;
        }

        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public K next() {
            K ret = cur.key;
            cur = buckets[i].iterator().next();
            i++;
            if (i >= bucketsnum) {
                i = 0;
            }
            return ret;
        }
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

}
