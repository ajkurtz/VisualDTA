package com.andykurtz.visualdta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PropositionStats {

    public int numberPropositions;
    public double avgSemanticDistance;
    public double avgSemanticDistanceP;
    public Map<String, Integer> moveCounts;
    public Map<String, Double> movePercents;
    public Map<String, Map<String, Integer>> attributeValueHash;
//    public int threadCount = 0;
//    public int avgPropositionsPerThread = 0;

    PropositionStats(PropositionList propositionList) {
        moveCounts = new HashMap<>();
        moveCounts.put("T", 0);
        moveCounts.put("P", 0);
        moveCounts.put("B", 0);
        moveCounts.put("M", 0);
        moveCounts.put("E", 0);

        movePercents = new HashMap<>();
        attributeValueHash = new HashMap<>();

        numberPropositions = propositionList.size();

        int allDistance = 0;
        int pDistance = 0;
        int pCount = 0;

        List<String> attributeArray = propositionList.getAttributeList();
        for (String s : attributeArray) {
            attributeValueHash.put(s, new HashMap<>());
        }

        Iterator<Proposition> propIterator = propositionList.iterator();
        while (propIterator.hasNext()) {
            Proposition p = propIterator.next();
            Iterator<PropositionData> dataIterator = p.getPropositionDataList().iterator();
            while (dataIterator.hasNext()) {
                PropositionData pd = dataIterator.next();

                allDistance += pd.distance;

                if (pd.relationType.equals("P")) {
                    pDistance += pd.distance;
                    ++pCount;
                }

                moveCounts.merge(pd.relationType, 1, Integer::sum);

                Map<String, String> attributeMap = pd.attributeMap;
                for (String s : attributeArray) {
                    Map<String, Integer> hm = attributeValueHash.get(s);
                    String value = attributeMap.get(s);
                    if ((value != null) && (!value.equalsIgnoreCase("NA"))) {
                        hm.merge(value, 1, Integer::sum);
                        attributeValueHash.put(s, hm);
                    }
                }
            }
        }

        for (String move : moveCounts.keySet()) {
            double count = moveCounts.get(move).doubleValue();
            movePercents.put(move, count / (double) numberPropositions);
        }

        avgSemanticDistance = (double) allDistance / (double) numberPropositions;
        avgSemanticDistanceP = (double) pDistance / (double) pCount;

    }

}
