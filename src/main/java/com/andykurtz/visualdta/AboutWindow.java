package com.andykurtz.visualdta;

import javax.swing.*;
import java.awt.*;

public class AboutWindow extends JDialog {

    AboutWindow(JFrame parent) {
        super(parent, "About VisualDTA", true);
    }

    private void BuildWindow() {
        Font titleFont = new Font("Dialog", Font.BOLD, 24);
        Font headerFont = new Font("Dialog", Font.BOLD, 16);
        Font normalFont = new Font("Dialog", Font.PLAIN, 14);

        JPanel contents = new JPanel();
        contents.setBackground(Color.WHITE);
        contents.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

        JLabel l = new JLabel("VisualDTA");
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(titleFont);
        contents.add(l);

        contents.add(Box.createVerticalStrut(10));

        l = new JLabel("Version 2.0 (Nov 5, 2016)");
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(normalFont);
        contents.add(l);

        contents.add(Box.createVerticalStrut(10));

        l = new JLabel("Copyright 2004-2016, Andrew J. Kurtz & Susan Herring", JLabel.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(normalFont);
        contents.add(l);

        contents.add(Box.createVerticalStrut(5));

        l = new JLabel("School of Library and Information Science", JLabel.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(normalFont);
        contents.add(l);

        l = new JLabel("Indiana University, Bloomington", JLabel.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(normalFont);
        contents.add(l);

        contents.add(Box.createVerticalStrut(10));

        l = new JLabel("The VisualDTA application may be used for any noncommercial purposes.", JLabel.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(normalFont);
        contents.add(l);

        contents.add(Box.createVerticalStrut(20));

        l = new JLabel("Background", JLabel.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(headerFont);
        contents.add(l);

        contents.add(Box.createVerticalStrut(10));

        JTextArea textArea = new JTextArea(Utilities.wrapText("VisualDTA is an application that can be used to assist Dynamic Topic Analysis (DTA) by providing a way to visualize the structure of the topic flow within a conversation.\n\nThis visualization research is being conducted by Andrew Kurtz and Susan Herring and is based on Dr. Herring's DTA visualization described in her Dynamic Topic Analysis of Synchronous Chat paper.  DTA research is a component of Computer-Mediated Discourse Analysis (CMDA).\n\nDr. Susan Herring is a faculty member with SLIS @ IU and performs research using CMDA techniques and teaches the techniques in various courses.\n\nAndrew J. Kurtz is a doctoral student with SLIS @ IU studying Human-Computer Interaction.", 80));
        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        textArea.setEditable(false);
        textArea.setFont(normalFont);
        contents.add(textArea);

        contents.add(Box.createVerticalStrut(20));

        l = new JLabel("Contact", JLabel.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(headerFont);
        contents.add(l);

        contents.add(Box.createVerticalStrut(10));

        l = new JLabel("VisualDTA Application: Andrew J. Kurtz <andrew@kurtz.ws>", JLabel.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(normalFont);
        contents.add(l);

        contents.add(Box.createVerticalStrut(5));

        l = new JLabel("DTA & CMDA Research: Susan Herring <herring@indiana.edu>", JLabel.LEFT);
        l.setFont(normalFont);
        contents.add(l);

        Container contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(contents, BorderLayout.CENTER);
        setResizable(false);
        pack();

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle r = getBounds();
        setLocation((d.width / 2) - (r.width / 2), (d.height / 2) - (r.height / 2));

        setVisible(true);
    }

}
