package com.defano.hypertalk.ast.common;

public class VisualEffectSpecifier {

    public final VisualEffectName name;
    public final VisualEffectSpeed speed;
    public final VisualEffectImage image;

    public VisualEffectSpecifier(VisualEffectName name) {
        this(name, VisualEffectSpeed.FAST, VisualEffectImage.CARD);
    }

    public VisualEffectSpecifier(VisualEffectName name, VisualEffectSpeed speed) {
        this(name, speed, VisualEffectImage.CARD);
    }

    public VisualEffectSpecifier(VisualEffectName name, VisualEffectImage image) {
        this(name, VisualEffectSpeed.FAST, image);
    }

    public VisualEffectSpecifier(VisualEffectName name, VisualEffectSpeed speed, VisualEffectImage image) {
        this.name = name;
        this.speed = speed;
        this.image = image;
    }

}
