package com.andykurtz.visualdta;


import org.apache.log4j.Logger;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public Main() {
        logger.debug("Starting up");
        MainWin mainWin = new MainWin();
    }

    public static void main(String[] args) {
        Main main = new Main();
    }

}
