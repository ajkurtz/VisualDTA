package com.andykurtz.visualdta;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.net.URL;
import java.util.Stack;

public class Browser extends JDialog {

    JDialog me;
    int backFlag = 1;

    Stack<URL> history = new Stack<>();

    JButton back;
    JButton close;

    JTextPane contents;

    boolean showBackFlag;

    public Browser(String title, int height, int width, int x, int y,
                   boolean sbf, boolean isResizable) {

        setTitle(title);
        setResizable(isResizable);
        showBackFlag = sbf;

        me = this;

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        if (showBackFlag) {
            back = new JButton("Back");
            back.setEnabled(false);
            back.addActionListener(
                    e -> {
                        history.pop();   // remove the current page.
                        try {
                            showPage(history.pop());   // go to the previous page.
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    ex.toString(),
                                    "Error displaying HTML page",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });

            topPanel.add(back);
        }

        close = new JButton("Close");
        close.addActionListener(
                e -> me.setVisible(false));

        topPanel.add(close);

        contents = new JTextPane();
        contents.setEditable(false);
        contents.addHyperlinkListener(
                e -> {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        try {
                            showPage(e.getURL());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    ex.toString(),
                                    "Error displaying HTML page",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

        JPanel w = new JPanel();
        w.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        w.setLayout(new BorderLayout());
        w.add(topPanel, BorderLayout.NORTH);
        w.add(new JScrollPane(contents), BorderLayout.CENTER);

        Container p = getContentPane();
        p.setLayout(new BorderLayout());
        p.add(w, BorderLayout.CENTER);

        pack();
        setSize(width, height);
        setLocation(x, y);
        setVisible(false);
    }

    void showPage(URL url) {

        try {
            contents.setPage(url);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.toString(),
                    "Error retrieving specified URL",
                    JOptionPane.ERROR_MESSAGE);
        }

        if (showBackFlag) {
            history.push(url);
            if (history.size() > backFlag) {
                back.setEnabled(true);
            } else {
                back.setEnabled(false);
            }
        }

        setVisible(true);
        requestFocus();
    }

}
