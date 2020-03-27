package com.eu.habbo.rosie.eventloader;

import com.eu.habbo.Emulator;

public class LoadTexts {
    public static void loadTexts() {
        try {
            Emulator.getTexts().register("commands.description.cmd_buyroom", ":buyroom");
            Emulator.getTexts().register("commands.description.cmd_sellroom", ":sellroom <diamonds>");
            Emulator.getTexts().register("rosie.sellroom.keys", "sellroom");
            Emulator.getTexts().register("rosie.sellroom.removed", "This room was remove from sale.");
            Emulator.getTexts().register("rosie.sellroom.invalid_credits", "%diamonds% diamonds is an invalid amount!");
            Emulator.getTexts().register("rosie.sellroom.has_guild", "Cannot sell this room, room has a guild.");
            Emulator.getTexts().register("rosie.sellroom.confirmed", "This room is now up for sale for %currency% diamonds!");
            Emulator.getTexts().register("rosie.sellroom.usage", "Usage: :sellroom <diamonds>");
            Emulator.getTexts().register("rosie.sellroom.forsale", "This room is being sold by %ownername% for %currency% diamonds! If you wish to buy it write :buyroom confirm.");
            Emulator.getTexts().register("rosie.sellroom.notforsale", "This room is not being sold.");
            Emulator.getTexts().register("rosie.sellroom.confirmkey", "confirm");
            Emulator.getTexts().register("rosie.buyroom.has_guild", "This room has a guild and therefor cannot be bought.");
            Emulator.getTexts().register("rosie.sellroom.bubble.thisroom", "This room is for sale for %currency% diamonds!");
            Emulator.getTexts().register("rosie.buyroom.bubble.message", "This room has been bought from %oldowner% by %newowner%!");
            Emulator.getTexts().register("rosie.buyroom.bubble.message.bought", "%roomname% has been bought by %newowner%");
            Emulator.getTexts().register("rosie.buyroom.invalid_amount", "You don't have enough diamonds to buy this room!");
            Emulator.getTexts().register("rosie.sellroom.owneroffline", "Owner of this room, %username%, is offline.");
            Emulator.getTexts().register("rosie.sellroom.selfbuy", "Silly %username%. You cannot buy your own room.");
            Emulator.getTexts().register("rosie.sellroom.buyroom.usage", "To buy a room use :%key% %confirmkey%");
            Emulator.getTexts().register("rosie.buyroom.keys", "buyroom");
            Emulator.getTexts().register("rosie.roomforsale.alert", "This room is for sale. Type :buyroom for more information.");
        } catch (Exception ex) {
            Emulator.getLogging().logErrorLine(ex);
        }
    }
}
