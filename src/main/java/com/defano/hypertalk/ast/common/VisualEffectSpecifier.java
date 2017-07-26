package com.defano.hypertalk.ast.common;

import com.defano.hypercard.gui.fx.SegueName;

public class VisualEffectSpecifier {

    public final SegueName name;
    public final VisualEffectSpeed speed;
    public final VisualEffectImage image;

    public VisualEffectSpecifier(SegueName name) {
        this(name, VisualEffectSpeed.FAST, VisualEffectImage.CARD);
    }

    public VisualEffectSpecifier(SegueName name, VisualEffectSpeed speed) {
        this(name, speed, VisualEffectImage.CARD);
    }

    public VisualEffectSpecifier(SegueName name, VisualEffectImage image) {
        this(name, VisualEffectSpeed.FAST, image);
    }

    public VisualEffectSpecifier(SegueName name, VisualEffectSpeed speed, VisualEffectImage image) {
        this.name = name;
        this.speed = speed;
        this.image = image;
    }

}
