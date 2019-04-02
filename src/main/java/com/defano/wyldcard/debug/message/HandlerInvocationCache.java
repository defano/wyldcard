package com.defano.wyldcard.debug.message;

import com.defano.wyldcard.aspect.RunOnDispatch;

import javax.swing.*;
import java.util.*;

public class HandlerInvocationCache {

    private final static HandlerInvocationCache instance = new HandlerInvocationCache();

    private final List<HandlerInvocationObserver> handlerInvocationObservers = new ArrayList<>();
    private final Map<String, List<HandlerInvocation>> invocationMap = new HashMap<>();

    private HandlerInvocationCache() {}

    public static HandlerInvocationCache getInstance() {
        return instance;
    }

    public void notifyMessageHandled(HandlerInvocation invocation) {
        SwingUtilities.invokeLater(() -> {
            addInvocation(invocation);
            for (HandlerInvocationObserver observer : handlerInvocationObservers) {
                observer.onHandlerInvoked(invocation);
            }
        });
    }

    @RunOnDispatch
    public void addObserver(HandlerInvocationObserver observer) {
        handlerInvocationObservers.add(observer);
    }

    @RunOnDispatch
    public void removeObserver(HandlerInvocationObserver observer) {
        handlerInvocationObservers.remove(observer);

        if (handlerInvocationObservers.isEmpty()) {
            invocationMap.clear();
        }
    }

    @RunOnDispatch
    public List<HandlerInvocation> getInvocationHistory(String forThread) {
        List<HandlerInvocation> invocations = invocationMap.get(forThread);
        return invocations == null ? new ArrayList<>() : invocations;
    }

    @RunOnDispatch
    public List<HandlerInvocation> getInvocationHistory() {
        List<HandlerInvocation> invocations = new ArrayList<>();
        for (Collection<HandlerInvocation> thisCollection : invocationMap.values()) {
            invocations.addAll(thisCollection);
        }

        Collections.sort(invocations);
        return invocations;
    }

    @RunOnDispatch
    private void addInvocation(HandlerInvocation invocation) {

        // Ignore invocations when nobody is observing
        if (!handlerInvocationObservers.isEmpty()) {
            List<HandlerInvocation> invocations = invocationMap.get(invocation.getThread());

            if (invocations == null) {
                invocations = new ArrayList<>();
            }

            invocations.add(invocation);

            invocationMap.put(invocation.getThread(), invocations);
        }
    }

}
