package com.defano.wyldcard.debug.watch.message;

import javax.swing.*;
import java.util.*;

public class HandlerInvocationBridge {

    private final static HandlerInvocationBridge instance = new HandlerInvocationBridge();
    private final static int CACHE_DEPTH = 250;

    private final List<HandlerInvocationObserver> handlerInvocationObservers = new ArrayList<>();
    private final Map<String, List<HandlerInvocation>> invocationMap = new HashMap<>();

    private HandlerInvocationBridge() {}

    public static HandlerInvocationBridge getInstance() {
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

    public void addObserver(HandlerInvocationObserver observer) {
        handlerInvocationObservers.add(observer);
    }

    public List<HandlerInvocation> getInvocationHistory(String forThread) {
        List<HandlerInvocation> invocations = invocationMap.get(forThread);
        return invocations == null ? new ArrayList<>() : invocations;
    }

    public List<HandlerInvocation> getInvocationHistory() {
        List<HandlerInvocation> invocations = new ArrayList<>();
        for (Collection<HandlerInvocation> thisCollection : invocationMap.values()) {
            invocations.addAll(thisCollection);
        }

        Collections.sort(invocations);
        return invocations;
    }

    private void addInvocation(HandlerInvocation invocation) {
        List<HandlerInvocation> invocations = invocationMap.get(invocation.getThread());

        if (invocations == null) {
            invocations = new ArrayList<>();
        }

        invocations.add(invocation);

        if (invocations.size() > CACHE_DEPTH) {
            invocations.remove(0);
        }

        invocationMap.put(invocation.getThread(), invocations);
    }

}
