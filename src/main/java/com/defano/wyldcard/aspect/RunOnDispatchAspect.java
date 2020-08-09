package com.defano.wyldcard.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

@SuppressWarnings("unused")
@Aspect
public class RunOnDispatchAspect {

    private static final Logger LOG = LoggerFactory.getLogger(RunOnDispatch.class);

    @Pointcut("execution(@com.defano.wyldcard.aspect.RunOnDispatch * *(..))")
    public void runOnDispatchMethods() {
        // Nothing to do
    }

    @Before("runOnDispatchMethods()")
    public void runOnDispatch(JoinPoint joinPoint) {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.error("Method should execute on dispatch thread, not {}", Thread.currentThread().getName(),
                    new Throwable());
        }
    }

}
