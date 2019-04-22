package com.defano;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class AlphaCompositeDemo extends JFrame {
    MyCanvas canvas;

    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 5);

    JComboBox rulesBox;

    String[] rulesLabels = { "Clear", "Source", "Source-over",
            "Destination-over", "Source-in", "Destination-in", "Source-out",
            "Destination-out", "Xor"};

    int[] rules = { AlphaComposite.CLEAR, AlphaComposite.SRC,
            AlphaComposite.SRC_OVER, AlphaComposite.DST_OVER,
            AlphaComposite.SRC_IN, AlphaComposite.DST_IN,
            AlphaComposite.SRC_OUT, AlphaComposite.DST_OUT, AlphaComposite.XOR };

    public AlphaCompositeDemo() {
        super();
        Container container = getContentPane();

        canvas = new MyCanvas();
        container.add(canvas);

        rulesBox = new JComboBox(rulesLabels);
        rulesBox.setSelectedIndex(0);
        rulesBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        rulesBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                canvas.compositeRule = rules[cb.getSelectedIndex()];
                canvas.repaint();
            }
        });

        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(25);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                canvas.alphaValue = (float) slider.getValue() / 100;
                canvas.repaint();
            }
        });



        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));
        panel.add(rulesBox);
        panel.add(new JLabel("Alpha Adjustment x E-2: ", JLabel.RIGHT));
        panel.add(slider);
        container.add(panel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setSize(500,300);
        setVisible(true);
    }

    public static void main(String arg[]) {
        new AlphaCompositeDemo();
    }

    class MyCanvas extends JLabel {
        float alphaValue = 1.0f;

        int compositeRule = AlphaComposite.CLEAR;

        AlphaComposite ac;

        public void paint(Graphics g) {
            Graphics2D g2D = (Graphics2D) g;

            int w = getSize().width;
            int h = getSize().height;

            BufferedImage bi = new BufferedImage(w, h,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D big = bi.createGraphics();

            ac = AlphaComposite.getInstance(compositeRule, alphaValue);

            big.setColor(Color.red);
            big.drawString("Destination", w / 4, h / 4);
            big.fill(new Ellipse2D.Double(0, h / 3, 2 * w / 3, h / 3));

            big.setColor(Color.blue);
            big.drawString("Source", 3 * w / 4, h / 4);

            big.setComposite(ac);
            big.fill(new Ellipse2D.Double(w / 3, h / 3, 2 * w / 3, h / 3));

            g2D.drawImage(bi, null, 0, 0);
        }
    }

}

