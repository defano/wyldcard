package hypercard.paint.canvas;


import hypercard.paint.canvas.surface.PaintableSurface;
import hypercard.paint.utils.Geometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSwingCanvas extends JPanel implements PaintableSurface, KeyListener, MouseListener, MouseMotionListener {

    private List<CanvasCommitObserver> observers = new ArrayList<>();
    private List<CanvasInteractionObserver> interactionListeners = new ArrayList<>();

    public AbstractSwingCanvas() {
        setOpaque(false);
        setEnabled(true);
        setFocusable(true);
        setLayout(null);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    @Override
    public void addComponent(Component component) {
        add(component);
        revalidate();
        repaint();
    }

    @Override
    public void removeComponent(Component component) {
        remove(component);
        revalidate();
        repaint();
    }

    /**
     * Adds an observer to be notified of scratch buffer commits.
     * @param observer The observer to be registered.
     */
    public void addObserver(CanvasCommitObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an existing observer.
     * @param observer The observer to be removed.
     * @return True if the given observer was successfully unregistered; false otherwise.
     */
    public boolean removeObserver(CanvasCommitObserver observer) {
        return observers.remove(observer);
    }

    protected void fireObservers(Canvas canvas, BufferedImage committedImage, BufferedImage canvasImage) {
        for (CanvasCommitObserver thisObserver : observers) {
            thisObserver.onCommit(canvas, committedImage, canvasImage);
        }
    }

    /**
     * Causes the Swing framework to redraw/refresh the Canvas by calling repaint on the Canvas's parent component.
     */
    public void invalidateCanvas() {
        if (getParent() != null) {
            getParent().repaint();
        }
    }



    public void setSize(int width, int height) {
        super.setSize(width, height);

        // Don't truncate image if canvas shrinks, but do grow it
        if (width < getCanvasImage().getWidth()) {
            width = getCanvasImage().getWidth();
        }

        if (height < getCanvasImage().getHeight()) {
            height = getCanvasImage().getHeight();
        }

        BufferedImage newScratch = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics newScratchGraphics = newScratch.getGraphics();
        newScratchGraphics.drawImage(getScratchImage(), 0, 0, null);
        setScratchImage(newScratch);

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics newImageGraphics = newImage.getGraphics();
        newImageGraphics.drawImage(getCanvasImage(), 0, 0, null);
        setCanvasImage(newImage);

        newScratchGraphics.dispose();
        newImageGraphics.dispose();

        invalidateCanvas();
    }

    private int translateX(int x) {
        int gridSpacing = getGridSpacingProvider().get();
        double scale = getScaleProvider().get();

        x = Geometry.round(x, (int) (gridSpacing * scale));
        return (int) (getImageLocation().x / scale + (x / scale));
    }

    private int translateY(int y) {
        int gridSpacing = getGridSpacingProvider().get();
        double scale = getScaleProvider().get();

        y = Geometry.round(y, (int) (gridSpacing * scale));
        return (int) (getImageLocation().y / scale + (y / scale));
    }

    /**
     * Invoked by the Swing framework to render this component (i.e., draw the Canvas).
     * @param g The shared Graphics context in which the component should be rendered.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isVisible()) {
            double scale = getScaleProvider().get();
            Point offset = getImageLocation();

            Graphics2D g2d = (Graphics2D) g;
            BufferedImage canvasImage = getCanvasImage();
            BufferedImage scratchImage = getScratchImage();

            g2d.drawImage(canvasImage, -offset.x, -offset.y, (int) (canvasImage.getWidth() * scale), (int) (canvasImage.getHeight() * scale), null);
            g2d.drawImage(scratchImage, -offset.x, -offset.y, (int) (scratchImage.getWidth() * scale), (int) (scratchImage.getHeight() * scale), null);
        }
    }

    @Override
    public void addCanvasInteractionListener(CanvasInteractionObserver listener) {
        interactionListeners.add(listener);
    }

    @Override
    public boolean removeCanvasInteractionListener(CanvasInteractionObserver listener) {
        return interactionListeners.remove(listener);
    }

    @Override
    public final void keyTyped(KeyEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.keyTyped(e);
        }
    }

    @Override
    public final void keyPressed(KeyEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.keyPressed(e);
        }
    }

    @Override
    public final void keyReleased(KeyEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.keyReleased(e);
        }
    }

    @Override
    public final void mouseClicked(MouseEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.mouseClicked(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mousePressed(MouseEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.mousePressed(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.mouseReleased(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseEntered(MouseEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.mouseEntered(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseExited(MouseEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.mouseExited(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseDragged(MouseEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.mouseDragged(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseMoved(MouseEvent e) {
        for(CanvasInteractionObserver thisListener : interactionListeners) {
            thisListener.mouseMoved(e, translateX(e.getX()), translateY(e.getY()));
        }
    }
}
