package com.uop.imdb.ui;

import com.uop.imdb.model.Movie;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class MovieDetailsDialog extends JDialog {

    public MovieDetailsDialog(Window parent, Movie movie) {
        super(parent, "Movie Details", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // transparent corners

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        outerPanel.setBackground(new Color(25, 25, 25, 240));
        outerPanel.setOpaque(true);

        // === Poster ===
        JLabel posterLabel = new JLabel();
        posterLabel.setPreferredSize(new Dimension(220, 330));
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setOpaque(true);
        posterLabel.setBackground(Color.BLACK);

// Load fixed placeholder image from src/images/placeholder.png
        try {
            URL imgUrl = getClass().getResource("/images/placeholder.png");
            if (imgUrl != null) {
                BufferedImage img = ImageIO.read(imgUrl);
                Image scaledImg = img.getScaledInstance(220, 330, Image.SCALE_SMOOTH);
                posterLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                posterLabel.setText("No Image");
                posterLabel.setForeground(Color.WHITE);
            }
        } catch (IOException ex) {
            posterLabel.setText("Image Error");
            posterLabel.setForeground(Color.RED);
        }


        // === Details Panel ===
        JPanel detailsPanel = new JPanel();
        detailsPanel.setBackground(new Color(25, 25, 25, 240));
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(movie.getTitle() + " (" + movie.getYear() + ")");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel directorLabel = makeDetailLabel("Director",
                movie.getDirector() != null ? movie.getDirector().getFullName() : "N/A");

        JLabel genreLabel = makeDetailLabel("Genre",
                movie.getGenre() != null ? movie.getGenre().toString() : "N/A");

        JLabel durationLabel = makeDetailLabel("Duration",
                movie.getDurationMinutes() + " min");

        JLabel imdbLabel = makeDetailLabel("IMDb", String.format("%.1f", movie.getImdbRating()));
        imdbLabel.setForeground(new Color(255, 215, 0));

        JLabel userLabel = makeDetailLabel("User Avg", String.format("%.2f", movie.getAverageUserRating()));
        userLabel.setForeground(new Color(173, 216, 230));

        JTextArea descriptionArea = new JTextArea(movie.getDetails());
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setBackground(new Color(35, 35, 35));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Description",
                0, 0,
                new Font("SansSerif", Font.BOLD, 13),
                Color.LIGHT_GRAY
        ));
        descriptionScroll.setPreferredSize(new Dimension(300, 120));

        detailsPanel.add(titleLabel);
        detailsPanel.add(directorLabel);
        detailsPanel.add(genreLabel);
        detailsPanel.add(durationLabel);
        detailsPanel.add(imdbLabel);
        detailsPanel.add(userLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(descriptionScroll);

        // === Close button ===
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(70, 70, 70));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(closeBtn);

        outerPanel.add(posterLabel, BorderLayout.WEST);
        outerPanel.add(detailsPanel, BorderLayout.CENTER);
        outerPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(outerPanel);
        pack();
        setLocationRelativeTo(parent);

        // Close on ESC
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        setFocusable(true);
    }

    private JLabel makeDetailLabel(String label, String value) {
        JLabel l = new JLabel(label + ": " + value);
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        l.setForeground(Color.LIGHT_GRAY);
        l.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return l;
    }
}

