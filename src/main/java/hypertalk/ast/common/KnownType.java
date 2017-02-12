/*
 * KnownType
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/12/17 8:48 AM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package hypertalk.ast.common;

import com.google.common.collect.Lists;
import hypertalk.exception.HtSemanticException;

import java.util.List;

public enum  KnownType {
    NUMBER("number"),
    INTEGER("integer"),
    POINT("point"),
    RECT("rect", "rectangle"),
    DATE("date"),
    LOGICAL("logical", "boolean", "bool");

    private final List<String> name;

    KnownType(String... name) {
        this.name = Lists.newArrayList(name);
    }

    public static KnownType getTypeByName(String name) throws HtSemanticException {
        for (KnownType thisType : KnownType.values()) {
            if (thisType.name.contains(name.toLowerCase())) {
                return thisType;
            }
        }

        throw new HtSemanticException(name + " is not a type.");
    }

}
