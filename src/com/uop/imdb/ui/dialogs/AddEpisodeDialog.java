package com.uop.imdb.ui.dialogs;

import com.uop.imdb.model.*;
import com.uop.imdb.storage.DataStore;
import com.uop.imdb.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddEpisodeDialog extends JDialog {
    private final DataStore store;
    private final JComboBox<String> seriesBox = new JComboBox<>();
    private final JComboBox<Season> seasonBox = new JComboBox<>();

    public AddEpisodeDialog(JFrame owner, DataStore store) {
        super(owner, "Add Episode", true);
        this.store = store;
        setSize(520, 340);
        setLocationRelativeTo(getOwner());
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
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BG);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        styleField(seriesBox);
        styleField(seasonBox);

        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(45, 1, 1000, 1));
        styleField(durationSpinner);

        JComboBox<String> directorBox = new JComboBox<>();
        styleField(directorBox);

        JComboBox<String> actorBox = new JComboBox<>();
        styleField(actorBox);

        JSpinner imdbSpinner = new JSpinner(new SpinnerNumberModel(7.0, 0.0, 10.0, 0.1));
        styleField(imdbSpinner);

        // populate dropdowns
        for (Series s : store.getAllSeries()) seriesBox.addItem(s.getTitle());
        for (Director d : store.getDirectors()) directorBox.addItem(d.getFullName());
        for (Actor a : store.getActors()) actorBox.addItem(a.getFullName());

        seriesBox.addActionListener(e -> loadSeasons());

        int row = 0;
        c.gridx = 0; c.gridy = row; p.add(label("Series:"), c);
        c.gridx = 1; p.add(seriesBox, c); row++;

        c.gridx = 0; c.gridy = row; p.add(label("Season:"), c);
        c.gridx = 1; p.add(seasonBox, c); row++;

        c.gridx = 0; c.gridy = row; p.add(label("Duration (min):"), c);
        c.gridx = 1; p.add(durationSpinner, c); row++;

        c.gridx = 0; c.gridy = row; p.add(label("Director:"), c);
        c.gridx = 1; p.add(directorBox, c); row++;

        c.gridx = 0; c.gridy = row; p.add(label("Main Actor:"), c);
        c.gridx = 1; p.add(actorBox, c); row++;

        c.gridx = 0; c.gridy = row; p.add(label("IMDb rating:"), c);
        c.gridx = 1; p.add(imdbSpinner, c);

        loadSeasons();

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UITheme.BG);

        JButton ok = new JButton("Add Episode");
        styleMiniButton(ok);
        ok.addActionListener(e -> {
            try {
                String seriesTitle = (String) seriesBox.getSelectedItem();
                Series s = store.findSeriesByTitle(seriesTitle).orElse(null);
                if (s == null) throw new IllegalArgumentException("Series not found.");
                Season season = (Season) seasonBox.getSelectedItem();
                if (season == null) throw new IllegalArgumentException("Season not selected.");

                int dur = (Integer) durationSpinner.getValue();
                String dname = (String) directorBox.getSelectedItem();
                String aname = (String) actorBox.getSelectedItem();
                double imdb = ((Number) imdbSpinner.getValue()).doubleValue();

                Director director = store.findDirectorByName(dname)
                        .orElseGet(() -> new Director(dname, "", null, null, null));
                Actor actor = store.findActorByName(aname).orElse(null);

                Episode ep = new Episode(dur, director, imdb, actor);
                season.addEpisode(ep);
                if (imdb > 7.5) director.addKnownWork(seriesTitle);

                JOptionPane.showMessageDialog(this, "Episode added.");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancel = new JButton("Cancel");
        styleMiniButton(cancel);
        cancel.addActionListener(e -> dispose());

        bottom.add(ok);
        bottom.add(cancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(p), BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    private void loadSeasons() {
        seasonBox.removeAllItems();
        String seriesTitle = (String) seriesBox.getSelectedItem();
        if (seriesTitle == null) return;
        Series s = store.findSeriesByTitle(seriesTitle).orElse(null);
        if (s == null) return;
        for (Season season : s.getSeasons()) seasonBox.addItem(season);
    }

    private void styleMiniButton(JButton b) {
        b.setFont(UITheme.UI);
        b.setBackground(UITheme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }
}

