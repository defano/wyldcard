package com.defano.hypertalk.ast.model.specifier;

import com.defano.hypertalk.ast.model.effect.VisualEffectImage;
import com.defano.hypertalk.ast.model.effect.VisualEffectSpeed;
import com.defano.jsegue.AnimatedSegue;

public class VisualEffectSpecifier {

    public final Class<? extends AnimatedSegue> effect;
    public final VisualEffectSpeed speed;
    public final VisualEffectImage image;

    public VisualEffectSpecifier(Class<? extends AnimatedSegue> effect, VisualEffectSpeed speed, VisualEffectImage image) {
        this.effect = effect;
        this.speed = speed;
        this.image = image;
    }

    @Override
    public String toString() {
        return "VisualEffectSpecifier{" +
                "effect=" + effect +
                ", speed=" + speed +
                ", image=" + image +
                '}';
    }
}
