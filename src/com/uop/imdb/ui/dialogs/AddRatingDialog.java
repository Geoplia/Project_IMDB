package com.uop.imdb.ui.dialogs;

import com.uop.imdb.model.Movie;
import com.uop.imdb.model.Series;
import com.uop.imdb.model.User;
import com.uop.imdb.storage.DataStore;
import com.uop.imdb.ui.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AddRatingDialog extends JDialog {
    private final DataStore store;

    public AddRatingDialog(JFrame owner, DataStore store, Movie movie) {
        super(owner, "Add Rating to Movie: " + movie.getTitle(), true);
        this.store = store;
        initForMovie(movie);
    }

    public AddRatingDialog(JFrame owner, DataStore store, Series series) {
        super(owner, "Add Rating to Series: " + series.getTitle(), true);
        this.store = store;
        initForSeries(series);
    }

    private void initForMovie(Movie movie) {
        setSize(400, 200);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(UITheme.BG);

        JPanel p = createFormPanel();
        JComboBox<String> userBox = createUserCombo();
        if (userBox == null) return;
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 10, 1));
        styleSpinnerDark(ratingSpinner);

        p.add(createStyledLabel("User:")); p.add(userBox);
        p.add(createStyledLabel("Rating (1-10):")); p.add(ratingSpinner);

        JPanel bottom = createButtonPanel(
                "Add Rating", e -> {
                    String sel = (String) userBox.getSelectedItem();
                    int userId = Integer.parseInt(sel.split(":")[0].trim());
                    int r = (Integer) ratingSpinner.getValue();
                    movie.addUserRating(userId, r);
                    JOptionPane.showMessageDialog(this, "Rating added.");
                    dispose();
                }
        );
        add(p, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void initForSeries(Series series) {
        setSize(400, 200);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(UITheme.BG);

        JPanel p = createFormPanel();
        JComboBox<String> userBox = createUserCombo();
        if (userBox == null) return;
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 10, 1));
        styleSpinnerDark(ratingSpinner);

        p.add(createStyledLabel("User:")); p.add(userBox);
        p.add(createStyledLabel("Rating (1-10):")); p.add(ratingSpinner);

        JPanel bottom = createButtonPanel(
                "Add Rating", e -> {
                    String sel = (String) userBox.getSelectedItem();
                    int userId = Integer.parseInt(sel.split(":")[0].trim());
                    int r = (Integer) ratingSpinner.getValue();
                    series.addUserRating(userId, r);
                    JOptionPane.showMessageDialog(this, "Rating added.");
                    dispose();
                }
        );
        add(p, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // ====== Reusable UI Helpers ======

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(new EmptyBorder(15, 20, 15, 20));
        p.setBackground(UITheme.BG);
        return p;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UITheme.ACCENT);
        label.setFont(UITheme.UI.deriveFont(Font.BOLD, 14f));
        return label;
    }

    private JComboBox<String> createUserCombo() {
        JComboBox<String> userBox = new JComboBox<>();
        List<User> users = store.getUsers();
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users available. Add users to Users.txt first.", "No Users", JOptionPane.WARNING_MESSAGE);
            dispose();
            return null;
        }
        for (User u : users) {
            userBox.addItem(u.getId() + " : " + u.getUsername());
        }
        userBox.setFont(UITheme.UI);
        userBox.setBackground(UITheme.SURFACE);
        userBox.setForeground(Color.WHITE);
        return userBox;
    }

    private JSpinner createStyledSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(8, 1, 10, 1));
        spinner.setFont(UITheme.UI);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setBackground(UITheme.SURFACE);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setForeground(Color.WHITE);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setBorder(new EmptyBorder(5, 5, 5, 5));
        return spinner;
    }

    private JPanel createButtonPanel(String okText, java.awt.event.ActionListener okAction) {
        JButton ok = new JButton(okText);
        styleButton(ok);
        ok.addActionListener(okAction);

        JButton cancel = new JButton("Cancel");
        styleButton(cancel);
        cancel.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UITheme.BG);
        bottom.add(ok);
        bottom.add(cancel);
        return bottom;
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(UITheme.ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(UITheme.UI.deriveFont(Font.BOLD));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSpinnerDark(JSpinner spinner) {
        spinner.setFont(UITheme.UI);
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            JTextField tf = editor.getTextField();
            tf.setOpaque(true);
            tf.setBackground(UITheme.SURFACE);
            tf.setForeground(Color.black);
            tf.setCaretColor(Color.BLACK);
            tf.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        }
    }
}
