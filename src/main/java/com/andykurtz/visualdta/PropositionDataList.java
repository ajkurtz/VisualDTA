package com.andykurtz.visualdta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class PropositionDataList {
    List<PropositionData> list;


    PropositionDataList() {
        list = new ArrayList<>();
    }


    public void add(PropositionData value) {
        list.add(value);
    }

    public PropositionData get(int index) {
        return list.get(index);
    }


    public Iterator<PropositionData> iterator() {
        return (list.iterator());
    }


    public int size() {
        return (list.size());
    }

}

