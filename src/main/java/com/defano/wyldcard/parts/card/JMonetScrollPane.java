package com.defano.wyldcard.parts.card;

import com.defano.jmonet.canvas.JMonetCanvas;

import javax.swing.*;

class JMonetScrollPane extends JScrollPane {

    private final JMonetCanvas canvas;

    public JMonetScrollPane(JMonetCanvas canvas) {
        this.canvas = canvas;

        setViewportView(canvas);
        getViewport().setOpaque(false);
        setBorder(null);
        setOpaque(false);

        revalidate();
    }

    public JMonetCanvas getCanvas() {
        return canvas;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        canvas.setVisible(visible);
    }
}
