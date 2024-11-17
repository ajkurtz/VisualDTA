package com.andykurtz.visualdta;

import org.apache.batik.bridge.UpdateManagerEvent;
import org.apache.batik.bridge.UpdateManagerListener;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;
import org.apache.batik.swing.svg.*;
import org.apache.logging.log4j.*;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DisplayWindow extends JFrame {

    final static Logger logger = LogManager.getLogger(DisplayWindow.class);

    public boolean isReady = false;
    Container contentPane = null;
    JFrame frame;
    JPanel panel = null;
    String svg = null;
    File svgFile = null;
    PropositionList propositionList;
    JSVGCanvas svgCanvas = null;
    JScrollPane scrollPane = null;
    int stepCount = 0;
    boolean textShowing = false;
    JButton textButton = null;
    JButton startStopButton = null;
    JButton previousButton = null;
    JButton nextButton = null;
    JMenuItem zoomInMenu = null;
    JMenuItem zoomOutMenu = null;
    JMenuItem zoomFitMenu = null;
    JMenuItem zoomResetMenu = null;
    JProgressBar progressBar = null;
    JLabel statusLabel = null;
    JMenuItem nodeDetailsMenu = null;
    JMenuItem clearHighlightsMenu = null;
    JPopupMenu popupMenu = null;
    Dimension defaultSize = null;
    boolean enableZoomResetMenu = false;
    boolean enableClearHighlightsMenu = false;
    //    int zoomStartX = -1;
//    int zoomStartY = -1;
    org.w3c.dom.Document document = null;
    private JPanel topPanel = null;

    DisplayWindow(PropositionList propositionList, String svg) {
        frame = this;
        this.propositionList = propositionList;
        this.svg = svg;

        if ((propositionList == null) || (svg == null)) {
            displaySVG(null);
        } else {
            try {
                svgFile = File.createTempFile("VisualDTA", ".svg");
                PrintWriter out = new PrintWriter(new FileWriter(svgFile));
                out.println(svg);
                out.close();
            } catch (Exception ex) {
                Utilities.errorFatal("There was a problem creating the SVG temp file", ex);
            }
            displaySVG(svgFile);
        }
    }

    DisplayWindow(PropositionList propositionList, File svgFile) {
        frame = this;
        this.propositionList = propositionList;
        displaySVG(svgFile);
    }

    private void displaySVG(File svgFile) {
        logger.debug("display SVG");
        logger.debug("SVG file = " + svgFile);

        contentPane = getContentPane();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (propositionList != null) {
            setTitle(propositionList.getFilename());
        } else {
            setTitle("VisualDTA");
        }

        JMenu fileMenu = new JMenu("File");

        JMenuItem loadMenu = new JMenuItem("Load");
        fileMenu.add(loadMenu).setEnabled(true);
        loadMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));

        loadMenu.addActionListener(e -> VisualDTA.doLoad());

        fileMenu.add(new JSeparator());

        JMenuItem exportMenu = new JMenuItem("Export");
        fileMenu.add(exportMenu);
        if (svgFile == null) {
            exportMenu.setEnabled(false);
        } else {
            exportMenu.setEnabled(true);
        }
        exportMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));

        exportMenu.addActionListener(e -> VisualDTA.doExport(svgFile));

        fileMenu.add(new JSeparator());

        JMenuItem quitMenu = new JMenuItem("Quit");
        fileMenu.add(quitMenu).setEnabled(true);
        quitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        quitMenu.addActionListener(e -> VisualDTA.doQuit());

        JMenu viewMenu = new JMenu("View");

        JMenuItem statsMenu = new JMenuItem("Basic Statistics");
        viewMenu.add(statsMenu);
        if (svgFile == null) {
            statsMenu.setEnabled(false);
        } else {
            statsMenu.setEnabled(true);
        }
        statsMenu.addActionListener(e -> VisualDTA.doViewStats());

        JMenuItem customStatsMenu = new JMenuItem("Custom Comparison");
        viewMenu.add(customStatsMenu);
        customStatsMenu.setEnabled(!(svgFile == null));
        customStatsMenu.addActionListener(e -> VisualDTA.doCustomStats());

        nodeDetailsMenu = new JMenuItem("Node Details");
        viewMenu.add(nodeDetailsMenu);
        nodeDetailsMenu.setEnabled(false);
        nodeDetailsMenu.addActionListener(e -> VisualDTA.doViewNodeDetails(svgCanvas));

        viewMenu.add(new JSeparator());

        clearHighlightsMenu = new JMenuItem("Clear Highlights");
        viewMenu.add(clearHighlightsMenu);
        clearHighlightsMenu.setEnabled(false);
        clearHighlightsMenu.addActionListener(e -> clearHighlights());

        JMenu zoomMenu = new JMenu("Zoom");

        zoomInMenu = new JMenuItem("Zoom In");
        //zoomMenu.add(zoomInMenu);
        zoomInMenu.setEnabled(false);
        zoomInMenu.addActionListener(
                e -> svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                        () -> {
                            disableButtons();

                            double scale = 1.25;
                            Dimension svgSize = svgCanvas.getSize();
                            svgSize.width = (int) (svgSize.width * scale);
                            svgSize.height = (int) (svgSize.height * scale);

                            svgCanvas.setPreferredSize(svgSize);
                            svgCanvas.revalidate();
                            scrollPane.revalidate();

                            Element element = document.getElementById("svgDocument");
                            if (element != null) {
                                element.setAttribute("width", String.valueOf(svgSize.width));
                                element.setAttribute("height", String.valueOf(svgSize.height));
                                element.setAttribute("viewBox", "0 0 " + svgSize.width + " " + svgSize.height);
                            }

                            element = document.getElementById("svgScale");
                            if (element != null) {
                                element.setAttribute("transform", "scale(" + scale + ", " + scale + ")");
                            }

                            svgCanvas.flushImageCache();

                            zoomResetMenu.setEnabled(true);
                        }));

        zoomOutMenu = new JMenuItem("Zoom Out");
        //zoomMenu.add(zoomOutMenu);
        zoomOutMenu.setEnabled(false);
        zoomOutMenu.addActionListener(
                e -> svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                        () -> {
                            disableButtons();

                            double scale = 0.75;
                            Dimension svgSize = svgCanvas.getSize();
                            svgSize.width = (int) (svgSize.width * scale);
                            svgSize.height = (int) (svgSize.height * scale);

                            svgCanvas.setPreferredSize(svgSize);
                            svgCanvas.revalidate();
                            scrollPane.revalidate();

                            Element element = document.getElementById("svgDocument");
                            if (element != null) {
                                element.setAttribute("width", String.valueOf(svgSize.width));
                                element.setAttribute("height", String.valueOf(svgSize.height));
                                element.setAttribute("viewBox", "0 0 " + svgSize.width + " " + svgSize.height);
                            }

                            element = document.getElementById("svgScale");
                            if (element != null) {
                                element.setAttribute("transform", "scale(" + scale + ", " + scale + ")");
                            }

                            svgCanvas.flushImageCache();

                            zoomResetMenu.setEnabled(true);
                        }));

        zoomFitMenu = new JMenuItem("Fit To Window");
        zoomMenu.add(zoomFitMenu);
        zoomFitMenu.setEnabled(false);
        zoomFitMenu.addActionListener(
                e -> svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                        () -> {
                            disableButtons();

                            Dimension windowSize = scrollPane.getSize();
                            Dimension svgSize = svgCanvas.getSize();
                            double scaleX = (double) windowSize.width / (double) svgSize.width;
                            double scaleY = (double) windowSize.height / (double) svgSize.height;
                            double scale = Math.min(scaleX, scaleY);

                            svgSize.width = (int) (svgSize.width * scale);
                            svgSize.height = (int) (svgSize.height * scale);

                            svgCanvas.setPreferredSize(svgSize);
                            svgCanvas.revalidate();
                            scrollPane.revalidate();

                            Element element = document.getElementById("svgDocument");
                            if (element != null) {
                                element.setAttribute("width", String.valueOf(svgSize.width));
                                element.setAttribute("height", String.valueOf(svgSize.height));
                                element.setAttribute("viewBox", "0 0 " + svgSize.width + " " + svgSize.height);
                            }

                            element = document.getElementById("svgScale");
                            if (element != null) {
                                element.setAttribute("transform", "scale(" + scale + ", " + scale + ")");
                            }

                            svgCanvas.flushImageCache();

                            zoomResetMenu.setEnabled(true);
                        }));

        zoomMenu.add(new JSeparator());

        zoomResetMenu = new JMenuItem("Reset Zoom");
        zoomMenu.add(zoomResetMenu);
        zoomResetMenu.setEnabled(false);
        zoomResetMenu.addActionListener(
                e -> svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                        () -> {
                            disableButtons();

                            svgCanvas.setPreferredSize(defaultSize);
                            svgCanvas.revalidate();
                            scrollPane.revalidate();

                            Element element = document.getElementById("svgDocument");
                            if (element != null) {
                                element.setAttribute("width", String.valueOf(defaultSize.width));
                                element.setAttribute("height", String.valueOf(defaultSize.height));
                                element.setAttribute("viewBox", "0 0 " + defaultSize.width + " " + defaultSize.height);
                            }

                            element = document.getElementById("svgScale");
                            if (element != null) {
                                element.setAttribute("transform", "scale(1, 1)");
                            }

                            svgCanvas.flushImageCache();

                            zoomResetMenu.setEnabled(false);
                        }));

        JMenu helpMenu = new JMenu("Help");

        JMenuItem interfaceMenu = new JMenuItem("Interface");
        helpMenu.add(interfaceMenu).setEnabled(true);
        interfaceMenu.addActionListener(e -> VisualDTA.doInterfaceHelp());

        JMenuItem formatMenu = new JMenuItem("File Format");
        helpMenu.add(formatMenu).setEnabled(true);
        formatMenu.addActionListener(e -> VisualDTA.doFileFormatHelp());

        helpMenu.add(new JSeparator());

        JMenuItem aboutMenu = new JMenuItem("About");
        helpMenu.add(aboutMenu).setEnabled(true);
        aboutMenu.addActionListener(e -> VisualDTA.doAbout());

        JMenuBar mainMenuBar = new JMenuBar();
        mainMenuBar.add(fileMenu);
        mainMenuBar.add(viewMenu);
        mainMenuBar.add(zoomMenu);
        mainMenuBar.add(helpMenu);

        setJMenuBar(mainMenuBar);

        svgCanvas = new JSVGCanvas(new SVGUserAgentAdapter(), true, false);
        svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
        svgCanvas.setDoubleBufferedRendering(true);
        //svgCanvas.addMouseListener(new MyMouseListener());

        if (svgFile != null) {
            try {
                svgCanvas.setURI(svgFile.toURI().toString());
            } catch (Exception ex) {
                Utilities.errorFatal("There was a problem loading the SVG temp file", ex);
            }
        }

        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            @Override
            public void documentLoadingCancelled(SVGDocumentLoaderEvent e) {

            }

            @Override
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {

            }

            @Override
            public void documentLoadingFailed(SVGDocumentLoaderEvent e) {

            }

            @Override
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {

            }

        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildCancelled(GVTTreeBuilderEvent e) {

            }

            @Override
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                progressBar.setValue(progressBar.getValue() + 1);

            }

            @Override
            public void gvtBuildFailed(GVTTreeBuilderEvent e) {

            }

            @Override
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
                progressBar.setIndeterminate(false);
                progressBar.setValue(progressBar.getValue() + 1);

            }
        });

        svgCanvas.addSVGLoadEventDispatcherListener(new SVGLoadEventDispatcherListener() {

            @Override
            public void svgLoadEventDispatchCancelled(SVGLoadEventDispatcherEvent e) {

            }

            @Override
            public void svgLoadEventDispatchCompleted(SVGLoadEventDispatcherEvent e) {
                progressBar.setValue(progressBar.getValue() + 1);

            }

            @Override
            public void svgLoadEventDispatchFailed(SVGLoadEventDispatcherEvent e) {

            }

            @Override
            public void svgLoadEventDispatchStarted(SVGLoadEventDispatcherEvent e) {
                progressBar.setValue(progressBar.getValue() + 1);

            }

        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererListener() {
            @Override
            public void gvtRenderingCancelled(GVTTreeRendererEvent e) {

            }

            @Override
            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                progressBar.setValue(progressBar.getValue() + 1);
                displayReady();

            }

            @Override
            public void gvtRenderingFailed(GVTTreeRendererEvent e) {

            }

            @Override
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                displayRendering();
                progressBar.setValue(progressBar.getValue() + 1);

            }

            @Override
            public void gvtRenderingStarted(GVTTreeRendererEvent e) {
                progressBar.setValue(progressBar.getValue() + 1);

            }
        });

        svgCanvas.addUpdateManagerListener(new UpdateManagerListener() {

            @Override
            public void managerResumed(UpdateManagerEvent e) {

            }

            @Override
            public void managerStarted(UpdateManagerEvent e) {
                document = svgCanvas.getSVGDocument();

            }

            @Override
            public void managerStopped(UpdateManagerEvent e) {

            }

            @Override
            public void managerSuspended(UpdateManagerEvent e) {

            }

            @Override
            public void updateCompleted(UpdateManagerEvent e) {
                progressBar.setIndeterminate(false);
                enableButtons();

            }

            @Override
            public void updateFailed(UpdateManagerEvent e) {
                progressBar.setIndeterminate(false);
                enableButtons();

                logger.debug(e.toString());
            }

            @Override
            public void updateStarted(UpdateManagerEvent e) {
                progressBar.setIndeterminate(true);
                disableButtons();

            }
        });

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        textButton = new JButton("Show");
        textButton.setEnabled(false);
        textButton.addActionListener(
                e -> svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                        () -> {
                            int loopTo = (stepCount != 0) ? stepCount : propositionList.size();
                            for (int i = 0; i < loopTo; ++i) {
                                Element element = document.getElementById("desc" + i);
                                if (element != null) {
                                    if (textShowing) {
                                        element.setAttribute("style", "opacity:0");
                                    } else {
                                        element.setAttribute("style", "opacity:1");
                                    }
                                }
                            }
                            if (textShowing) {
                                textShowing = false;
                                textButton.setText("Show");
                            } else {
                                textShowing = true;
                                textButton.setText("Hide");
                            }
                            svgCanvas.flushImageCache();
                        }));

        LineBorder lineBorder = new LineBorder(Color.BLACK); // Used for both title borders

        JPanel textBorderPanel = new JPanel();
        textBorderPanel.setLayout(new FlowLayout());
        textBorderPanel.setBorder(new TitledBorder(lineBorder, "Description"));
        textBorderPanel.add(textButton);
        topPanel.add(textBorderPanel);

        startStopButton = new JButton("Start");
        startStopButton.setEnabled(false);
        startStopButton.addActionListener(
                e -> svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                        () -> {
                            if (stepCount == 0) {
                                stepCount = 1;
                                nextButton.setEnabled(true);
                                startStopButton.setText("Stop");
                                stepUpdate();
                            } else {
                                startStopButton.setText("Start");
                                nextButton.setEnabled(false);
                                previousButton.setEnabled(false);
                                stepCount = 0;
                                for (int i = 0; i < propositionList.size(); ++i) {
                                    Element element = document.getElementById("type" + i);
                                    if (element != null) {
                                        element.setAttribute("style", "opacity:1");
                                    }
                                    if (textShowing) {
                                        element = document.getElementById("desc" + i);
                                        if (element != null) {
                                            element.setAttribute("style", "opacity:1");
                                        }
                                    }
                                    int lineCount = 0;
                                    String id = "line" + i + lineCount;
                                    element = document.getElementById(id);
                                    while (element != null) {
                                        element.setAttribute("style", "opacity:1");
                                        ++lineCount;
                                        id = "line" + i + lineCount;
                                        element = document.getElementById(id);
                                    }
                                }
                            }
                            svgCanvas.flushImageCache();
                        }));

        previousButton = new JButton("Previous");
        previousButton.setEnabled(false);
        previousButton.addActionListener(
                e -> svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                        () -> {
                            --stepCount;
                            if (stepCount <= 1) {
                                stepCount = 1;
                                previousButton.setEnabled(false);
                            }
                            nextButton.setEnabled(true);
                            stepUpdate();
                            svgCanvas.flushImageCache();
                        }));

        nextButton = new JButton("Next");
        nextButton.setEnabled(false);
        nextButton.addActionListener(
                e -> svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                        () -> {
                            ++stepCount;
                            if (stepCount >= propositionList.size()) {
                                stepCount = propositionList.size();
                                nextButton.setEnabled(false);
                            }
                            previousButton.setEnabled(true);
                            stepUpdate();
                            svgCanvas.flushImageCache();
                        }));

        JPanel stepBorderPanel = new JPanel();
        stepBorderPanel.setLayout(new FlowLayout());
        stepBorderPanel.setBorder(new TitledBorder(lineBorder, "Progresive display"));
        stepBorderPanel.add(startStopButton);
        stepBorderPanel.add(previousButton);
        stepBorderPanel.add(nextButton);
        topPanel.add(stepBorderPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 20));

        statusLabel = new JLabel("Loading...");
        if (svgFile == null) {
            statusLabel.setText("Select \"File -> Load\" to load a coding file.");
        }
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        progressBar = new JProgressBar(0, 3);
        if (svgFile != null) {
            progressBar.setIndeterminate(true);
        }
        progressBar.setValue(0);
        bottomPanel.add(progressBar, BorderLayout.EAST);

        scrollPane = new JScrollPane(svgCanvas);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);

        frame.pack();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle screen = ge.getMaximumWindowBounds();

        int width;
        int height;

        Dimension preferredSize = frame.getPreferredSize();

        if (propositionList == null) {
            width = preferredSize.width + 10;
            height = 400;
        } else {
            width = Proposition.calulateViewWidth(Proposition.getMaxSemanticDistance(propositionList)) + 20;
            height = Proposition.calculateViewHeight(propositionList) + 100;
        }

        if (width > ((int) screen.getWidth() - 10)) {
            width = (int) screen.getWidth() - 10;
        } else if (width < preferredSize.width) {
            width = preferredSize.width;
        }

        if (height > ((int) screen.getHeight() - 10)) {
            height = (int) screen.getHeight() - 10;
        }

        frame.setSize(width, height);

        frame.setVisible(true);
    }

    @Override
    public void dispose() {
        if (svgFile != null) {
            svgFile.delete();
        }
        super.dispose();
    }

    private void disableButtons() {
        if (zoomResetMenu.isEnabled()) {
            enableZoomResetMenu = true;
            zoomResetMenu.setEnabled(false);
        } else {
            enableZoomResetMenu = false;
        }
        if (clearHighlightsMenu.isEnabled()) {
            enableClearHighlightsMenu = true;
            clearHighlightsMenu.setEnabled(false);
        } else {
            enableClearHighlightsMenu = false;
        }
        textButton.setEnabled(false);
        startStopButton.setEnabled(false);
        zoomInMenu.setEnabled(false);
        zoomOutMenu.setEnabled(false);
        zoomFitMenu.setEnabled(false);
        nodeDetailsMenu.setEnabled(false);
        statusLabel.setText("Please wait...");
    }

    private void enableButtons() {
        if (enableZoomResetMenu) {
            zoomResetMenu.setEnabled(true);
        }
        if (enableClearHighlightsMenu) {
            clearHighlightsMenu.setEnabled(true);
        }
        textButton.setEnabled(true);
        startStopButton.setEnabled(true);
        zoomInMenu.setEnabled(true);
        zoomOutMenu.setEnabled(true);
        zoomFitMenu.setEnabled(true);
        nodeDetailsMenu.setEnabled(true);
        statusLabel.setText("Ready");
    }

    private void displayRendering() {
        disableButtons();
        statusLabel.setText("Rendering...");
        progressBar.setValue(0);
        progressBar.setMaximum(3);
    }

    private void displayReady() {
        enableButtons();
        statusLabel.setText("Ready");
        isReady = true;
        setupEvents();
        progressBar.setValue(0);
        defaultSize = svgCanvas.getSize();
    }

    private void setupEvents() {
        org.w3c.dom.Document svgDocument = svgCanvas.getSVGDocument();
        Iterator<Proposition> iterator = propositionList.iterator();
        while (iterator.hasNext()) {
            Proposition p = iterator.next();
            PropositionData pd = p.getPropositionDataList().get(0);
            Element element = svgDocument.getElementById(pd.svgID);
            EventTarget target = (EventTarget) element;
            target.addEventListener("click", new OnClickMoveAction(pd), false);
        }
    }

    private void stepUpdate() {
        org.w3c.dom.Element element;
        for (int i = 0; i < stepCount; ++i) {
            element = document.getElementById("type" + i);
            if (element != null) {
                element.setAttribute("style", "opacity:1");
            }
            if (textShowing) {
                element = document.getElementById("desc" + i);
                if (element != null) {
                    element.setAttribute("style", "opacity:1");
                }
            }
            int lineCount = 0;
            String id = "line" + i + lineCount;
            element = document.getElementById(id);
            while (element != null) {
                element.setAttribute("style", "opacity:1");
                ++lineCount;
                id = "line" + i + lineCount;
                element = document.getElementById(id);
            }
        }

        for (int i = stepCount; i < propositionList.size(); ++i) {
            element = document.getElementById("type" + i);
            if (element != null) {
                element.setAttribute("style", "opacity:0");
            }
            if (textShowing) {
                element = document.getElementById("desc" + i);
                if (element != null) {
                    element.setAttribute("style", "opacity:0");
                }
            }
            int lineCount = 0;
            String id = "line" + i + lineCount;
            element = document.getElementById(id);
            while (element != null) {
                element.setAttribute("style", "opacity:0");
                ++lineCount;
                id = "line" + i + lineCount;
                element = document.getElementById(id);
            }
        }

        if (VisualDTA.nodeDetailsWindow != null) {
            String key = "type" + (stepCount - 1);
            for (int i = 0; i < propositionList.size(); ++i) {
                Proposition p = propositionList.get(i);
                PropositionData pd = p.getPropositionDataList().get(0);
                if (pd.svgID.equals(key)) {
                    NodeDetailsWindow.showDetails(p, pd);
                    break;
                }
            }
        }
    }

    private void doClick(PropositionData pd, int x, int y) {
        popupMenu = new JPopupMenu();

        y += topPanel.getHeight();
        x += 15;

        JMenuItem popupMenuItem = new JMenuItem("Clear Highlights");
        popupMenu.add(popupMenuItem).setEnabled(true);
        popupMenuItem.addActionListener(
                e -> clearHighlights());

        popupMenu.add(new JSeparator()).setEnabled(true);

        List<String> attributeList = propositionList.getAttributeList();
        for (String attribute : attributeList) {
            String value = pd.attributeMap.get(attribute);
            popupMenuItem = new JMenuItem(attribute + " = " + value);
            popupMenu.add(popupMenuItem).setEnabled(true);
            popupMenuItem.addActionListener(
                    e -> {
                        clearHighlights();
                        highlightAttribute(e.getActionCommand());
                    });
        }

        popupMenu.pack();
        popupMenu.show(frame, x, y);
        popupMenu.update(popupMenu.getGraphics());
    }

    private void clearHighlights() {
        clearHighlightsMenu.setEnabled(false);
        svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                () -> {
                    for (int i = 0; i < propositionList.size(); ++i) {
                        Element element = document.getElementById("highlight" + i);
                        if (element != null) {
                            element.setAttribute("style", "fill:yellow;opacity:0");
                        }
                        element = document.getElementById("plain" + i);
                        if (element != null) {
                            element.setAttribute("style", "opacity:1");
                        }
                    }
                    svgCanvas.flushImageCache();
                });
    }

    private void highlightAttribute(String actionCommand) {
        clearHighlightsMenu.setEnabled(true);

        int separator = actionCommand.indexOf('=');
        final String keyAttribute = actionCommand.substring(0, (separator - 1)).trim();
        final String keyValue = actionCommand.substring((separator + 1)).trim();

        svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(
                () -> {
                    for (int i = 0; i < propositionList.size(); ++i) {
                        Proposition p = propositionList.get(i);
                        PropositionData pd = p.getPropositionDataList().get(0);
                        Map<String, String> am = pd.attributeMap;
                        String value = am.get(keyAttribute);
                        if (keyValue.equals(value)) {
                            Element element = document.getElementById("highlight" + i);
                            if (element != null) {
                                element.setAttribute("style", "fill:yellow;opacity:1");
                            }
                            element = document.getElementById("plain" + i);
                            if (element != null) {
                                element.setAttribute("style", "opacity:0");
                            }
                        }
                    }
                    svgCanvas.flushImageCache();
                });

    }

    public class OnClickMoveAction implements org.w3c.dom.events.EventListener {

        private PropositionData pd;

        OnClickMoveAction(PropositionData pd) {
            this.pd = pd;
        }

        @Override
        public void handleEvent(org.w3c.dom.events.Event event) {
            org.w3c.dom.events.MouseEvent me = (org.w3c.dom.events.MouseEvent) event;
            doClick(pd, me.getClientX(), me.getClientY());
        }
    }

//    class MyMouseListener implements MouseListener {
//
//        @Override
//        public void mouseClicked(java.awt.event.MouseEvent e) {
//        }
//
//        @Override
//        public void mouseEntered(java.awt.event.MouseEvent e) {
//        }
//
//        @Override
//        public void mouseExited(java.awt.event.MouseEvent e) {
//        }
//
//        @Override
//        public void mousePressed(java.awt.event.MouseEvent e) {
//            zoomStartX = e.getX();
//            zoomStartY = e.getY();
//        }
//
//        @Override
//        public void mouseReleased(java.awt.event.MouseEvent e) {
//            int zoomEndX = e.getX();
//            int zoomEndY = e.getY();
//
//            if ((zoomEndX - zoomStartX) != 0
//                    && (zoomEndY - zoomStartY) != 0) {
//
//                disableButtons();
//
//                int dx = zoomEndX - zoomStartX;
//                int dy = zoomEndY - zoomStartY;
//
//                if (dx < 0) {
//                    dx = -dx;
//                    zoomStartX = zoomEndX;
//                }
//                if (dy < 0) {
//                    dy = -dy;
//                    zoomStartY = zoomEndY;
//                }
//
//                Dimension size = svgCanvas.getSize();
//
//                double scaleX = size.width / (float) dx;
//                double scaleY = size.height / (float) dy;
//                double scale = Math.min(scaleX, scaleY);
//
//                Dimension svgSize = defaultSize;
//                svgSize.width = (int) (svgSize.width * scale);
//                svgSize.height = (int) (svgSize.height * scale);
//
//                svgCanvas.setPreferredSize(svgSize);
//                svgCanvas.revalidate();
//                scrollPane.revalidate();
//
//                org.w3c.dom.Element element = document.getElementById("svgDocument");
//                if (element != null) {
//                    element.setAttribute("width", String.valueOf(svgSize.width));
//                    element.setAttribute("height", String.valueOf(svgSize.height));
//                    element.setAttribute("viewBox", "0 0 " + svgSize.width + " " + svgSize.height);
//                }
//
//                element = document.getElementById("svgScale");
//                if (element != null) {
//                    element.setAttribute("transform", "scale(" + scale + ", " + scale + ")");
//                }
//
//                svgCanvas.flushImageCache();
//
//                zoomResetMenu.setEnabled(true);
//            }
//
//        }
//
//    }

}
