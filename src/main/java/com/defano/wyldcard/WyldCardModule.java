package com.defano.wyldcard;

import com.defano.wyldcard.awt.DefaultKeyboardManager;
import com.defano.wyldcard.awt.DefaultMouseManager;
import com.defano.wyldcard.awt.KeyboardManager;
import com.defano.wyldcard.awt.MouseManager;
import com.google.inject.AbstractModule;

public class WyldCardModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MouseManager.class).to(DefaultMouseManager.class);
        bind(KeyboardManager.class).to(DefaultKeyboardManager.class);
    }
}
