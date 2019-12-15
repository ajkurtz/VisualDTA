package com.andykurtz.visualdta;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.StringTokenizer;

public class Utilities {

    final static Logger logger = Logger.getLogger(Utilities.class);

    public static void errorMessage(String msg, Exception ex) {
        if (ex != null) {
            msg += "\n" + ex.toString() + "\n\n";
            StackTraceElement[] ste = ex.getStackTrace();
            StringBuilder msgBuilder = new StringBuilder(msg);
            for (StackTraceElement stackTraceElement : ste) {
                String str = stackTraceElement.toString();
                if (!str.startsWith("java")) {
                    msgBuilder.append(str).append("\n");
                }
            }
            msg = msgBuilder.toString();
        }
        logger.error("msg");
        JOptionPane.showMessageDialog(null, wrapText(msg), "Error", JOptionPane.ERROR_MESSAGE);
    }


    public static void errorFatal(String msg, Exception ex) {
        if (ex != null) {
            msg += "\n" + ex.toString() + "\n\n";
            StackTraceElement[] ste = ex.getStackTrace();
            StringBuilder msgBuilder = new StringBuilder(msg);
            for (StackTraceElement stackTraceElement : ste) {
                String str = stackTraceElement.toString();
                if (!str.startsWith("java")) {
                    msgBuilder.append(str).append("\n");
                }
            }
            msg = msgBuilder.toString();
        }

        logger.error("msg");

        JOptionPane.showMessageDialog(null, wrapText(msg), "Fatal Error", JOptionPane.ERROR_MESSAGE);

        System.exit(1);
    }


    public static String wrapText(String txt) {
        return (wrapText(txt, 40));
    }


    public static String wrapText(String txt, int width) {

        StringTokenizer st = new StringTokenizer(txt, " ");
        StringBuilder buf = new StringBuilder();
        String s;
        int c = 0;

        while (st.hasMoreTokens()) {
            s = st.nextToken();
            buf.append(s);
            buf.append(" ");
            if (s.indexOf('\n') > -1) {
                c = 0;
            } else {
                c += s.length() + 1;
            }
            if (c > width) {
                buf.append("\n");
                c = 0;
            }
        }

        return (buf.toString());
    }

}

