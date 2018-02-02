package com.defano.hypercard.util;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class ProxyObservable<T> {

    private final BehaviorSubject<T> proxy;
    private Disposable disposable;

    public ProxyObservable(Observable<T> proxied) {
        this.proxy = BehaviorSubject.createDefault(proxied.blockingFirst());
        this.disposable = proxied.subscribe(proxy::onNext);
    }

    public void setSource(Observable<T> newProxied) {
        this.disposable.dispose();
        this.disposable = newProxied.subscribe(proxy::onNext);
    }

    public Observable<T> getObservable() {
        return proxy;
    }
}
