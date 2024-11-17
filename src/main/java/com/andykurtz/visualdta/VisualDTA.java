package com.andykurtz.visualdta;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.logging.log4j.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
        if (null != codingFile) {
            propositionList = Proposition.loadCoding(codingFile);
            logger.debug("coding loaded");
            if (null != propositionList) {
                propositionStats = new PropositionStats(propositionList);
                if (null != statsWindow) {
                    statsWindow.dispose();
                }
                if (null != nodeDetailsWindow) {
                    nodeDetailsWindow.dispose();
                }
                String svg = createSVG(propositionList);
                logger.debug("svg created");
                if (null != svg) {
                    if (null != displayWindow) {
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
        if (null == fd.getFile()) {
            return null;
        }
        return (fd.getDirectory() + fd.getFile());
    }


    private static String createSVG(PropositionList propositionList) {
        String svg;

        int maxSemanticDistance = Proposition.getMaxSemanticDistance(propositionList);
        if (maxSemanticDistance == -1) {
            return null;
        }

        int viewWidth = Proposition.calulateViewWidth(maxSemanticDistance);
        int viewHeight = Proposition.calculateViewHeight(propositionList);

        try {
            svg = Proposition.genHead(viewWidth, viewHeight) +
                    Proposition.genDiagram(propositionList) +
                    Proposition.genFoot();
        } catch (Exception ex) {
            Utilities.errorMessage("There was a problem creating the diagram", ex);
            return null;
        }

        return (svg);
    }


    public static void doQuit() {
        if (null != displayWindow) {
            displayWindow.dispose();
        }
        System.exit(0);
    }


    public static void doInterfaceHelp() {
        if (null == helpBrowser) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            helpBrowser = new Browser("Help", 400, 500, d.width - 405, 0, true, true);
        }
        helpBrowser.history.clear();
        helpBrowser.showPage(frame.getClass().getResource("/html/interfacehelp.html"));
    }


    public static void doFileFormatHelp() {
        if (null == helpBrowser) {
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
        if (null != statsWindow) {
            statsWindow.dispose();
        }
        statsWindow = new StatsWindow(frame, propositionStats, displayWindow, propositionList);
    }


    public static void doCustomStats() {
        if (null != customStatsWindow) {
            customStatsWindow.dispose();
        }
        customStatsWindow = new CustomStatsWindow(frame, propositionStats, displayWindow, propositionList);
    }

    public static void doViewNodeDetails(JSVGCanvas svgCanvas) {
        if (null != nodeDetailsWindow) {
            nodeDetailsWindow.dispose();
        }
        nodeDetailsWindow = new NodeDetailsWindow(frame, propositionList, svgCanvas, displayWindow);
    }

    public static void doExport(File svgFile) {
        String exportFilePath = promptForExportFile();
        if (null != exportFilePath) {
            try {
                Path source = svgFile.toPath();
                Path dest = Paths.get(exportFilePath);
                Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                Utilities.errorMessage("There was an error when exporting the SVG file to " + exportFilePath + ". ", e);
            }
        }
    }

    public static String promptForExportFile() {
        FileDialog fd = new FileDialog(frame, "Select Export File", FileDialog.SAVE);
        fd.setVisible(true);
        if (null == fd.getFile()) {
            return null;
        }
        return (fd.getDirectory() + fd.getFile());
    }

}

