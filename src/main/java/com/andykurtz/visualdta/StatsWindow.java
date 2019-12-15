package com.andykurtz.visualdta;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class StatsWindow extends JDialog {

    StatsWindow(JFrame parent, PropositionStats propositionStats, DisplayWindow displayWindow, PropositionList propositionList) {
        super(parent, "Basic Statistics", false);

        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

        Container contentPane = getContentPane();
        JLabel l = null;
        JPanel tablePanel = null;

        NumberFormat nf = NumberFormat.getInstance();
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(2);

        Font headerFont = new Font("Dialog", Font.BOLD, 16);
        Font normalFont = new Font("Dialog", Font.PLAIN, 14);

        JPanel contents = new JPanel();
        contents.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

        //
        // layout the proposition counts
        //

        l = new JLabel("Proposition counts ");
        l.setFont(headerFont);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        contents.add(l);

        contents.add(Box.createVerticalStrut(5));

        tablePanel = new JPanel();
        tablePanel.setLayout(new SpringLayout());
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        l = new JLabel("Number of propositions:", JLabel.TRAILING);
        l.setFont(normalFont);
        tablePanel.add(l);

        l = new JLabel(String.valueOf(propositionStats.numberPropositions));
        l.setFont(normalFont);
        tablePanel.add(l);

        l = new JLabel("Avg. semantic distance (all):", JLabel.TRAILING);
        l.setFont(normalFont);
        tablePanel.add(l);

        l = new JLabel(nf.format(propositionStats.avgSemanticDistance));
        l.setFont(normalFont);
        tablePanel.add(l);

        l = new JLabel("Avg. semantic distance (P):", JLabel.TRAILING);
        l.setFont(normalFont);
        tablePanel.add(l);

        l = new JLabel(nf.format(propositionStats.avgSemanticDistanceP));
        l.setFont(normalFont);
        tablePanel.add(l);

        SpringUtilities.makeCompactGrid(tablePanel, 3, 2, 0, 0, 5, 0);

        contents.add(tablePanel);

        contents.add(Box.createVerticalStrut(15));

        //
        // layout the move counts
        //

        l = new JLabel("Move counts ");
        l.setFont(headerFont);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        contents.add(l);

        contents.add(Box.createVerticalStrut(5));

        Integer value;

        tablePanel = new JPanel();
        tablePanel.setLayout(new SpringLayout());
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] moves = {"T", "P", "B", "M", "E"};

        for (String move : moves) {
            l = new JLabel(move + ":", JLabel.TRAILING);
            l.setFont(normalFont);
            tablePanel.add(l);

            value = propositionStats.moveCounts.get(move);
            l = new JLabel(value.toString());
            l.setFont(normalFont);
            tablePanel.add(l);

            Double percent = propositionStats.movePercents.get(move);
            l = new JLabel("(" + pf.format(percent) + ")", JLabel.TRAILING);
            l.setFont(normalFont);
            tablePanel.add(l);

        }

        SpringUtilities.makeCompactGrid(tablePanel, moves.length, 3, 0, 0, 5, 0);

        contents.add(tablePanel);

        contents.add(Box.createVerticalStrut(15));

        //
        // layout the attribute counts
        //

        Map<String, Map<String, Integer>> attributeValueHash = propositionStats.attributeValueHash;
        List<String> attributeArray = propositionList.getAttributeList();
        for (String key : attributeArray) {
            l = new JLabel(key + " counts ");
            l.setFont(headerFont);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            contents.add(l);
            contents.add(Box.createVerticalStrut(5));

            String[] attributeColumnNames = {key, "#", "Percent"};
            int tableWidth = key.length();

            Map<String, Integer> attributeValues = attributeValueHash.get(key);

            double totalValues = 0.0;

            TreeSet<String> valuesKeySet = new TreeSet<>(attributeValues.keySet());
            Iterator<String> valuesIterator = valuesKeySet.iterator();
            while (valuesIterator.hasNext()) {
                key = valuesIterator.next();
                value = attributeValues.get(key);
                totalValues += value.doubleValue();
            }

            Object[][] tableData = new Object[attributeValues.size()][3];
            int numAttributes = 0;

            valuesKeySet = new TreeSet<>(attributeValues.keySet());
            valuesIterator = valuesKeySet.iterator();
            while (valuesIterator.hasNext()) {
                key = valuesIterator.next();
                value = attributeValues.get(key);
                if (key.length() > tableWidth) {
                    tableWidth = key.length();
                }
                tableData[numAttributes][0] = " " + key;
                tableData[numAttributes][1] = value;
                tableData[numAttributes][2] = "   " + pf.format(value.doubleValue() / totalValues);
                ++numAttributes;
            }

            TableSorter sorter = new TableSorter(new MyTableModel(tableData, attributeColumnNames));
            JTable table = new JTable(sorter);
            sorter.setTableHeader(table.getTableHeader());

            int maxAdvance = table.getFontMetrics(table.getFont()).getMaxAdvance() / 2;
            table.getColumnModel().getColumn(2).setPreferredWidth(8 * maxAdvance);
            table.getColumnModel().getColumn(1).setPreferredWidth(6 * maxAdvance);
            table.getColumnModel().getColumn(0).setPreferredWidth(tableWidth * maxAdvance);
            tableWidth = table.getColumnModel().getColumn(0).getPreferredWidth() + table.getColumnModel().getColumn(1).getPreferredWidth() + table.getColumnModel().getColumn(2).getPreferredWidth();

            if (tableWidth > screenDimension.width - 100) {
                tableWidth = screenDimension.width - 100;
            }

            int tableHeight = numAttributes * table.getRowHeight();
            if (tableHeight > 100) {
                tableHeight = 100;
            }

            table.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));
            table.setGridColor(Color.GRAY);
            table.setShowGrid(true);

            JScrollPane tablePanelScrollPane = new JScrollPane(table);
            tablePanelScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

            contents.add(tablePanelScrollPane);

            contents.add(Box.createVerticalStrut(15));
        }

        //
        // layout the thread counts
        //

        /*
         *   l = new JLabel("Thread counts ");
         *   l.setFont(headerFont);
         *   l.setAlignmentX(Component.LEFT_ALIGNMENT);
         *   contents.add(l);
         *   contents.add(Box.createVerticalStrut(5));
         *   tablePanel = new JPanel();
         *   tablePanel.setLayout(new SpringLayout());
         *   tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
         *   l = new JLabel("Number of threads:", JLabel.TRAILING);
         *   l.setFont(normalFont);
         *   tablePanel.add(l);
         *   l = new JLabel(String.valueOf(propositionStats.threadCount));
         *   l.setFont(normalFont);
         *   tablePanel.add(l);
         *   l = new JLabel("Avg. propositions / thread:", JLabel.TRAILING);
         *   l.setFont(normalFont);
         *   tablePanel.add(l);
         *   l = new JLabel(nf.format(propositionStats.avgPropositionsPerThread));
         *   l.setFont(normalFont);
         *   tablePanel.add(l);
         *   SpringUtilities.makeCompactGrid(tablePanel, 2, 2, 0, 0, 5, 0);
         *   contents.add(tablePanel);
         */
        //
        // put it on the dialog and size it
        //

        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(contents), BorderLayout.CENTER);
        pack();

        Rectangle myBounds = getBounds();
        if (myBounds.height > screenDimension.height) {
            myBounds.height = screenDimension.height - 20;
            setSize(myBounds.width, myBounds.height);
            pack();
            myBounds = getBounds();
        }
        Rectangle displayWindowBounds = displayWindow.getBounds();
        if ((screenDimension.width - displayWindowBounds.width) < myBounds.width) {
            setLocation(screenDimension.width - myBounds.width, 0);
        } else {
            setLocation(displayWindowBounds.width + 10, 0);
        }

        setVisible(true);
    }


    static class MyTableModel extends AbstractTableModel {
        private String[] columnNames;
        private Object[][] data;


        MyTableModel(Object[][] data, String[] columnNames) {
            this.data = data;
            this.columnNames = columnNames;
        }


        public int getColumnCount() {
            return columnNames.length;
        }


        public int getRowCount() {
            return data.length;
        }


        public String getColumnName(int col) {
            return columnNames[col];
        }


        public Object getValueAt(int row, int col) {
            return data[row][col];
        }


        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

    }

}

