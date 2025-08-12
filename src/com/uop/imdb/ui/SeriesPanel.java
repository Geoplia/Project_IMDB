package com.uop.imdb.ui;

import com.uop.imdb.model.Episode;
import com.uop.imdb.model.Season;
import com.uop.imdb.model.Series;
import com.uop.imdb.storage.DataStore;
import com.uop.imdb.ui.dialogs.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class SeriesPanel extends JPanel {
    private final DataStore store;
    private final JTree tree;

    public SeriesPanel(DataStore store) {
        this.store = store;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // --- Toolbar with Add Series Button ---
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(UITheme.BG);
        toolbar.setBorder(new EmptyBorder(5, 10, 5, 10));

        JButton addSeriesBtn = new JButton("+ Add Series");
        styleButton(addSeriesBtn);
        addSeriesBtn.addActionListener(e -> {
            AddSeriesDialog dialog = new AddSeriesDialog((JFrame) SwingUtilities.getWindowAncestor(this), store);
            dialog.setVisible(true);
            refresh();
        });

        toolbar.add(addSeriesBtn);
        add(toolbar, BorderLayout.NORTH);

        // --- Tree setup ---
        tree = new JTree();
        tree.setBackground(UITheme.BG);
        tree.setForeground(Color.WHITE);
        tree.setFont(UITheme.UI);
        tree.setRowHeight(24);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(true);
        tree.setToggleClickCount(1);
        tree.setBorder(new EmptyBorder(5, 10, 5, 10));

        tree.setCellRenderer(new DarkTreeCellRenderer());

        refresh();

        // --- ScrollPane styling ---
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.getViewport().setBackground(UITheme.BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);

        // --- Right-click menu ---
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) { maybeShow(e); }
            public void mouseReleased(java.awt.event.MouseEvent e) { maybeShow(e); }

            private void maybeShow(java.awt.event.MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                if (selRow == -1) return;
                tree.setSelectionRow(selRow);

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) return;

                String nodeText = node.toString();
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (nodeText.contains(" (userRating=")) {
                        JPopupMenu m = new JPopupMenu();
                        stylePopup(m);

                        JMenuItem addSeason = new JMenuItem("Add Season");
                        addSeason.addActionListener(a -> {
                            AddSeasonDialog d = new AddSeasonDialog((JFrame) SwingUtilities.getWindowAncestor(SeriesPanel.this), store);
                            d.setVisible(true);
                            refresh();
                        });

                        JMenuItem addEpisode = new JMenuItem("Add Episode");
                        addEpisode.addActionListener(a -> {
                            AddEpisodeDialog d = new AddEpisodeDialog((JFrame) SwingUtilities.getWindowAncestor(SeriesPanel.this), store);
                            d.setVisible(true);
                            refresh();
                        });

                        JMenuItem addRating = new JMenuItem("Add Rating");
                        addRating.addActionListener(a -> {
                            String title = nodeText.substring(0, nodeText.indexOf(" (user="));
                            store.findSeriesByTitle(title).ifPresent(s -> {
                                AddRatingDialog d = new AddRatingDialog((JFrame) SwingUtilities.getWindowAncestor(SeriesPanel.this), store, s);
                                d.setVisible(true);
                                refresh();
                            });
                        });

                        m.add(addSeason);
                        m.add(addEpisode);
                        m.addSeparator();
                        m.add(addRating);
                        m.show(tree, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    public void refresh() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Series");
        for (Series s : store.getAllSeries()) {
            DefaultMutableTreeNode sNode = new DefaultMutableTreeNode(
                    String.format("%s (user=%.2f)", s.getTitle(), s.getAverageUserRating())
            );
            for (Season season : s.getSeasons()) {
                DefaultMutableTreeNode seasonNode = new DefaultMutableTreeNode("Season " + season.getYear());
                for (Episode ep : season.getEpisodes()) {
                    seasonNode.add(new DefaultMutableTreeNode(ep.toString()));
                }
                sNode.add(seasonNode);
            }
            root.add(sNode);
        }
        tree.setModel(new DefaultTreeModel(root));
        tree.expandRow(0);
    }

    private void expandAllNodes() {
        for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(UITheme.ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(UITheme.UI.deriveFont(Font.BOLD));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void stylePopup(JPopupMenu popup) {
        popup.setBackground(UITheme.SURFACE);
        popup.setBorder(BorderFactory.createLineBorder(UITheme.ACCENT));
    }
}
