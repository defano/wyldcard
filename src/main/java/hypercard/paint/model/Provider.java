package hypercard.paint.model;

import java.util.Observable;

public class Provider<T> extends Observable {

    private T value;

    public Provider(Provider derivedFrom, ProviderTransform<T> transform) {
        derivedFrom.addObserver((oldValue, newValue) -> set(transform.transform(newValue)));
        set(transform.transform(derivedFrom.get()));
    }

    public Provider(T initialValue) {
        this.value = initialValue;
    }

    public static <T> Provider<T> copyOf(Provider<T> provider) {
        return new Provider<>(provider, value -> (T) value);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;

        setChanged();
        notifyObservers(this.value);
    }
}
