package com.uop.imdb.ui;

import com.uop.imdb.model.Director;
import com.uop.imdb.storage.DataStore;
import com.uop.imdb.ui.dialogs.DirectorDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class DirectorsPanel extends JPanel {

    private final DataStore store;
    private final DefaultTableModel model;
    private final JTable table;

    public DirectorsPanel(DataStore store) {
        this.store = store;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // ==== Table Model (No ID Column) ====
        model = new DefaultTableModel(new Object[]{"Name", "Known Works"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        // ==== Table ====
        table = new JTable(model);
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(UITheme.UI);
        table.setFillsViewportHeight(false); // Prevent white filler space

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
                setBackground(UITheme.ACCENT);
                setForeground(Color.WHITE);
                setFont(UITheme.UI.deriveFont(Font.BOLD));
                return c;
            }
        };
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

        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JTextArea area = new JTextArea(value == null ? "" : value.toString());
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setOpaque(true);
                area.setEditable(false);
                area.setFont(UITheme.UI);
                area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                if (isSelected) {
                    area.setBackground(tbl.getSelectionBackground());
                    area.setForeground(tbl.getSelectionForeground());
                } else {
                    area.setBackground(row % 2 == 0 ? UITheme.BG : UITheme.SURFACE);
                    area.setForeground(Color.WHITE);
                }

                // Auto-adjust row height for wrapped text
                int lineCount = area.getLineCount();
                int prefHeight = area.getFontMetrics(area.getFont()).getHeight() * lineCount + 10;
                if (tbl.getRowHeight(row) != prefHeight) {
                    tbl.setRowHeight(row, prefHeight);
                }

                return area;
            }
        });

        // ==== ScrollPane without white space ====
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UITheme.BG);
        scrollPane.setOpaque(false);

        // ==== Header Panel ====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true);
        headerPanel.setBackground(UITheme.SURFACE);

        JLabel label = new JLabel("🎬 Directors");
        label.setFont(UITheme.TITLE);
        label.setForeground(Color.WHITE);
        headerPanel.add(label, BorderLayout.WEST);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        buttonPanel.setOpaque(false);

        JButton refreshBtn = new JButton("Refresh");
        styleMiniButton(refreshBtn);
        refreshBtn.addActionListener(e -> refresh());
        buttonPanel.add(refreshBtn);

        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // ==== Add Components ====
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

    // Double-click director for details
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                        int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                        String name = (String) model.getValueAt(modelRow, 0); // First column now is Name
                        for (Director d : store.getDirectors()) {
                            if (d.getFullName().equals(name)) {
                                showDirectorDetails(d);
                                break;
                            }
                        }
                    }
                }
            });

        refresh();
    }

    private void showDirectorDetails(Director d) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Director Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(UITheme.BG);
        dialog.setUndecorated(true); // Modern borderless look

        // === Title ===
        JLabel title = new JLabel(d.getFullName(), SwingConstants.CENTER);
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        dialog.add(title, BorderLayout.NORTH);

        // === Content ===
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.BG);
        content.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

//        JLabel idLabel = new JLabel("ID: " + d.getId());
//        idLabel.setFont(UITheme.UI);
//        idLabel.setForeground(Color.LIGHT_GRAY);

        JLabel worksLabel = new JLabel("<html><b>Known Works:</b><br>" +
                String.join("<br>", d.getKnownWorks()) + "</html>");
        worksLabel.setFont(UITheme.UI);
        worksLabel.setForeground(Color.WHITE);
        worksLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

//        content.add(idLabel);
        content.add(worksLabel);

        dialog.add(content, BorderLayout.CENTER);

        // === Footer ===
        JButton closeBtn = new JButton("Close");
        styleMiniButton(closeBtn);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(UITheme.BG);
        footer.add(closeBtn);
        dialog.add(footer, BorderLayout.SOUTH);

        // === Size & Position ===
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void styleMiniButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(UITheme.ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(UITheme.UI.deriveFont(Font.BOLD, 13f));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(UITheme.ACCENT.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(UITheme.ACCENT);
            }
        });
    }

    public void refresh() {
        model.setRowCount(0);
        List<Director> dirs = store.getDirectors();
        for (Director d : dirs) {
            String works = String.join(", ", d.getKnownWorks());
            if (works.isEmpty()) works = "<none>";
            model.addRow(new Object[]{d.getFullName(), works});
        }
    }


}
