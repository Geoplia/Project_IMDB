package com.uop.imdb.ui.dialogs;

import com.uop.imdb.model.Director;

import javax.swing.*;
import java.awt.*;

// Read-only dialog showing director information

public class DirectorDetailsDialog extends JDialog {
    public DirectorDetailsDialog(JFrame owner, Director director) {
        super(owner, "Director: " + director.getFullName(), true);
        init(director);
    }

    private void init(Director d) {
        setSize(420, 320);
        setLocationRelativeTo(getOwner());
        JPanel p = new JPanel(new BorderLayout(8,8));

        // Build formatted director info
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(d.getFullName()).append("\n\n");
        sb.append("Known Works (IMDb > 7.5):\n");
        if (d.getKnownWorks().isEmpty()) {
            sb.append("  (none)\n");
        } else {
            for (String w : d.getKnownWorks()) sb.append(" - ").append(w).append("\n");
        }
        ta.setText(sb.toString());
        ta.setCaretPosition(0);

        p.add(new JScrollPane(ta), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        bottom.add(close);
        p.add(bottom, BorderLayout.SOUTH);

        setContentPane(p);
    }
}

