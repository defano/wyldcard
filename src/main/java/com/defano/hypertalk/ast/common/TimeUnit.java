/*
 * TimeUnit
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.common;

public enum TimeUnit {
    TICKS, SECONDS;

    public int toMilliseconds (double count) {
        switch (this) {
            case SECONDS:
                return (int)(count * 1000);
            case TICKS:
                return (int)((count / 60.0) * 1000);
            default:
                throw new RuntimeException("Bug! Unimplemented case.");
        }
    }
}
