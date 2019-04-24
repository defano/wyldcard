package com.defano.wyldcard.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.swing.*;

@SuppressWarnings("unused")
@Aspect
public class RunOnDispatchAspect {

    @Pointcut("execution(@com.defano.wyldcard.aspect.RunOnDispatch * *(..))")
    public void runOnDispatchMethods() {
    }

    @Before("runOnDispatchMethods()")
    public void runOnDispatch(JoinPoint joinPoint) {
        if (!SwingUtilities.isEventDispatchThread()) {
            new Throwable("Method should execute on dispatch thread, not " + Thread.currentThread().getName()).printStackTrace();
        }
    }

}
