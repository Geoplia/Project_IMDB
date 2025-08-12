package com.uop.imdb.ui.dialogs;

import com.uop.imdb.model.*;
import com.uop.imdb.model.enums.Genre;
import com.uop.imdb.storage.DataStore;
import com.uop.imdb.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddSeriesDialog extends JDialog {
    private final DataStore store;

    public AddSeriesDialog(JFrame owner, DataStore store) {
        super(owner, "Add Series", true);
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

        comp.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                comp.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.ACCENT, 2),
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                comp.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.OUTLINE, 1),
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
            }
        });
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(UITheme.UI);
        return lbl;
    }

    private void init() {
        setSize(520, 380);
        setLocationRelativeTo(getOwner());

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BG);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField();
        styleField(titleField);

        JComboBox<String> genreBox = new JComboBox<>(java.util.Arrays.stream(Genre.values()).map(Enum::toString).toArray(String[]::new));
        styleField(genreBox);

        JCheckBox addSeasonChk = new JCheckBox("Add initial season?");
        addSeasonChk.setForeground(Color.WHITE);
        addSeasonChk.setOpaque(false);

        JSpinner seasonYearSpinner = new JSpinner(new SpinnerNumberModel(2024, 1870, 2100, 1));
        styleField(seasonYearSpinner);

        JCheckBox addEpisodeChk = new JCheckBox("Add initial episode?");
        addEpisodeChk.setForeground(Color.WHITE);
        addEpisodeChk.setOpaque(false);

        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(45, 1, 1000, 1));
        styleField(durationSpinner);

        JComboBox<String> directorBox = new JComboBox<>();
        styleField(directorBox);

        JComboBox<String> actorBox = new JComboBox<>();
        styleField(actorBox);

        JSpinner imdbSpinner = new JSpinner(new SpinnerNumberModel(7.0, 0.0, 10.0, 0.1));
        styleField(imdbSpinner);

        // populate lists
        List<Director> dirs = store.getDirectors();
        for (Director d : dirs) directorBox.addItem(d.getFullName());
        List<Actor> acts = store.getActors();
        for (Actor a : acts) actorBox.addItem(a.getFullName());

        int row = 0;
        c.gridx = 0; c.gridy = row; p.add(label("Title:"), c);
        c.gridx = 1; p.add(titleField, c); row++;

        c.gridx = 0; c.gridy = row; p.add(label("Genre:"), c);
        c.gridx = 1; p.add(genreBox, c); row++;

        c.gridx = 0; c.gridy = row; p.add(addSeasonChk, c); row++;
        c.gridx = 0; c.gridy = row; p.add(label("Season year:"), c);
        c.gridx = 1; p.add(seasonYearSpinner, c); row++;

        c.gridx = 0; c.gridy = row; p.add(addEpisodeChk, c); row++;
        c.gridx = 0; c.gridy = row; p.add(label("Duration (min):"), c);
        c.gridx = 1; p.add(durationSpinner, c); row++;

        c.gridx = 0; c.gridy = row; p.add(label("Director:"), c);
        c.gridx = 1; p.add(directorBox, c); row++;

        c.gridx = 0; c.gridy = row; p.add(label("Main Actor:"), c);
        c.gridx = 1; p.add(actorBox, c); row++;

        c.gridx = 0; c.gridy = row; p.add(label("Episode IMDb:"), c);
        c.gridx = 1; p.add(imdbSpinner, c);

        // disable fields initially
        seasonYearSpinner.setEnabled(false);
        durationSpinner.setEnabled(false);
        directorBox.setEnabled(false);
        actorBox.setEnabled(false);
        imdbSpinner.setEnabled(false);

        addSeasonChk.addActionListener(e -> seasonYearSpinner.setEnabled(addSeasonChk.isSelected()));
        addEpisodeChk.addActionListener(e -> {
            boolean en = addEpisodeChk.isSelected();
            durationSpinner.setEnabled(en);
            directorBox.setEnabled(en);
            actorBox.setEnabled(en);
            imdbSpinner.setEnabled(en);
            if (en) addSeasonChk.setSelected(true);
            seasonYearSpinner.setEnabled(addSeasonChk.isSelected());
        });

        // bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UITheme.BG);

        JButton ok = new JButton("Add");
       styleMiniButton(ok);
        ok.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                if (title.isEmpty()) throw new IllegalArgumentException("Title required.");
                Genre g = Genre.fromString((String) genreBox.getSelectedItem());
                Series s = new Series(title, g);
                store.addSeries(s);

                if (addSeasonChk.isSelected()) {
                    int year = (Integer) seasonYearSpinner.getValue();
                    Season season = new Season(year);
                    s.addSeason(season);

                    if (addEpisodeChk.isSelected()) {
                        int dur = (Integer) durationSpinner.getValue();
                        String dname = (String) directorBox.getSelectedItem();
                        String aname = (String) actorBox.getSelectedItem();
                        double imdb = ((Number) imdbSpinner.getValue()).doubleValue();
                        Director director = store.findDirectorByName(dname)
                                .orElseGet(() -> new Director(dname, "", null, null, null));
                        Actor actor = store.findActorByName(aname).orElse(null);
                        Episode ep = new Episode(dur, director, imdb, actor);
                        season.addEpisode(ep);
                        if (imdb > 7.5) director.addKnownWork(s.getTitle());
                    }
                }

                JOptionPane.showMessageDialog(this, "Series added.");
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


    private void styleMiniButton(JButton b) {
        b.setFont(UITheme.UI);
        b.setBackground(UITheme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }


}
