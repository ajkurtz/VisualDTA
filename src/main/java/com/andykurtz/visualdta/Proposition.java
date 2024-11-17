package com.andykurtz.visualdta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Proposition {

//    private final static int viewWidth = 600;
//    private final static int viewHeight = 800;

    private final static int xPad = 20;
    private final static int yPad = 15;

    private final static int xMargin = 25;
    private final static int yMargin = 25;

    private final static int charHeight = 10;
    private final static int charWidth = 10;

    //    private final static int xOffset = 4;
    private final static int yOffset = -6;

    private static int xPropositionIdPad = 30;

    public String id;
    public int x = 0;
    public int y = 0;
    public int semanticDistance = 0;
    private PropositionDataList propositionDataList;

//    Proposition(String id, PropositionDataList propositionDataList) {
//        this.propositionDataList = null;
//        this.id = id;
//        this.propositionDataList = propositionDataList;
//    }

    Proposition(String id, PropositionData propositionData) {
        this.propositionDataList = null;
        this.id = id;
        this.propositionDataList = new PropositionDataList();
        this.propositionDataList.add(propositionData);
    }

    private static Proposition findProposition(PropositionList propositionList, String id) {

        if (propositionList == null) {
            return null;
        }

        for (int i = 0; i < propositionList.size(); ++i) {
            Proposition p = propositionList.get(i);
            if (p.id.equalsIgnoreCase(id)) {
                return (p);
            }
        }

        return null;
    }

    private static String stripNonPrint(String str) {
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; ++i) {
            if ((c[i] < 0x20) || (c[i] > 0x7E)) {
                c[i] = ' ';
            }
        }
        return (new String(c));
    }

    private static String replaceHtmlEntities(String str) {
        String replaceAll = str.replaceAll("&", "&amp;");
        replaceAll = replaceAll.replaceAll("\"", "&quot;");
        replaceAll = replaceAll.replaceAll("<", "&lt;");
        replaceAll = replaceAll.replaceAll(">", "&gt;");
        return replaceAll;
    }

    private static String[] splitFields(String record) {
        StringTokenizer st = new StringTokenizer(record.trim(), "\t");
        String[] fields = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            fields[i] = st.nextToken().trim();
            if (fields[i].charAt(0) == '"' || fields[i].charAt(0) == '\'') {
                fields[i] = fields[i].substring(1);
            }
            int j = fields[i].length() - 1;
            if (fields[i].charAt(j) == '"' || fields[i].charAt(j) == '\'') {
                fields[i] = fields[i].substring(0, j);
            }
            ++i;
        }
        return (fields);
    }

    public static int getMaxSemanticDistance(PropositionList propositionList) {
        int maxSemanticDistance = 0;

        for (int i = 0; i < propositionList.size(); ++i) {
            Proposition p = propositionList.get(i);

            boolean first = true;
            int size = p.getPropositionDataList().size();

            for (int j = 0; j < size; ++j) {
                PropositionData pd = p.getPropositionDataList().get(j);
                if (pd.respondsTo.equalsIgnoreCase("NA")) {
                    if (first) {
                        if (pd.relationType.equals("B")) {
                            p.semanticDistance = 4;
                        } else {
                            p.semanticDistance = 0;
                        }
                    }
                } else {
                    if (pd.respondsTo.equals("0")) {
                        if (first) {
                            p.semanticDistance = 0;
                        }
                    } else {
                        if (first) {
                            Proposition r = findProposition(propositionList, pd.respondsTo);
                            if (r == null) {
                                Utilities.errorMessage("There was an error in the coding file.   A Responds to value was given, but it was not found in a previous proposition.  Current proposition = '" + p.id + "', Responds to = '" + pd.respondsTo + "'.", null);
                                return (-1);
                            }
                            p.semanticDistance = r.semanticDistance + pd.distance;
                        }
                    }
                }

                first = false;

            }

            if (p.semanticDistance > maxSemanticDistance) {
                maxSemanticDistance = p.semanticDistance;
            }

        }

        return (maxSemanticDistance);
    }

    public static String genHead(int viewWidth, int viewHeight) {

        return ("<?xml version=\"1.0\"?>\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + viewWidth + "\" height=\"" + viewHeight + "\" viewBox=\"0 0 " + viewWidth + " " + viewHeight + "\" preserveAspectRatio=\"xMidYMin meet\">\n" +
                "	<defs>\n" +
                "	<style type=\"text/css\">\n" +
                "	<![CDATA[\n" +
                "		text.label {\n" +
                "			fill: black;\n" +
                "			stroke: none;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 14;\n" +
                "			font-weight: bold;\n" +
                "		}\n" +
                "		text.button {\n" +
                "			fill: black;\n" +
                "			stroke: none;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 11;\n" +
                "			font-weight: normal;\n" +
                "			text-anchor: middle;\n" +
                "		}\n" +
                "		text.normal {\n" +
                "			fill: black;\n" +
                "			stroke: none;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 14;\n" +
                "			font-weight: normal;\n" +
                "		}\n" +
                "		text.description {\n" +
                "			fill: black;\n" +
                "			stroke: none;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 13;\n" +
                "			letter-spacing: 1;\n" +
                "			font-weight: normal;\n" +
                "		}\n" +
                "		text.description-fill {\n" +
                "			fill: white;\n" +
                "			stroke: white;\n" +
                "			stroke-width: 4;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 13;\n" +
                "			letter-spacing: 1;\n" +
                "			font-weight: normal;\n" +
                "		}\n" +
                "		text.B {\n" +
                "			fill: black;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 14;\n" +
                "			font-weight: bold;\n" +
                "		}\n" +
                "		text.E {\n" +
                "			fill: purple;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 14;\n" +
                "			font-weight: bold;\n" +
                "		}\n" +
                "		text.M {\n" +
                "			fill: green;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 14;\n" +
                "			font-weight: bold;\n" +
                "		}\n" +
                "		text.P {\n" +
                "			fill: red;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 14;\n" +
                "			font-weight: bold;\n" +
                "		}\n" +
                "		text.T {\n" +
                "			fill: blue;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 14;\n" +
                "			font-weight: bold;\n" +
                "		}\n" +
                "		text.F {\n" +
                "			fill: white;\n" +
                "			stroke: white;\n" +
                "			stroke-width: 4;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 14;\n" +
                "			font-weight: bold;\n" +
                "		}\n" +
                "		text.H {\n" +
                "			opacity: 0;\n" +
                "			fill: yellow;\n" +
                "			stroke: yellow;\n" +
                "			stroke-width: 6;\n" +
                "			font-family: sans-serif;\n" +
                "			font-size: 14;\n" +
                "			font-weight: bold;\n" +
                "		}\n" +
                "		line.label {\n" +
                "			fill: none;\n" +
                "			stroke: black;\n" +
                "			stroke-width: 1;\n" +
                "		}	\n" +
                "		line.solid {\n" +
                "			fill: none;\n" +
                "			stroke: black;\n" +
                "			stroke-width: 1.5;\n" +
                "		}\n" +
                "		line.dotted {\n" +
                "			fill: none;\n" +
                "			stroke: black;\n" +
                "			stroke-width: 1.5;\n" +
                "			stroke-dasharray:5 5;\n" +
                "		}\n" +
                "		circle.H {\n" +
                "			opacity: 0;\n" +
                "			fill: yellow;\n" +
                "			stroke: black;\n" +
                "			stroke-width: 1;\n" +
                "		}\n" +
                "	]]>\n" +
                "	</style>\n" +
                "	</defs>\n" +
                "	<g id=\"svgScale\" transform=\"scale(1, 1)\">\n");
    }

    private static String normalLine(int x1, int y1, int x2, int y2, String style, String id) {
        if (id != null) {
            return ("<line x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\" class=\"" + style + "\" id=\"" + id + "\"/>\n");
        } else {
            return ("<line x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\" class=\"" + style + "\"/>\n");
        }
    }

    private static String normalText(int x, int y) {
        return ("<text x=\"" + x + "\" y=\"" + y + "\" class=\"label\">" + "Semantic Distance" + "</text>\n");
    }

    private static String rightText(int x, int y, String text, String id) {
        if (id != null) {
            return ("<text x=\"" + x + "\" y=\"" + y + "\" class=\"normal\" id=\"" + id + "\" text-anchor=\"end\">" + text + "</text>\n");
        } else {
            return ("<text x=\"" + x + "\" y=\"" + y + "\" class=\"normal\" text-anchor=\"end\">" + text + "</text>\n");
        }
    }

    private static String middleText(int x, int y, String text) {
        return ("<text x=\"" + x + "\" y=\"" + y + "\" class=\"normal\" text-anchor=\"middle\">" + text + "</text>\n");
    }

    private static String descriptionText(int x, int y, String text, String id) {
        return ("<g id=\"" + id + "\" style=\"opacity:0\">\n" +
                "<text x=\"" + x + "\" y=\"" + y + "\" class=\"description-fill\">" + text + "</text>\n" +
                "<text x=\"" + x + "\" y=\"" + y + "\" class=\"description\">" + text + "</text>\n" +
                "</g>\n");
    }

    private static String verticalPropositionNumberText(int x, int y) {
        return ("<text x=\"" + x + "\" y=\"" + y + "\" class=\"label\" transform=\"rotate(-90, " + x + ", " + y + ")\" text-anchor=\"end\">Proposition Number</text>\n");
    }

    private static String filledText(int x, int y, String style, String text, String svgID, String svgHighlightId, String plainId) {
        return ("<g id=\"" + svgID + "\" style=\"cursor:pointer\">\n" +
                "<circle id=\"" + svgHighlightId + "\" cx=\"" + (x - 1) + "\" cy=\"" + (y - (charHeight / 2)) + "\" r=\"" + charWidth + "\" class=\"H\"/>\n" +
                "<text id=\"" + plainId + "\" x=\"" + x + "\" y=\"" + y + "\" class=\"F\" text-anchor=\"middle\">" + text + "</text>\n" +
                "<text id=\"" + svgHighlightId + "\" x=\"" + x + "\" y=\"" + y + "\" class=\"H\" text-anchor=\"middle\">" + text + "</text>\n" +
                "<text x=\"" + x + "\" y=\"" + y + "\" class=\"" + style + "\" text-anchor=\"middle\">" + text + "</text>\n" +
                "</g>\n");
    }

    public static String genDiagram(PropositionList propositionList) {
        int x;
        int y;

        int maxSemanticDistance = 0;

        StringBuilder buffer = new StringBuilder();

        x = xMargin;
        y = yMargin + 40;

        buffer.append(verticalPropositionNumberText(x, y));

        x = xMargin + xPad + xPropositionIdPad + xPad;
        y = yMargin;

        buffer.append(normalText(x, y));

        y += 40;

        for (int i = 0; i < propositionList.size(); ++i) {
            Proposition p = propositionList.get(i);

            buffer.append(rightText(x - xPad, y, p.id, ("label" + i)));

            boolean first = true;
            int size = p.getPropositionDataList().size();

            // draw the lines and the proposition labels
            for (int j = 0; j < size; ++j) {
                PropositionData pd = p.getPropositionDataList().get(j);

                String style;
                if (pd.dotted) {
                    style = "dotted";
                } else {
                    style = "solid";
                }

                int x2;
                if (pd.respondsTo.equalsIgnoreCase("NA")) {
                    if (first) {
                        if (pd.relationType.equals("B")) {
                            p.semanticDistance = 4;
                        } else {
                            p.semanticDistance = 0;
                        }
                        x2 = x + ((charWidth + xPad) * pd.distance);
                    } else {
                        x2 = p.x;
                    }
                } else {
                    if (pd.respondsTo.equals("0")) {
                        // special case when reply to is 0 only used for first proposition
                        if (first) {
                            p.semanticDistance = 0;
                            x2 = x + ((charWidth + xPad) * pd.distance);
                        } else {
                            x2 = p.x;
                        }
                        buffer.append(normalLine(xMargin + xPropositionIdPad, yMargin + 10, x2, y + yOffset, style, ("line" + i + j)));
                    } else {
                        Proposition r = findProposition(propositionList, pd.respondsTo);
                        if (r == null) {
                            Utilities.errorMessage("There was an error in the coding file.   A Responds to value was given, but it was not found in a previous proposition.  Current proposition = '" + p.id + "', Responds to = '" + pd.respondsTo + "'.", null);
                            return null;
                        }
                        if (first) {
                            x2 = r.x + ((charWidth + xPad) * pd.distance);
                            p.semanticDistance = r.semanticDistance + pd.distance;
                        } else {
                            x2 = p.x;
                        }
                        if (!pd.relationType.equals("B")) {
                            buffer.append(normalLine((r.x - 1), r.y + yOffset, (x2 - 1), y + yOffset, style, ("line" + i + j)));
                        }
                    }
                }

                if (first) {
                    p.x = x2;
                    p.y = y;
                }

                first = false;

            }

            if (p.semanticDistance > maxSemanticDistance) {
                maxSemanticDistance = p.semanticDistance;
            }

            y += charHeight + yPad;
        }

        x = xMargin + xPad + xPropositionIdPad + xPad;
        y = yMargin + 20;

        // draw the semantic distance labels
        for (int i = 1; i <= maxSemanticDistance + 1; ++i) {
            buffer.append(middleText(x, y, String.valueOf(i)));
            x += charWidth + xPad;
        }

        // draw the relation types and description text
        for (int i = 0; i < propositionList.size(); ++i) {
            Proposition p = propositionList.get(i);
            PropositionData pd = p.getPropositionDataList().get(0);
            pd.svgID = "type" + i;
            pd.svgHighlight = "highlight" + i;
            pd.plain = "plain" + i;
            buffer.append(filledText(p.x, p.y, pd.relationType, pd.relationType, pd.svgID, pd.svgHighlight, pd.plain));
            if (!pd.text.equalsIgnoreCase("NA")) {
                buffer.append(descriptionText((p.x + 20), p.y, pd.text, ("desc" + i)));
            }
        }

        return (buffer.toString());
    }

    public static String genFoot() {
        return ("</g></svg>\n");
    }

    public static int calulateViewWidth(int maxSemanticDistance) {
        return (200 + xPropositionIdPad + ((maxSemanticDistance + 1) * (charWidth + xPad)) + (xMargin * 2));
    }

    public static int calculateViewHeight(PropositionList propositionList) {
        return ((propositionList.size() * (charHeight + yPad)) + (yMargin * 2) + 100);
    }

    public static PropositionList loadCoding(String codingFileName) {

        //
        // Required fields
        //			Proposition
        //			Responds To
        //			Relation Type
        //			Distance
        //
        // Known fields
        //			Description (or Text)
        //			Dotted Line (or Dotted Line?)
        //
        // Example attribute fields
        //			Speaker
        //			Gender
        //			Role
        //			Native Language
        //			Nationality
        //
        try {
            BufferedReader in = new BufferedReader(new FileReader(codingFileName));

            String line = in.readLine();
            String[] fields = splitFields(line);

            boolean foundProposition = false;
            boolean foundRespondsTo = false;
            boolean foundRelationType = false;
            boolean foundDistance = false;
            boolean foundText = false;
            boolean foundDottedLine = false;

            List<String> attributeList = new ArrayList<>();

            Map<String, Integer> fieldMap = new HashMap<>();

            //
            // We care about the case of the fields, but don't want the user to have to.
            // So, fix the case on the known fields.
            //
            for (int i = 0; i < fields.length; ++i) {
                if (fields[i].equalsIgnoreCase("Proposition")) {
                    foundProposition = true;
                    fields[i] = "Proposition";
                } else if (fields[i].equalsIgnoreCase("Responds To")) {
                    foundRespondsTo = true;
                    fields[i] = "Responds To";
                } else if (fields[i].equalsIgnoreCase("Relation Type")) {
                    foundRelationType = true;
                    fields[i] = "Relation Type";
                } else if (fields[i].equalsIgnoreCase("Distance")) {
                    foundDistance = true;
                    fields[i] = "Distance";
                } else if (fields[i].equalsIgnoreCase("Text") || fields[i].equalsIgnoreCase("Description")) {
                    fields[i] = "Description";
                    foundText = true;
                } else if (fields[i].equalsIgnoreCase("Dotted Line") || fields[i].equalsIgnoreCase("Dotted Line?")) {
                    fields[i] = "Dotted Line";
                    foundDottedLine = true;
                } else {
                    attributeList.add(fields[i]);
                }
                fieldMap.put(fields[i], i);
            }

            if (!foundProposition || !foundRespondsTo || !foundRelationType || !foundDistance) {
                Utilities.errorMessage("The file " + codingFileName + " does not contain all the required fields.  'Proposition', 'Reponds To', 'Relation Type', and 'Distance' are required.", null);
                return null;
            }

            PropositionList propositionList = new PropositionList(attributeList);
            propositionList.setFilename((new File(codingFileName)).getName());

            int fieldPos;
            String propositionString;
            String respondsToString;
            String relationTypeString;
            String distanceString;
            String textString;
            boolean dottedFlag;
            int maxPropositionIdLength = 0;

            while (null != (line = in.readLine())) {
                fields = splitFields(line);

                if (fields.length > 0) {
                    fieldPos = fieldMap.get("Proposition");

                    propositionString = fields[fieldPos];
                    if (propositionString.length() > maxPropositionIdLength) {
                        maxPropositionIdLength = propositionString.length();
                    }

                    fieldPos = fieldMap.get("Responds To");
                    respondsToString = fields[fieldPos];

                    fieldPos = fieldMap.get("Relation Type");
                    relationTypeString = fields[fieldPos].toUpperCase();

                    fieldPos = fieldMap.get("Distance");
                    distanceString = fields[fieldPos];

                    if (foundText) {
                        fieldPos = fieldMap.get("Description");
                        textString = fields[fieldPos];
                        textString = stripNonPrint(textString);
                        textString = replaceHtmlEntities(textString);
                    } else {
                        textString = "";
                    }

                    if (foundDottedLine) {
                        fieldPos = fieldMap.get("Dotted Line");
                        dottedFlag = fields[fieldPos].equals("1");
                    } else {
                        dottedFlag = false;
                    }

                    int distance = 0;
                    try {
                        distance = Integer.parseInt(distanceString);
                    } catch (NumberFormatException ignored) {
                    }

                    Map<String, String> attributeMap = new HashMap<>();
                    String attribute;
                    for (String s : attributeList) {
                        attribute = s;
                        fieldPos = fieldMap.get(attribute);
                        attributeMap.put(attribute, fields[fieldPos]);
                    }

                    PropositionData propositionData = new PropositionData(respondsToString, relationTypeString,
                            distance, dottedFlag, textString, attributeMap);

                    Proposition existingProposition = findProposition(propositionList, propositionString);
                    if (existingProposition != null) {
                        existingProposition.getPropositionDataList().add(propositionData);
                    } else {
                        propositionList.add(new Proposition(propositionString, propositionData));
                    }
                }
            }
            in.close();

            xPropositionIdPad = maxPropositionIdLength * charWidth;

            return (propositionList);

        } catch (Exception ex) {
            Utilities.errorMessage("There was an error when loading the coding file " + codingFileName + ". ", ex);
            return null;
        }

    }

    public PropositionDataList getPropositionDataList() {
        return propositionDataList;
    }

//    public void setPropositionDataList(PropositionDataList propositionDataList) {
//        this.propositionDataList = propositionDataList;
//    }

}
