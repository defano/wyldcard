package hypercard.paint.model;

import java.util.Observable;
import java.util.Observer;

public class ImmutableProvider<T> extends Observable implements Observer {

    protected T value;

    private ImmutableProvider source;
    private ProviderTransform transform;

    public ImmutableProvider() {
        source = null;
        transform = null;
        value = null;
    }

    private <S> ImmutableProvider(ImmutableProvider<S> derivedFrom, ProviderTransform<S, T> transform) {
        this.transform = transform;

        setSource(derivedFrom);
        update(derivedFrom, derivedFrom.get());
    }

    public static <S, T> ImmutableProvider<T> derivedFrom(ImmutableProvider<S> derivedFrom, ProviderTransform<S, T> transform) {
        return new ImmutableProvider<>(derivedFrom, transform);
    }

    public static <T> ImmutableProvider<T> from(Provider<T> derivedFrom) {
        return derivedFrom(derivedFrom, null);
    }

    public T get() {
        return value;
    }

    public void setSource(ImmutableProvider source) {
        if (source != null) {
            source.deleteObserver(this);
        }

        this.source = source;
        this.source.addObserver(this);
        update(this, source.get());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (transform != null) {
            value = (T) transform.transform(arg);
        } else {
            value = (T) arg;
        }

        setChanged();
        notifyObservers(value);
    }

}
