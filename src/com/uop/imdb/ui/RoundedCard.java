package com.uop.imdb.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Lightweight rounded card panel with gradient.
 */
public class RoundedCard extends JPanel {
    private final int arc;
    private final Color start;
    private final Color end;

    public RoundedCard(int arc, Color start, Color end) {
        this.arc = arc;
        this.start = start;
        this.end = end;
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    public RoundedCard() { this(14, UITheme.CARD_START, UITheme.CARD_END); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(0,0,start,0,h,end);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w-1, h-1, arc, arc);
        g2.setColor(new Color(255,255,255,18));
        g2.drawRoundRect(0,0,w-1,h-1,arc,arc);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override public Insets getInsets() { return new Insets(10,10,10,10); }
}
