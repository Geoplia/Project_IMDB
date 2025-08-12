package com.uop.imdb.ui.dialogs;

import com.uop.imdb.model.Season;
import com.uop.imdb.model.Series;
import com.uop.imdb.storage.DataStore;
import com.uop.imdb.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddSeasonDialog extends JDialog {
    private final DataStore store;

    public AddSeasonDialog(JFrame owner, DataStore store) {
        super(owner, "Add Season", true);
        this.store = store;
        init();
    }

    private void styleField(JComponent comp) {
        comp.setFont(UITheme.UI);
        comp.setForeground(Color.WHITE);
        comp.setBackground(UITheme.SURFACE);
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.OUTLINE, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        comp.setOpaque(true);
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(UITheme.UI);
        return lbl;
    }

    private void init() {
        setSize(420, 180);
        setLocationRelativeTo(getOwner());

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BG);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> seriesBox = new JComboBox<>();
        styleField(seriesBox);
        List<Series> seriesList = store.getAllSeries();
        for (Series s : seriesList) seriesBox.addItem(s.getTitle());

        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2024, 1870, 2100, 1));
        styleField(yearSpinner);

        c.gridx = 0; c.gridy = 0; p.add(label("Series:"), c);
        c.gridx = 1; p.add(seriesBox, c);

        c.gridx = 0; c.gridy = 1; p.add(label("Season year:"), c);
        c.gridx = 1; p.add(yearSpinner, c);

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UITheme.BG);

        JButton ok = new JButton("Add Season");
        styleMiniButton(ok);
        ok.addActionListener(e -> {
            String title = (String) seriesBox.getSelectedItem();
            if (title == null) {
                JOptionPane.showMessageDialog(this, "No series selected.");
                return;
            }
            Series s = store.findSeriesByTitle(title).orElse(null);
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Series not found.");
                return;
            }
            int year = (Integer) yearSpinner.getValue();
            Season season = new Season(year);
            s.addSeason(season);
            JOptionPane.showMessageDialog(this, "Season added to " + s.getTitle());
            dispose();
        });

        JButton cancel = new JButton("Cancel");
        styleMiniButton(cancel);
        cancel.addActionListener(e -> dispose());

        bottom.add(ok);
        bottom.add(cancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(p, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    private void styleMiniButton(JButton b) {
        b.setFont(UITheme.UI);
        b.setBackground(UITheme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }
}