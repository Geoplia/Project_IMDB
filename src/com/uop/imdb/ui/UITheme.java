package com.uop.imdb.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Central theme helpers for a modern, cinematic dark UI.
 */
public final class UITheme {
    public static final Color BG = new Color(20, 20, 21);
    public static final Color SURFACE = new Color(30, 34, 42);
    public static final Color MUTED = new Color(255, 189, 61);
    public static final Color ACCENT = new Color(17, 69, 225, 255);
    public static final Color CARD_START = new Color(48, 46, 36);
    public static final Color CARD_END = new Color(20, 20, 21);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font UI = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Color OUTLINE = new Color(255, 255, 255, 255);
    public static final Color POP_UP_BG = new Color(30, 34, 42);
    public static final Color BACKGROUND = new Color(0x1A1A1A);
    public static final Color PRIMARY_TEXT = new Color(0xFFFFFF);
    public static final Color SECONDARY_TEXT = new Color(0xAAAAAA);


    // Fonts
    //public static final Font TITLE = new Font("SansSerif", Font.BOLD, 18);
    //public static final Font UI = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font TEXT = new Font("SansSerif", Font.PLAIN, 14);
    private UITheme() {}

    public static void applyDefaults() {
        UIManager.put("Panel.background", BG);
        UIManager.put("Label.font", UI);
        UIManager.put("Button.font", UI);
        UIManager.put("TextField.font", UI);
        UIManager.put("Table.font", UI);
        UIManager.put("TabbedPane.font", UI);
        UIManager.put("ToggleButton.font", UI);
    }

}


