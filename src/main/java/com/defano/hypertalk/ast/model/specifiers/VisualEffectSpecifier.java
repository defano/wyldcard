package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.VisualEffectImage;
import com.defano.hypertalk.ast.model.VisualEffectSpeed;
import com.defano.jsegue.SegueName;

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

    @Override
    public String toString() {
        return "VisualEffectSpecifier{" +
                "name=" + name +
                ", speed=" + speed +
                ", image=" + image +
                '}';
    }
}
