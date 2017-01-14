package hypercard.paint.canvas;


import hypercard.paint.model.Provider;
import hypercard.paint.utils.Geometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class SwingCanvas extends JPanel implements Canvas, KeyListener, MouseListener, MouseMotionListener {

    private Provider<Double> scale = new Provider<>(1.0);
    private Provider<Integer> gridSpacing = new Provider<>(1);

    private Point imageLocation = new Point(0, 0);
    private List<CanvasObserver> observers = new ArrayList<>();
    private List<CanvasInteractionListener> interactionListeners = new ArrayList<>();

    public SwingCanvas() {
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
    public void addObserver(CanvasObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an existing observer.
     * @param observer The observer to be removed.
     * @return True if the given observer was successfully unregistered; false otherwise.
     */
    public boolean removeObserver(CanvasObserver observer) {
        return observers.remove(observer);
    }

    @Override
    public void addCanvasInteractionListener(CanvasInteractionListener listener) {
        interactionListeners.add(listener);
    }

    @Override
    public boolean removeCanvasInteractionListener(CanvasInteractionListener listener) {
        return interactionListeners.remove(listener);
    }

    protected void fireObservers(BufferedImage committedImage, BufferedImage canvasImage) {
        for (CanvasObserver thisObserver : observers) {
            thisObserver.onCommit(this, committedImage, canvasImage);
        }
    }

    /**
     * Causes the Swing framework to redraw/refresh the Canvas by calling repaint on the Canvas's parent component.
     */
    public void repaintCanvas() {
        if (getParent() != null) {
            getParent().repaint();
        }
    }

    @Override
    public void setImageLocation(Point location) {
        imageLocation = location;
    }

    @Override
    public Point getImageLocation() {
        return imageLocation;
    }

    @Override
    public Provider<Double> getScaleProvider() {
        return scale;
    }

    @Override
    public void setGridSpacing(int grid) {
        this.gridSpacing.set(grid);
    }

    @Override
    public Provider<Integer> getGridSpacingProvider() {
        return gridSpacing;
    }

    @Override
    public void setScale(double scale) {
        this.scale.set(scale);
        repaintCanvas();
    }

    private int translateX(int x) {
        x = Geometry.round(x, (int) (gridSpacing.get() * scale.get()));
        return (int) (getImageLocation().x / scale.get() + (x / scale.get()));
    }

    private int translateY(int y) {
        y = Geometry.round(y, (int) (gridSpacing.get() * scale.get()));
        return (int) (getImageLocation().y / scale.get() + (y / scale.get()));
    }

    /**
     * Invoked by the Swing framework to render this component (i.e., draw the Canvas).
     * @param g The shared Graphics context in which the component should be rendered.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isVisible()) {
            double scale = this.scale.get();
            Point offset = getImageLocation();

            Graphics2D g2d = (Graphics2D) g;
            BufferedImage canvasImage = getCanvasImage();
            BufferedImage scratchImage = getScratchImage();

            g2d.drawImage(canvasImage, -offset.x, -offset.y, (int) (canvasImage.getWidth() * scale), (int) (canvasImage.getHeight() * scale), null);
            g2d.drawImage(scratchImage, -offset.x, -offset.y, (int) (scratchImage.getWidth() * scale), (int) (scratchImage.getHeight() * scale), null);
        }
    }

    @Override
    public final void keyTyped(KeyEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.keyTyped(e);
        }
    }

    @Override
    public final void keyPressed(KeyEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.keyPressed(e);
        }
    }

    @Override
    public final void keyReleased(KeyEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.keyReleased(e);
        }
    }

    @Override
    public final void mouseClicked(MouseEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.mouseClicked(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mousePressed(MouseEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.mousePressed(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.mouseReleased(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseEntered(MouseEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.mouseEntered(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseExited(MouseEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.mouseExited(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseDragged(MouseEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.mouseDragged(e, translateX(e.getX()), translateY(e.getY()));
        }
    }

    @Override
    public final void mouseMoved(MouseEvent e) {
        for(CanvasInteractionListener thisListener : interactionListeners) {
            thisListener.mouseMoved(e, translateX(e.getX()), translateY(e.getY()));
        }
    }
}
