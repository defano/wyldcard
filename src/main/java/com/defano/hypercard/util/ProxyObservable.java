package com.defano.hypercard.util;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class ProxySubject<T> {

    private BehaviorSubject<T> proxy;
    private Disposable disposable;

    public ProxySubject(Observable<T> proxied) {
        this.proxy = BehaviorSubject.createDefault(proxied.blockingFirst());
        this.disposable = proxied.subscribe(t -> proxy.onNext(t));
    }

    public void setSource(Observable<T> newProxied) {
        this.disposable.dispose();
        this.proxy.onNext(newProxied.blockingFirst());
        newProxied.subscribe(t -> proxy.onNext(t));
    }

    public Observable<T> getObservable() {
        return proxy;
    }
}
