package com.uop.imdb.ui;

import com.uop.imdb.model.Movie;
import com.uop.imdb.storage.DataStore;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchPanel extends JPanel {
    private final DataStore store;
    private final DefaultTableModel model;
    private final JTextField queryField;
    private final JSpinner minImdbSpinner;
    private final JSpinner minUserSpinner;

    public SearchPanel(DataStore store) {
        this.store = store;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Top Search Controls ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setOpaque(false);

        queryField = new JTextField(30);
        queryField.setFont(UITheme.UI);
        queryField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT, 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        queryField.putClientProperty("JTextField.placeholderText", "Search title, actor, or director...");
        queryField.addActionListener(e -> runSearch());

        minImdbSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.1));
        minUserSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.1));
        styleSpinner(minImdbSpinner);
        styleSpinner(minUserSpinner);

        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn);
        searchBtn.addActionListener(e -> runSearch());

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setForeground(Color.WHITE);
        top.add(searchIcon);

        JLabel minImdbLabel = new JLabel("Min IMDb:");
        minImdbLabel.setForeground(Color.WHITE);
        minImdbLabel.setFont(UITheme.UI);

        JLabel minUserLabel = new JLabel("Min User Avg:");
        minUserLabel.setForeground(Color.WHITE);
        minUserLabel.setFont(UITheme.UI);

        top.add(queryField);
        top.add(minImdbLabel);
        top.add(minImdbSpinner);
        top.add(minUserLabel);
        top.add(minUserSpinner);
        top.add(searchBtn);

        add(top, BorderLayout.NORTH);

        // --- Table Setup ---
        model = new DefaultTableModel(new Object[]{"Title", "Year", "IMDb", "UserAvg", "Director"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 1) return Integer.class;
                if (col == 2 || col == 3) return Double.class;
                return String.class;
            }
        };

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? UITheme.SURFACE : UITheme.BG);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };

       // JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);
        table.setFont(UITheme.UI);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(UITheme.BG); // table cell background
        table.setForeground(Color.WHITE); // table cell text
        table.setSelectionBackground(UITheme.ACCENT.darker());
        table.setSelectionForeground(Color.WHITE);
        // Remove grid lines
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

// Make the empty space under rows match background
        table.setFillsViewportHeight(true);

// Set scrollpane background
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UITheme.BG); // no white gap
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);

// --- Table Header Styling ---
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(UITheme.ACCENT);
        header.setForeground(Color.WHITE);
        header.setFont(UITheme.UI.deriveFont(Font.BOLD));

// Use a header renderer so text is centered and styled consistently
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT.darker()));
                setBackground(UITheme.ACCENT);   // header background
                setForeground(Color.WHITE);      // header text
                setFont(UITheme.UI.deriveFont(Font.BOLD));
                return c;
            }
        };

// Apply this renderer for all header columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // --- Zebra Striping ---
        table.setDefaultRenderer(Object.class, (tbl, value, isSelected, hasFocus, row, col) -> {
            Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? UITheme.SURFACE : UITheme.BG);
                c.setForeground(Color.WHITE);
            }
            return c;
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(UITheme.ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(UITheme.UI.deriveFont(Font.BOLD));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(UITheme.UI);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setBackground(UITheme.SURFACE);
            ((JSpinner.DefaultEditor) editor).getTextField().setForeground(Color.WHITE);
            ((JSpinner.DefaultEditor) editor).getTextField().setBorder(new EmptyBorder(5, 5, 5, 5));
        }
    }

    public void runSearch() {
        model.setRowCount(0);
        String q = queryField.getText();
        String qlow = (q == null) ? "" : q.trim().toLowerCase();
        Double minImdb = ((Number) minImdbSpinner.getValue()).doubleValue();
        Double minUser = ((Number) minUserSpinner.getValue()).doubleValue();
        if (minImdb <= 0.0) minImdb = null;
        if (minUser <= 0.0) minUser = null;

        List<Movie> all = store.getAllMoviesSortedByUserRatingDesc();
        for (Movie m : all) {
            boolean matchesText = qlow.isEmpty() ||
                    (m.getTitle() != null && m.getTitle().toLowerCase().contains(qlow)) ||
                    (m.getDirector() != null && m.getDirector().getFullName().toLowerCase().contains(qlow)) ||
                    (m.getMainActor() != null && m.getMainActor().getFullName().toLowerCase().contains(qlow));
            boolean passesImdb = (minImdb == null) || m.getImdbRating() >= minImdb;
            boolean passesUser = (minUser == null) || m.getAverageUserRating() >= minUser;
            if (matchesText && passesImdb && passesUser) {
                model.addRow(new Object[]{
                        m.getTitle(),
                        m.getYear(),
                        String.format("%.1f", m.getImdbRating()),
                        String.format("%.1f", m.getAverageUserRating()),
                        m.getDirector() == null ? "?" : m.getDirector().getFullName()
                });
            }
        }
    }

    public void externalSearch(String q) {
        queryField.setText(q == null ? "" : q);
        runSearch();
    }
}