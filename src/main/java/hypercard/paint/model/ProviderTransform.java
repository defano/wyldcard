package hypercard.paint.model;

public interface ProviderTransform<S, T> {
    T transform(S value);
}
