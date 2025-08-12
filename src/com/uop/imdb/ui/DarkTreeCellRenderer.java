package com.uop.imdb.ui;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class DarkTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        JLabel label = (JLabel) super.getTreeCellRendererComponent(
                tree, value, sel, expanded, leaf, row, hasFocus);

        // Background colors for selection & normal
        if (sel) {
            label.setBackground(UITheme.ACCENT);
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
        } else {
            label.setBackground(UITheme.BG);
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
        }

        // Different text style for Series / Seasons / Episodes
        String text = value.toString();
        if (text.equals("Series")) {
            label.setFont(UITheme.UI.deriveFont(Font.BOLD, 15f));
            label.setForeground(UITheme.ACCENT);
        } else if (text.startsWith("Season")) {
            label.setFont(UITheme.UI.deriveFont(Font.BOLD, 14f));
            label.setForeground(new Color(180, 200, 255)); // light blue
        } else {
            label.setFont(UITheme.UI.deriveFont(Font.PLAIN, 13f));
            label.setForeground(Color.LIGHT_GRAY);
        }

        return label;
    }
}
