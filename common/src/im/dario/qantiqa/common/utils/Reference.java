package im.dario.qantiqa.common.utils;

public class Reference<V> {

    private V value;

    @SuppressWarnings("unchecked")
    public void set(Object o) {
        value = (V) o;
    }

    public synchronized V get() {
        // TODO It should be improved with proper concurrency
        while (value == null) {
            try {
                this.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return value;
    }
}
