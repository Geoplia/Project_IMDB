package com.uop.imdb.ui;

import com.uop.imdb.storage.DataStore;
import com.uop.imdb.storage.FileParser;

import javax.swing.*;
import java.io.File;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }
            catch (Exception ignored) {}

            // small UI tweaks
            UITheme.applyDefaults();

            DataStore store = new DataStore();
            FileParser parser = new FileParser(store);
            try {
                String dataDir = "data";
                if (!new File(dataDir).exists()) {
                    JOptionPane.showMessageDialog(null, "data/ directory not found. Please create data/ and add the required files.", "Missing data", JOptionPane.WARNING_MESSAGE);
                } else {
                    parser.parseAll(dataDir);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error parsing data: " + ex.getMessage(), "Parse error", JOptionPane.ERROR_MESSAGE);
            }

            MainFrame mf = new MainFrame(store);
            mf.setVisible(true);
        });
    }
}
