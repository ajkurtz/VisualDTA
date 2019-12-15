package com.andykurtz.visualdta;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class CustomStatsWindow extends JDialog {

    private JComboBox<String> firstAttr;
    private JComboBox<String> secondAttr;
    private PropositionStats propositionStats;
    private PropositionList propositionList;
    private JPanel display;
    private CardLayout cardLayout;
    private Container contentPane;
    private Font headerFont = new Font("Dialog", Font.BOLD, 16);
    private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private JDialog me;

    CustomStatsWindow(JFrame parent, PropositionStats propositionStats, DisplayWindow displayWindow, PropositionList propositionList) {
        super(parent, "Custom Comparison", false);

        this.propositionStats = propositionStats;
        this.propositionList = propositionList;
        me = this;

        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

        JLabel l;

        JPanel select = new JPanel();
        select.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        select.setLayout(new GridLayout(3, 2, 5, 5));

        List<String> attributeArray = propositionList.getAttributeList();

        String[] attributes = new String[attributeArray.size() + 1];

        for (int i = 0; i < attributeArray.size(); ++i) {
            attributes[i] = attributeArray.get(i);
        }

        attributes[attributeArray.size()] = "Move Type";

        l = new JLabel("First Attribute:", JLabel.RIGHT);
        Font normalFont = new Font("Dialog", Font.PLAIN, 14);
        l.setFont(normalFont);
        select.add(l);

        firstAttr = new JComboBox<>(attributes);
        select.add(firstAttr);

        l = new JLabel("Second Attribute:", JLabel.RIGHT);
        l.setFont(normalFont);
        select.add(l);

        secondAttr = new JComboBox<>(attributes);
        select.add(secondAttr);

        l = new JLabel(" ", JLabel.RIGHT);
        l.setFont(normalFont);
        select.add(l);

        JButton showButton = new JButton("Show Statistics");
        showButton.addActionListener(
                e -> {
                    String attr1 = (String) firstAttr.getSelectedItem();
                    String attr2 = (String) secondAttr.getSelectedItem();
                    if (attr1 != null && attr1.equals(attr2)) {
                        JOptionPane.showMessageDialog(null, "Please select two different attributes", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    assert attr1 != null;
                    calcStats(attr1, attr2);
                });

        select.add(showButton);

        display = new JPanel();
        display.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        display.setLayout(new BoxLayout(display, BoxLayout.Y_AXIS));

        //
        // put it on the dialog and size it
        //
        cardLayout = new CardLayout();
        contentPane = getContentPane();
        contentPane.setLayout(cardLayout);
        contentPane.add("Select", select);
        contentPane.add("Display", display);
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

    private void calcStats(String attr1, String attr2) {

        Map<String, Map<String, Integer>> attributeValueHash = propositionStats.attributeValueHash;
        String[] attr1Values = {"T", "P", "B", "M", "E"};
        String[] attr2Values = {"T", "P", "B", "M", "E"};

        if (!attr1.equals("Move Type")) {
            Map<String, Integer> attributeValues = attributeValueHash.get(attr1);
            TreeSet<String> keySet = new TreeSet<>(attributeValues.keySet());
            Iterator<String> iterator = keySet.iterator();
            int i = 0;
            attr1Values = new String[attributeValues.size()];
            while (iterator.hasNext()) {
                attr1Values[i++] = iterator.next();
            }
        }

        if (!attr2.equals("Move Type")) {
            Map<String, Integer> attributeValues = attributeValueHash.get(attr2);
            TreeSet<String> keySet = new TreeSet<>(attributeValues.keySet());
            Iterator<String> iterator = keySet.iterator();
            int i = 0;
            attr2Values = new String[attributeValues.size()];
            while (iterator.hasNext()) {
                attr2Values[i++] = iterator.next();
            }
        }

        JLabel l = new JLabel(attr1 + " by " + attr2);
        l.setFont(headerFont);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        display.add(l);
        display.add(Box.createVerticalStrut(5));

        String[] columnNames = new String[attr2Values.length + 1];
        columnNames[0] = " ";

        System.arraycopy(attr2Values, 0, columnNames, 1, attr2Values.length);

        Object[][] tableData = new Object[attr1Values.length][attr2Values.length + 1];

        int maxAttrWidth = 0;
        for (int i = 0; i < attr1Values.length; ++i) {
            tableData[i][0] = attr1Values[i];
            if (attr1Values[i].length() > maxAttrWidth) {
                maxAttrWidth = attr1Values[i].length();
            }
            for (int j = 1; j < attr2Values.length + 1; ++j) {
                tableData[i][j] = 0;
            }
        }

        Iterator<Proposition> propIterator = propositionList.iterator();
        while (propIterator.hasNext()) {
            Proposition p = propIterator.next();
            Iterator<PropositionData> dataIterator = p.getPropositionDataList().iterator();
            while (dataIterator.hasNext()) {
                PropositionData pd = dataIterator.next();
                Map<String, String> attributeMap = pd.attributeMap;
                String value1 = attr1.equals("Move Type") ? pd.relationType : attributeMap.get(attr1).trim();

                if ((value1 != null) && (!value1.equalsIgnoreCase("NA"))) {

                    String value2 = attr2.equals("Move Type") ? pd.relationType : attributeMap.get(attr2).trim();

                    if ((value2 != null) && (!value2.equalsIgnoreCase("NA"))) {

                        int r = -1;
                        for (int i = 0; i < attr1Values.length; ++i) {
                            if (tableData[i][0].equals(value1)) {
                                r = i;
                                break;
                            }
                        }

                        int c = -1;
                        for (int i = 1; i < (attr2Values.length + 1); ++i) {
                            if (columnNames[i].equals(value2)) {
                                c = i;
                                break;
                            }
                        }

                        if ((r != -1) && (c != -1)) {
                            tableData[r][c] = ((Integer) tableData[r][c]) + 1;
                        }
                    }
                }
            }
        }

        TableSorter sorter = new TableSorter(new MyTableModel(tableData, columnNames));
        JTable table = new JTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        int maxAdvance = table.getFontMetrics(table.getFont()).getMaxAdvance() / 2;
        table.getColumnModel().getColumn(0).setPreferredWidth(maxAttrWidth * maxAdvance);
        int tableWidth = 0;
        for (int i = 0; i < attr2Values.length + 1; ++i) {
            tableWidth += table.getColumnModel().getColumn(i).getPreferredWidth();
        }

        if (tableWidth > screen.width - 100) {
            tableWidth = screen.width - 100;
        }
        int tableHeight = attr1Values.length * table.getRowHeight();
        if (tableHeight > 100) {
            tableHeight = 100;
        }
        table.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));
        table.setGridColor(Color.GRAY);
        table.setShowGrid(true);
        JScrollPane tablePanelScrollPane = new JScrollPane(table);
        tablePanelScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        display.add(tablePanelScrollPane);

        display.add(Box.createVerticalStrut(10));

        JButton newButton = new JButton("New Comparison");
        newButton.addActionListener(
                e -> {
                    display.removeAll();
                    me.pack();
                    cardLayout.next(contentPane);
                });
        display.add(newButton);

        me.pack();
        Rectangle myBounds = me.getBounds();
        if ((myBounds.x + myBounds.width) > screen.width) {
            if (myBounds.width > screen.width) {
                setLocation(0, 0);
            } else {
                setLocation(screen.width - myBounds.width - 10, 0);
            }
        }

        cardLayout.next(contentPane);
    }

    static class MyTableModel extends AbstractTableModel {

        private String[] columnNames;
        private Object[][] data;

        MyTableModel(Object[][] data, String[] columnNames) {
            this.data = data;
            this.columnNames = columnNames;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

    }

}
