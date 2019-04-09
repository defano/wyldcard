package com.defano.wyldcard.window.layouts;

import com.defano.jmonet.canvas.JMonetCanvas;
import com.defano.jmonet.canvas.layer.ImageLayer;
import com.defano.jmonet.canvas.layer.ImageLayerSet;
import com.defano.jmonet.model.Interpolation;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.builder.PaintToolBuilder;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.patterns.WyldCardPatternFactory;
import com.defano.wyldcard.window.WyldCardDialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class PatternEditor extends WyldCardDialog<Integer> {

    private final static int EDIT_SCALE = 32;
    private final static int PREVIEW_SIZE = 80;

    private JPanel windowPanel;
    private JPanel editorPanel;
    private JButton saveButton;
    private JPanel patternPanel;
    private JButton undoButton;
    private JLabel preview;
    private JButton revertButton;
    private JMonetCanvas patternCanvas;

    private int patternId;

    public PatternEditor() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                patternCanvas.dispose();
            }
        });

        undoButton.addActionListener(e -> {
            TexturePaint paint = WyldCardPatternFactory.getInstance().getPattern(patternId);
            setPreviewPaint(paint);
            setCanvasImage(makePaintImage(paint));
        });

        revertButton.addActionListener(e -> {
            StackModel focusedStack = WyldCard.getInstance().getStackManager().getFocusedStack().getStackModel();
            focusedStack.setUserPattern(patternId, null);
            setPreviewPaint(WyldCardPatternFactory.getInstance().getPattern(patternId));
            setCanvasImage(makePaintImage(WyldCardPatternFactory.getInstance().getPattern(patternId)));
        });

        saveButton.addActionListener(e -> {
            StackModel focusedStack = WyldCard.getInstance().getStackManager().getFocusedStack().getStackModel();
            focusedStack.setUserPattern(patternId, patternCanvas.getCanvasImage());
            dispose();
        });
    }

    @Override
    public JComponent getWindowPanel() {
        return windowPanel;
    }

    @Override
    public void bindModel(Integer patternId) {

        this.patternId = patternId;
        TexturePaint paint = WyldCardPatternFactory.getInstance().getPattern(patternId);

        setPreviewPaint(paint);

        patternCanvas = new JMonetCanvas(makePaintImage(paint));
        patternCanvas.setScale(EDIT_SCALE);
        patternCanvas.addCanvasCommitObserver((canvas, imageLayerSet, canvasImage) -> setPreviewPaint(new TexturePaint(canvasImage, new Rectangle(0, 0, WyldCardPatternFactory.PATTERN_WIDTH, WyldCardPatternFactory.PATTERN_HEIGHT))));

        PaintToolBuilder.create(PaintToolType.PENCIL)
                .makeActiveOnCanvas(patternCanvas)
                .withEraseColor(Color.WHITE)
                .withAntiAliasing(Interpolation.NONE)
                .build();

        editorPanel.setSize(new Dimension(EDIT_SCALE * WyldCardPatternFactory.PATTERN_WIDTH, EDIT_SCALE * WyldCardPatternFactory.PATTERN_HEIGHT));
        editorPanel.add(patternCanvas);
        editorPanel.invalidate();
    }

    private BufferedImage makePaintImage(TexturePaint paint) {
        BufferedImage paintImage = new BufferedImage(WyldCardPatternFactory.PATTERN_WIDTH, WyldCardPatternFactory.PATTERN_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = paintImage.createGraphics();
        g.setPaint(paint);
        g.fillRect(0, 0, WyldCardPatternFactory.PATTERN_WIDTH, WyldCardPatternFactory.PATTERN_HEIGHT);
        g.dispose();

        return paintImage;
    }

    private void setCanvasImage(BufferedImage image) {
        patternCanvas.commit(new ImageLayerSet(new ImageLayer(image)));
    }

    private void setPreviewPaint(TexturePaint paint) {
        BufferedImage previewImage = new BufferedImage(PREVIEW_SIZE, PREVIEW_SIZE, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = previewImage.createGraphics();
        g.setPaint(paint);
        g.fillRect(0, 0, PREVIEW_SIZE, PREVIEW_SIZE);
        g.dispose();

        ImageIcon previewIcon = new ImageIcon(previewImage);
        preview.setIcon(previewIcon);
    }

    @Override
    public JButton getDefaultButton() {
        return saveButton;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        windowPanel = new JPanel();
        windowPanel.setLayout(new GridLayoutManager(6, 2, new Insets(10, 10, 10, 10), -1, -1));
        editorPanel = new JPanel();
        editorPanel.setLayout(new BorderLayout(0, 0));
        windowPanel.add(editorPanel, new GridConstraints(0, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        editorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        patternPanel = new JPanel();
        patternPanel.setLayout(new BorderLayout(0, 0));
        windowPanel.add(patternPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        patternPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        preview = new JLabel();
        preview.setText("");
        patternPanel.add(preview, BorderLayout.CENTER);
        final Spacer spacer1 = new Spacer();
        windowPanel.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        windowPanel.add(saveButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        windowPanel.add(spacer2, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        undoButton = new JButton();
        undoButton.setText("Undo");
        windowPanel.add(undoButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        windowPanel.add(spacer3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        revertButton = new JButton();
        revertButton.setText("Revert");
        windowPanel.add(revertButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return windowPanel;
    }

}
