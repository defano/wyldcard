package hypercard.paint.utils;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MarchingAnts {

    private static MarchingAnts instance;

    private int antsPhase;
    private ScheduledExecutorService antsAnimator = Executors.newSingleThreadScheduledExecutor();
    private Future antsAnimation;
    private Set<MarchingAntsObserver> observers = new HashSet<>();

    private MarchingAnts() {
        startMarching();
    }

    synchronized public static MarchingAnts getInstance() {
        if (instance == null) {
            instance = new MarchingAnts();
        }

        return instance;
    }

    public void startMarching() {
        stopMarching();

        antsAnimation = antsAnimator.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                antsPhase = antsPhase + 1 % 5;
                fireMarchingAntsObservers();
            });

        }, 0, 20, TimeUnit.MILLISECONDS);
    }

    public void stopMarching() {
        if (antsAnimation != null) {
            antsAnimation.cancel(false);
        }
    }

    public Stroke getMarchingAnts() {
        return new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f}, antsPhase);
    }

    public void addObserver(MarchingAntsObserver observer) {
        this.observers.add(observer);
    }

    public boolean removeObserver(MarchingAntsObserver observer) {
        return this.observers.remove(observer);
    }

    private void fireMarchingAntsObservers() {
        for (MarchingAntsObserver thisObserver : observers) {
            thisObserver.onAntsMoved();
        }
    }
}
