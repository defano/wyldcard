package hypercard.paint.model;

import java.util.ArrayList;

public class Provider<T> {

    private T value;

    private final ArrayList<ProvidedValueObserver> observers = new ArrayList<>();

    public Provider(Provider derivedFrom, ProviderTransform<T> transform) {
        derivedFrom.addObserver((oldValue, newValue) -> set(transform.transform(newValue)));
        set(transform.transform(derivedFrom.get()));
    }

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

    public void addObserver(ProvidedValueObserver observer) {
        observers.add(observer);
        observer.onChanged(null, get());
    }

    public boolean removeObserver(ProvidedValueObserver observer) {
        return observers.remove(observer);
    }

    private void fireOnChanged(T oldValue, T newValue) {
        for (ProvidedValueObserver thisObserver : observers) {
            thisObserver.onChanged(oldValue, newValue);
        }
    }
}
