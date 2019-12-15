package com.andykurtz.visualdta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class PropositionList {
    private List<Proposition> list = null;
    private List<String> attributeList = null;
    private String filename = null;

    PropositionList(List<String> attributeList) {
        list = new ArrayList<>();
        this.attributeList = attributeList;
    }

    public String getFilename() {
        return (filename);
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getAttributeList() {
        return (attributeList);
    }

    public void add(Proposition value) {
        list.add(value);
    }


    public void set(int index, Proposition value) {
        list.set(index, value);
    }


    public Proposition get(int index) {
        return list.get(index);
    }

    public Iterator<Proposition> iterator() {
        return list.iterator();
    }

    public int size() {
        return (list.size());
    }

}

