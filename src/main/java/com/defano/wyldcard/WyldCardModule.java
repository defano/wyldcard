package com.defano.wyldcard;

import com.defano.wyldcard.awt.DefaultMouseManager;
import com.defano.wyldcard.awt.MouseManager;
import com.google.inject.AbstractModule;

public class WyldCardModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MouseManager.class).to(DefaultMouseManager.class);
    }
}
