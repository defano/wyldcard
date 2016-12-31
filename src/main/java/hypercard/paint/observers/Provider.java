package hypercard.paint.observers;

import java.util.ArrayList;

public class Provider<T> {

    public interface Observer {
        void onChanged(Object oldValue, Object newValue);
    }

    private T value;
    private ArrayList<Observer> observers = new ArrayList<>();

    public Provider(T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        T oldValue = this.value;
        this.value = value;

        fireOnChanged(oldValue, this.value);
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public boolean removeObserver(Observer observer) {
        return observers.remove(observer);
    }

    private void fireOnChanged(T oldValue, T newValue) {
        for (Observer thisObserver : observers) {
            thisObserver.onChanged(oldValue, newValue);
        }
    }
}
