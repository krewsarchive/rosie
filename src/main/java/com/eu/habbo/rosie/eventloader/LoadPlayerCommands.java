package com.eu.habbo.rosie.eventloader;

import com.eu.habbo.Emulator;
import com.eu.habbo.rosie.commands.*;
import com.eu.habbo.habbohotel.commands.CommandHandler;

public class LoadPlayerCommands {
    public static void loadPlayerCommands() {
        try {
            CommandHandler.addCommand(new SellRoomCommand("cmd_sellroom", Emulator.getTexts().getValue("rosie.sellroom.keys").split(";")));
            CommandHandler.addCommand(new BuyRoomCommand("cmd_buyroom", Emulator.getTexts().getValue("rosie.buyroom.keys").split(";")));
        } catch (Exception ex) {
            Emulator.getLogging().logErrorLine(ex);
        }
    }
}
