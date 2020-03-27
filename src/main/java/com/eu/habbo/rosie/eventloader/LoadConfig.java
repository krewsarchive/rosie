package com.eu.habbo.rosie.eventloader;

import com.eu.habbo.Emulator;

public class LoadConfig {
    public static void loadConfig() {
        try {
            Emulator.getConfig().register("rosie.bubble.image.url","${image.library.url}notifications/generic.png");
            Emulator.getConfig().register("rosie.buyroom.currency.type","5");

        } catch (Exception ex) {
            Emulator.getLogging().logErrorLine(ex);
        }
    }
}
