package com.uop.imdb.ui;

import com.uop.imdb.model.Movie;
import com.uop.imdb.storage.DataStore;
import com.uop.imdb.ui.dialogs.AddRatingDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.table.JTableHeader;


public class MoviesPanel extends JPanel {
    private final DataStore store;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JPanel gallery;
    private final JScrollPane tableScroll;
    private final JScrollPane galleryScroll;
    private boolean showingTable = true;

    public MoviesPanel(DataStore store) {
        this.store = store;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);





        // ==== Table ====
        tableModel = new DefaultTableModel(new Object[]{"Title", "Year", "IMDb", "UserAvg", "Director"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                switch (col) {
                    case 1:
                        return Integer.class; //styleSpinnerDark Year
                    case 2:
                    case 3:
                        return Double.class; // IMDb, UserAvg
                    default:
                        return String.class; // Title, Director
                }
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(UITheme.UI);
        table.setAutoCreateRowSorter(true);

// --- Modern Header Styling ---
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT.darker()));
                setBackground(UITheme.ACCENT);   // Background color
                setForeground(Color.WHITE);      // Text color
                setFont(UITheme.UI.deriveFont(Font.BOLD));
                return c;
            }
        };

// Apply the styled header renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Zebra stripes
        table.setDefaultRenderer(Object.class, (tbl, value, isSelected, hasFocus, row, col) -> {
            Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? UITheme.BG : UITheme.SURFACE);
                c.setForeground(Color.WHITE);
            }
            return c;
        });

        tableScroll = new JScrollPane(table);
        tableScroll.getViewport().setBackground(UITheme.BG);
        tableScroll.getViewport().setBackground(UITheme.BG);
        table.setBackground(UITheme.BG);

// Update zebra renderer to fill blank space nicely
        table.setDefaultRenderer(Object.class, (tbl, value, isSelected, hasFocus, row, col) -> {
            Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? UITheme.BG : UITheme.SURFACE);
                c.setForeground(Color.WHITE);
            }
            return c;
        });

        // ==== Gallery ====
        gallery = new JPanel(new WrapLayout(FlowLayout.LEFT, 18, 18));
        gallery.setBackground(UITheme.BG);
        galleryScroll = new JScrollPane(gallery);
        galleryScroll.getViewport().setBackground(UITheme.BG);

// ==== Header Panel (Movies + Buttons) ====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true);
        headerPanel.setBackground(UITheme.SURFACE); // Solid background for contrast

// Movies label
        JLabel label = new JLabel("🎬 Movies");
        label.setFont(UITheme.TITLE);
        label.setForeground(Color.WHITE);
        headerPanel.add(label, BorderLayout.WEST);

// Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        buttonPanel.setOpaque(false);

// Toggle button
        JButton toggle = new JButton("Switch View");
        styleMiniButton(toggle);
        toggle.addActionListener(e -> toggleView());
        buttonPanel.add(toggle);
        // Rate Movie button
        JButton rateBtn = new JButton("Rate Movie");
        styleMiniButton(rateBtn);
        rateBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a movie first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String movieTitle = tableModel.getValueAt(modelRow, 0).toString();
            //String movieTitle = table.getValueAt(table.convertRowIndexToModel(selectedRow), 0).toString();
            store.findMovieByTitle(movieTitle).ifPresent(m -> {
                new AddRatingDialog((JFrame) SwingUtilities.getWindowAncestor(MoviesPanel.this), store, m).setVisible(true);
                refresh();
            });
        });
        buttonPanel.add(rateBtn);

// Refresh button
        JButton refreshBtn = new JButton("Refresh");
        styleMiniButton(refreshBtn);
        refreshBtn.addActionListener(e -> refresh());
        buttonPanel.add(refreshBtn);

        headerPanel.add(buttonPanel, BorderLayout.EAST);

// Add components
        add(headerPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        refresh();
    }

    private void styleMiniButton(JButton b) {
        b.setFont(UITheme.UI);
        b.setBackground(UITheme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }

    private void toggleView() {
        remove(showingTable ? tableScroll : galleryScroll);
        add(showingTable ? galleryScroll : tableScroll, BorderLayout.CENTER);
        showingTable = !showingTable;
        revalidate();
        repaint();
    }

    public void refresh() {
        refreshTable();
        refreshGallery();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Movie m : store.getMovies()) {
            tableModel.addRow(new Object[]{
                    m.getTitle(),
                    m.getYear(),
                    String.format("%.1f", m.getImdbRating()),  // Formats IMDb rating to 1 decimal
                    String.format("%.1f", m.getAverageUserRating()),  // Formats user rating to 1 decimal
                    m.getDirector() != null ? m.getDirector().getFullName() : ""
            });
        }
    }

    private void refreshGallery() {
        gallery.removeAll();
        for (Movie m : store.getMovies()) {
            JPanel card = createMovieCard(m);
            gallery.add(card);
        }
        gallery.revalidate();
        gallery.repaint();
    }

    private Image loadMovieImage(String title) {
        String imgName = title.toLowerCase().replaceAll("\\s+", "_") + ".jpg";
        try {
            // Try resource first
            var imgStream = getClass().getResourceAsStream("/images/" + imgName);
            if (imgStream != null) {
                return ImageIO.read(imgStream).getScaledInstance(180, 200, Image.SCALE_SMOOTH);
            }
            // Fallback to placeholder
            var placeholderStream = getClass().getResourceAsStream("/images/placeholder.png");
            if (placeholderStream != null) {
                return ImageIO.read(placeholderStream).getScaledInstance(180, 200, Image.SCALE_SMOOTH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Ultimate fallback
        BufferedImage fallback = new BufferedImage(180, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = fallback.createGraphics();
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, 180, 200);
        g2.setColor(Color.WHITE);
        g2.drawString("No Image", 50, 100);
        g2.dispose();
        return fallback;
    };

    private JPanel createMovieCard(Movie m) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Rounded background
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(200, 320));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Title
        JLabel title = new JLabel("<html><div style='text-align:center;'><b>" + m.getTitle() + "</b></div></html>", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.UI);

        // Poster placeholder
        JLabel poster = new JLabel("Loading...", SwingConstants.CENTER);
        poster.setPreferredSize(new Dimension(180, 240));
        poster.setOpaque(true);
        poster.setBackground(Color.BLACK);
        poster.setForeground(Color.GRAY);
        poster.setFont(UITheme.UI);

        // Load image in background
        SwingWorker<ImageIcon, Void> imgLoader = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                Image img = loadMovieImage(m.getTitle());
                return new ImageIcon(img);
            }

            @Override
            protected void done() {
                try {
                    poster.setText("");
                    poster.setIcon(get());
                } catch (Exception e) {
                    poster.setText("[No Image]");
                }
            }
        };
        imgLoader.execute();

        // Ratings panel
        JPanel ratingPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        ratingPanel.setOpaque(false);

        JLabel imdb = new JLabel("\u2605 " + String.format("%.1f", m.getImdbRating()), SwingConstants.CENTER);
        imdb.setForeground(new Color(255, 215, 0));

        JLabel user = new JLabel("\uD83D\uDC64 " + String.format("%.1f", m.getAverageUserRating()), SwingConstants.CENTER);
        user.setForeground(new Color(173, 216, 230));

        ratingPanel.add(imdb);
        ratingPanel.add(user);

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(UITheme.ACCENT, 2, true));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) { // double click for full details
                    new MovieDetailsDialog(
                            SwingUtilities.getWindowAncestor(MoviesPanel.this),
                            m
                    ).setVisible(true);
                }
            }
        });

        // Assemble card
        card.add(title, BorderLayout.NORTH);
        card.add(poster, BorderLayout.CENTER);
        card.add(ratingPanel, BorderLayout.SOUTH);

        return card;
    }



}


