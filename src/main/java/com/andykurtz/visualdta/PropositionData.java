package com.andykurtz.visualdta;

import java.util.Map;

public class PropositionData {

    public String respondsTo;
    public String relationType;
    public int distance;
    public boolean dotted;
    public String text;
    public Map<String, String> attributeMap;
    public String svgID;
    public String svgHighlight;
    public String plain;

    PropositionData(String respondsTo, String relationType, int distance, boolean dotted, String text, Map<String, String> attributeMap) {
        this.respondsTo = respondsTo;
        this.relationType = relationType;
        this.distance = distance;
        this.dotted = dotted;
        this.text = text;
        this.attributeMap = attributeMap;
        this.svgID = null;
        this.svgHighlight = null;
        this.plain = null;
    }

}

