package com.eu.habbo.rosie.room;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

public class IsForSale {
    // Credits to Layne for this code.
    public static void enterRoomForSale(Habbo habbo) {
        habbo.alert(Emulator.getTexts().getValue("rosie.roomforsale.alert"));
        habbo.getHabboStats().cache.put("isForSale", "yes");
    }
    public static void exitRoomForSale(Habbo habbo) {
        habbo.getHabboStats().cache.remove("isForSale");
    }
}
