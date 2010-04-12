package utils;

public class Reference<V> {

    private V value;

    public void set(V o) {
        value = o;
    }

    public V get() {
        return value;
    }
}
