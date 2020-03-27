package com.eu.habbo.rosie;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.rosie.room.IsForSale;
import com.eu.habbo.rosie.room.RoomLoaded;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.eu.habbo.Emulator.ANSI_BLUE;
import static com.eu.habbo.Emulator.ANSI_WHITE;
import static com.eu.habbo.rosie.eventloader.loadAll.loadAll;

/* Rosie
   Buy and Sell Room System.
   #Go Go Team Krews. Love for Harmony, Beny, Alejandro, ArpyAge, Layne, Bill, Ridge and Cronk.
 */
public class Rosie extends HabboPlugin implements EventListener {
    public static Rosie INSTANCE = null;

    @Override
    public void onEnable() {
        INSTANCE = this;
        Emulator.getPluginManager().registerEvents(this, this);
        Emulator.getPluginManager().registerEvents(Rosie.INSTANCE, new RoomLoaded());
        if (Emulator.isReady) {
            this.checkDatabase();
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean hasPermission(Habbo habbo, String s) {
        return false;
    }

    private boolean registerPermission(String name, String options, String defaultValue, boolean defaultReturn)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE  `permissions` ADD  `" + name +"` ENUM(  " + options + " ) NOT NULL DEFAULT  '" + defaultValue + "'"))
            {
                statement.execute();
                return true;
            }
        }
        catch (SQLException e)
        {}
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE  `rooms` ADD  `" + "is_forsale" +"` ENUM(  " + options + " ) NOT NULL DEFAULT  '" + "0" + "'"))
            {
                statement.execute();
                return true;
            }
        }
        catch (SQLException e)
        {}

        return defaultReturn;
    }

    @EventHandler
    public static void onEmulatorLoaded(EmulatorLoadedEvent event) throws Exception {
        INSTANCE.checkDatabase();
        loadAll();
        System.out.println("[" + ANSI_BLUE + "OFFICIAL PLUGIN" + ANSI_WHITE + "] " + "Rosie (1.0.0) has officially loaded!");
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET is_forsale = '0' WHERE is_forsale = '1';"))
            {
                statement.execute();
            }
        }
        catch (SQLException e)
        {}
    }

    public void checkDatabase() {
        boolean reloadPermissions = false;
        reloadPermissions = this.registerPermission("cmd_sellroom", "'0', '1', '2'", "1", reloadPermissions);
        reloadPermissions = this.registerPermission("cmd_buyroom", "'0', '1', '2'", "1", reloadPermissions);
        if (reloadPermissions)
        {
            Emulator.getGameEnvironment().getPermissionsManager().reload();
        }

    }

    public static void main(String[] args)
    {
        System.out.println("Don't run this separately");
    }
}