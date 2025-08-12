//package com.uop.imdb.ui;
//
//import com.uop.imdb.storage.DataStore;
//import com.uop.imdb.storage.FileParser;
//
//import javax.swing.*;
//import java.io.File;
//
//// Main application entry point
//public class App {
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                // Modern UI theme
//                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//            } catch (Exception ignored) {}
//
//            // Initialize data store and parser
//            DataStore store = new DataStore();
//            FileParser parser = new FileParser(store);
//
//            try {
//                // Load data from files
//                String dataDir = "data";
//                if (!new File(dataDir).exists()) {
//                    JOptionPane.showMessageDialog(null,
//                            "data/ directory not found.\nPlease create data/ and add the required files.",
//                            "Missing Data Folder", JOptionPane.ERROR_MESSAGE);
//                } else {
//                    parser.parseAll(dataDir);
//                }
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(null, "Error parsing data: " + ex.getMessage(),
//                        "Parsing Error", JOptionPane.ERROR_MESSAGE);
//            }
//
//            // Launch main window
//            MainFrame frame = new MainFrame(store);
//            frame.setVisible(true);
//        });
//    }
//}

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