package com.andykurtz.visualdta;

import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

public class NodeDetailsWindow extends JDialog {

    private static JPanel detailsPanel = null;
    private static JDialog dialog = null;
    private static List<String> attributeList = null;
    private PropositionList propositionList;
    private JSVGCanvas svgCanvas;

    NodeDetailsWindow(JFrame parent, PropositionList propositionList, JSVGCanvas svgCanvas, DisplayWindow displayWindow) {
        super(parent, "Node Details", false);

        dialog = this;
        this.propositionList = propositionList;
        this.svgCanvas = svgCanvas;
        attributeList = propositionList.getAttributeList();

        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

//        NumberFormat nf = NumberFormat.getInstance();
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(2);

//        Font headerFont = new Font("Dialog", Font.BOLD, 16);
        Font normalFont = new Font("Dialog", Font.PLAIN, 14);

        JPanel contents = new JPanel();
        contents.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

        detailsPanel = new JPanel();
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.setFont(normalFont);
        detailsPanel.setLayout(new SpringLayout());

        JTextArea instructionsTextArea = new JTextArea(Utilities.wrapText("Hover over a node with the mouse pointer and details about that node will be displayed here.", 30));
        detailsPanel.add(instructionsTextArea);
        SpringUtilities.makeCompactGrid(detailsPanel, 1, 1, 0, 0, 5, 0);

        contents.add(new JScrollPane(detailsPanel));

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(contents, BorderLayout.CENTER);
        pack();

        setupEvents();

        Rectangle myBounds = getBounds();

        setSize(myBounds.width, 200);

        myBounds = getBounds();

        Rectangle displayWindowBounds = displayWindow.getBounds();
        if ((screenDimension.width - displayWindowBounds.width) < myBounds.width) {
            setLocation(screenDimension.width - myBounds.width, 0);
        } else {
            setLocation(displayWindowBounds.width + 10, 0);
        }

        setVisible(true);
    }

    public static void showDetails(Proposition p, PropositionData pd) {
        detailsPanel.removeAll();
        int rows = 0;

        detailsPanel.add(new JLabel("Proposition: ", JLabel.TRAILING));
        detailsPanel.add(new JLabel(p.id));
        ++rows;

        //detailsPanel.add(new JLabel("Relation Type: ", JLabel.TRAILING));
        //detailsPanel.add(new JLabel(pd.relationType));
        //++rows;
        //detailsPanel.add(new JLabel("Distance: ", JLabel.TRAILING));
        //detailsPanel.add(new JLabel(String.valueOf(pd.distance)));
        //++rows;
        //detailsPanel.add(new JLabel("Responds To: ", JLabel.TRAILING));
        //detailsPanel.add(new JLabel(pd.respondsTo));
        //++rows;
        //detailsPanel.add(new JLabel("Text: ", JLabel.TRAILING));
        //detailsPanel.add(new JLabel(pd.text));
        //++rows;
        for (String s : attributeList) {
            String value = pd.attributeMap.get(s);
            detailsPanel.add(new JLabel(s + ": ", JLabel.TRAILING));
            detailsPanel.add(new JLabel(value));
            ++rows;
        }

        SpringUtilities.makeCompactGrid(detailsPanel, rows, 2, 0, 0, 5, 0);
        detailsPanel.revalidate();
        detailsPanel.setSize(detailsPanel.getPreferredSize());
        dialog.validate();
    }

    private void setupEvents() {
        org.w3c.dom.Document document = svgCanvas.getSVGDocument();
        Iterator<Proposition> iterator = propositionList.iterator();
        while (iterator.hasNext()) {
            Proposition p = iterator.next();
            PropositionData pd = p.getPropositionDataList().get(0);
            Element element = document.getElementById(pd.svgID);
            EventTarget target = (EventTarget) element;
            target.addEventListener("mouseover", new OnOverAction(p, pd), false);
        }
    }

//    class MyTableModel extends AbstractTableModel {
//
//        private String[] columnNames = null;
//        private Object[][] data = null;
//
//        MyTableModel(Object[][] data, String[] columnNames) {
//            this.data = data;
//            this.columnNames = columnNames;
//        }
//
//        @Override
//        public int getColumnCount() {
//            return columnNames.length;
//        }
//
//        @Override
//        public int getRowCount() {
//            return data.length;
//        }
//
//        @Override
//        public String getColumnName(int col) {
//            return columnNames[col];
//        }
//
//        @Override
//        public Object getValueAt(int row, int col) {
//            return data[row][col];
//        }
//
//        @Override
//        public Class getColumnClass(int c) {
//            return getValueAt(0, c).getClass();
//        }
//
//    }

    public static class OnOverAction implements org.w3c.dom.events.EventListener {

        private Proposition p;
        private PropositionData pd;

        OnOverAction(Proposition p, PropositionData pd) {
            this.p = p;
            this.pd = pd;
        }

        @Override
        public void handleEvent(org.w3c.dom.events.Event evt) {
            showDetails(p, pd);
        }
    }

}
