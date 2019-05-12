package com.defano.hypertalk.ast.model.effect;

import com.defano.hypertalk.exception.HtSemanticException;

import java.util.Arrays;
import java.util.List;

public enum VisualEffectImage {
    BLACK("black"),
    GRAY("gray", "grey"),
    CARD("card"),
    INVERSE("inverse"),
    WHITE("white");

    private final List<String> hypertalkNames;

    VisualEffectImage(String... hypertalkNames) {
        this.hypertalkNames = Arrays.asList(hypertalkNames);
    }

    public static VisualEffectImage fromHypertalkName(String hypertalkName) throws HtSemanticException {
        String name = hypertalkName.toLowerCase().replaceAll("\\s+", "");

        return Arrays.stream(values())
                .filter(v -> v.hypertalkNames.contains(hypertalkName))
                .findFirst()
                .orElseThrow(() -> new HtSemanticException("Not a visual effect image."));
    }
}
