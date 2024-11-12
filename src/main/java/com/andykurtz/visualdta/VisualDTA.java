package com.andykurtz.visualdta;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.logging.log4j.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class VisualDTA extends JFrame {

    final static Logger logger = LogManager.getLogger(VisualDTA.class);

    static JFrame frame = null;
    static DisplayWindow displayWindow = null;
    static StatsWindow statsWindow = null;
    static CustomStatsWindow customStatsWindow = null;
    static NodeDetailsWindow nodeDetailsWindow = null;
    static PropositionStats propositionStats = null;
    static PropositionList propositionList = null;
    static private Browser helpBrowser = null;

    VisualDTA() {
        frame = this;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Utilities.errorFatal("There was a problem setting the look and feel", ex);
        }

        displayWindow = new DisplayWindow(null, (File) null);

    }


    public static void doLoad() {
        logger.debug("loading file");

        String codingFile = promptForCodingFile();
        if (codingFile != null) {
            propositionList = Proposition.loadCoding(codingFile);
            logger.debug("coding loaded");
            if (propositionList != null) {
                propositionStats = new PropositionStats(propositionList);
                if (statsWindow != null) {
                    statsWindow.dispose();
                }
                if (nodeDetailsWindow != null) {
                    nodeDetailsWindow.dispose();
                }
                String svg = createSVG(propositionList);
                logger.debug("svg created");
                if (svg != null) {
                    if (displayWindow != null) {
                        displayWindow.dispose();
                    }
                    displayWindow = new DisplayWindow(propositionList, svg);
                }
            }
        }
    }


    public static String promptForCodingFile() {
        FileDialog fd = new FileDialog(frame, "Select Coding File", FileDialog.LOAD);
        fd.setVisible(true);
        if (fd.getFile() == null) {
            return (null);
        }
        return (fd.getDirectory() + fd.getFile());
    }


    private static String createSVG(PropositionList propositionList) {
        String svg;

        int maxSemanticDistance = Proposition.getMaxSemanticDistance(propositionList);
        if (maxSemanticDistance == -1) {
            return (null);
        }

        int viewWidth = Proposition.calulateViewWidth(maxSemanticDistance);
        int viewHeight = Proposition.calculateViewHeight(propositionList);

        try {
            svg = Proposition.genHead(viewWidth, viewHeight) +
                    Proposition.genDiagram(propositionList) +
                    Proposition.genFoot();
        } catch (Exception ex) {
            Utilities.errorMessage("There was a problem creating the diagram", ex);
            return (null);
        }

        return (svg);
    }


    public static void doQuit() {
        if (displayWindow != null) {
            displayWindow.dispose();
        }
        System.exit(0);
    }


    public static void doInterfaceHelp() {
        if (helpBrowser == null) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            helpBrowser = new Browser("Help", 400, 500, d.width - 405, 0, true, true);
        }
        helpBrowser.history.clear();
        helpBrowser.showPage(frame.getClass().getResource("/html/interfacehelp.html"));
    }


    public static void doFileFormatHelp() {
        if (helpBrowser == null) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            helpBrowser = new Browser("Help", 400, 500, d.width - 405, 0, true, true);
        }
        helpBrowser.history.clear();
        helpBrowser.showPage(frame.getClass().getResource("/html/codinghelp.html"));
    }


    public static void doAbout() {
        new AboutWindow(frame);
    }


    public static void doViewStats() {
        if (statsWindow != null) {
            statsWindow.dispose();
        }
        statsWindow = new StatsWindow(frame, propositionStats, displayWindow, propositionList);
    }


    public static void doCustomStats() {
        if (customStatsWindow != null) {
            customStatsWindow.dispose();
        }
        customStatsWindow = new CustomStatsWindow(frame, propositionStats, displayWindow, propositionList);
    }

    public static void doViewNodeDetails(JSVGCanvas svgCanvas) {
        if (nodeDetailsWindow != null) {
            nodeDetailsWindow.dispose();
        }
        nodeDetailsWindow = new NodeDetailsWindow(frame, propositionList, svgCanvas, displayWindow);
    }

}

