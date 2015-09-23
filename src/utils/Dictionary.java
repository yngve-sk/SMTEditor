package utils;

import java.util.ArrayList;
import java.util.List;

import model.SMTLink;

public class Dictionary<K, V> {

    private ArrayList<K> keys;
    private ArrayList<V> values;

    public Dictionary() {
        keys = new ArrayList<K>();
        values = new ArrayList<V>();
    }

    public void put(K key, V value) {
        if(keys.contains(key)) {
            return;
        }
        keys.add(key);
        values.add(value);
    }

    public void remove(K key) {
        int index = keys.indexOf(key);
        keys.remove(index);
        values.remove(index);
    }

    public V get(K key) {
        int index = keys.indexOf(key);
        return index == -1 ? null : values.get(index);
    }

    public void clear() {
        keys.clear();
        values.clear();
    }

    public boolean containsKey(K k) {
        return keys.contains(k);
    }

    public List<V> values() {
        return values;
    }

	public boolean isEmpty() {
		return values.isEmpty();
	}

}
