package com.uop.imdb.ui;

import com.uop.imdb.storage.DataStore;
import com.uop.imdb.ui.dialogs.AddMovieDialog;
import com.uop.imdb.ui.dialogs.AddSeriesDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * MainFrame with top search that updates the SearchPanel live (300ms debounce)
 */
public class MainFrame extends JFrame {
    private final DataStore store;
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);

    // references to panels so we can call externalSearch(...)
    private SearchPanel searchPanel;
    private MoviesPanel moviesPanel;

    public MainFrame(DataStore store) {
        super("CINEMA — Manager");
        this.store = store;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        init();
    }

    private void init() {
        // left nav same as before (omitted for brevity) — keep your previous code
        JPanel left = new JPanel();
        left.setBackground(UITheme.SURFACE);
        left.setPreferredSize(new Dimension(180, 0));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(16,12,16,12));
        JLabel logo = new JLabel("🎬 CINEMA");
        logo.setFont(UITheme.TITLE);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(logo);
        left.add(Box.createRigidArea(new Dimension(0,12)));

        JButton btnMovies = createNavButton("Movies");
        JButton btnSeries = createNavButton("Series");
        JButton btnSearch = createNavButton("Search");
        JButton btnDirectors = createNavButton("Directors");

        left.add(btnMovies);
        left.add(Box.createRigidArea(new Dimension(0,8)));
        left.add(btnSeries);
        left.add(Box.createRigidArea(new Dimension(0,8)));
        left.add(btnSearch);
        left.add(Box.createRigidArea(new Dimension(0,8)));
        left.add(btnDirectors);
        left.add(Box.createVerticalGlue());

        // top bar with search
        JPanel top = new JPanel(new BorderLayout(12,12));
        top.setBackground(UITheme.BG);
        top.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel title = new JLabel("Browse Movies");
        title.setFont(UITheme.TITLE);
        title.setForeground(Color.WHITE);

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(420, 34));
        searchField.setFont(UITheme.UI);
        searchField.setToolTipText("Search title, actor or director");
        searchField.setText(""); // no placeholder here; keep it simple

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        right.setOpaque(false);
        JButton addMovie = new JButton("Add Movie");
        addMovie.addActionListener(e -> new AddMovieDialog(this, store).setVisible(true));
        JButton addSeries = new JButton("Add Series");
        addSeries.addActionListener(e -> new AddSeriesDialog(this, store).setVisible(true));
        styleActionButton(addMovie);
        styleActionButton(addSeries);
        right.add(addMovie); right.add(addSeries);


        top.add(title, BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);

        // content panels
        moviesPanel = new MoviesPanel(store);
        searchPanel = new SearchPanel(store);
        content.add(moviesPanel, "movies");
        content.add(new com.uop.imdb.ui.SeriesPanel(store), "series");
        content.add(searchPanel, "search");
        content.add(new com.uop.imdb.ui.DirectorsPanel(store), "directors");

        // nav button actions
        btnMovies.addActionListener(e -> show("movies"));
        btnSeries.addActionListener(e -> show("series"));
        btnSearch.addActionListener(e -> show("search"));
        btnDirectors.addActionListener(e -> show("directors"));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(left, BorderLayout.WEST);
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(content, BorderLayout.CENTER);

        // Live search wiring (debounced)
        final Timer debounce = new Timer(300, e -> {
            String q = searchField.getText();
            if (q == null) q = "";
            // update search panel and show it
            searchPanel.externalSearch(q);
            show("search");
        });
        debounce.setRepeats(false);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { debounce.restart(); }
            @Override public void removeUpdate(DocumentEvent e) { debounce.restart(); }
            @Override public void changedUpdate(DocumentEvent e) { debounce.restart(); }
        });

        // pressing Enter: immediate search
        searchField.addActionListener(e -> {
            String q = searchField.getText();
            if (q == null) q = "";
            searchPanel.externalSearch(q);
            show("search");
        });
    }

    private JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setBackground(UITheme.SURFACE);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(UITheme.UI.deriveFont(Font.BOLD));
        return b;
    }

    private void styleActionButton(AbstractButton b) {
        b.setBackground(UITheme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
    }

    private void show(String name) { cards.show(content, name); }
}
