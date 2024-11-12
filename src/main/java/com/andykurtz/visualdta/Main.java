package com.andykurtz.visualdta;


import org.apache.logging.log4j.*;

public class Main {

    final static Logger logger = LogManager.getLogger(Main.class);

    public Main() {
        logger.debug("Starting up");
        MainWin mainWin = new MainWin();
    }

    public static void main(String[] args) {
        Main main = new Main();
    }

}
