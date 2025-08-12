package com.uop.imdb.ui.dialogs;

import com.uop.imdb.model.Actor;
import com.uop.imdb.model.Director;
import com.uop.imdb.model.Movie;
import com.uop.imdb.model.enums.Genre;
import com.uop.imdb.storage.DataStore;
import com.uop.imdb.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddMovieDialog extends JDialog {
    private final DataStore store;

    public AddMovieDialog(JFrame owner, DataStore store) {
        super(owner, "Add Movie", true);
        this.store = store;
        init();
    }

    private void init() {
        setSize(520, 380);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout(10, 10));

        // === Title ===
        JLabel header = new JLabel("Add New Movie", SwingConstants.CENTER);
        header.setFont(UITheme.TITLE);
        header.setForeground(UITheme.ACCENT);
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(header, BorderLayout.NORTH);

        // === Form Panel ===
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.POP_UP_BG);
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        JTextField titleField = new JTextField();
        styleField(titleField);

        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2024, 1870, 2100, 1));
        styleField(yearSpinner);

        JComboBox<String> genreBox = new JComboBox<>(java.util.Arrays.stream(Genre.values()).map(Enum::toString).toArray(String[]::new));
        styleField(genreBox);

        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(90, 1, 1000, 1));
        styleField(durationSpinner);

        JComboBox<String> directorBox = new JComboBox<>();
        styleField(directorBox);

        JComboBox<String> actorBox = new JComboBox<>();
        styleField(actorBox);

        JSpinner imdbSpinner = new JSpinner(new SpinnerNumberModel(5.0, 0.0, 10.0, 0.1));
        styleField(imdbSpinner);

        // Populate dropdowns
        store.getDirectors().forEach(d -> directorBox.addItem(d.getFullName()));
        store.getActors().forEach(a -> actorBox.addItem(a.getFullName()));

        // Add components
        int row = 0;
        row = addField(form, gbc, row, "Title:", titleField);
        row = addField(form, gbc, row, "Year:", yearSpinner);
        row = addField(form, gbc, row, "Genre:", genreBox);
        row = addField(form, gbc, row, "Duration (min):", durationSpinner);
        row = addField(form, gbc, row, "Director:", directorBox);
        row = addField(form, gbc, row, "Main Actor:", actorBox);
        row = addField(form, gbc, row, "IMDb Rating:", imdbSpinner);

        add(form, BorderLayout.CENTER);

        // === Footer Buttons ===
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(UITheme.POP_UP_BG);

        JButton addBtn = new JButton("Add Movie");
        styleActionButton(addBtn, UITheme.ACCENT);
        addBtn.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                int year = (Integer) yearSpinner.getValue();
                Genre g = Genre.fromString((String) genreBox.getSelectedItem());
                int dur = (Integer) durationSpinner.getValue();
                String dname = (String) directorBox.getSelectedItem();
                String aname = (String) actorBox.getSelectedItem();
                double imdb = ((Number) imdbSpinner.getValue()).doubleValue();

                Director director = store.findDirectorByName(dname)
                        .orElseThrow(() -> new Exception("Director not found"));
                Actor actor = store.findActorByName(aname).orElse(null);

                store.addMovie(new Movie(title, year, g, dur, director, imdb, actor));
                JOptionPane.showMessageDialog(this, "✅ Movie added successfully!", "Success", JOptionPane.PLAIN_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        styleActionButton(cancelBtn, Color.GRAY);
        cancelBtn.addActionListener(e -> dispose());

        footer.add(addBtn);
        footer.add(cancelBtn);
        add(footer, BorderLayout.SOUTH);
    }

    // === Helper: Field Row ===
    private int addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component input) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(UITheme.UI);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(input, gbc);

        return row + 1;
    }

    // === Helper: Styled Components ===
    private JTextField themedTextField() {
        JTextField tf = new JTextField();
        tf.setFont(UITheme.UI);
        return tf;
    }

    private JComboBox<String> themedCombo(Genre[] genres) {
        JComboBox<String> box = new JComboBox<>();
        for (Genre g : genres) box.addItem(g.toString());
        box.setFont(UITheme.UI);
        return box;
    }

    private void styleActionButton(JButton btn, Color bg) {
        btn.setFont(UITheme.UI.deriveFont(Font.BOLD));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
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

        // Focus effect
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
}
