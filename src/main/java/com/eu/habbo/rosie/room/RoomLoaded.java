package com.eu.habbo.rosie.room;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.users.UserEnterRoomEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomLoaded implements EventListener {

    @EventHandler
    public static void onRoomLoaded(UserEnterRoomEvent event) throws IOException {

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms WHERE id=?")) {
                statement.setInt(1, event.room.getId());
                ResultSet rs = statement.executeQuery();

                if(rs.next()) {
                    if (rs.getInt("is_forsale") == 1) {
                        IsForSale.enterRoomForSale(event.habbo);
                    }
                }

                rs.close();
                statement.getConnection().close();

            }
        } catch(SQLException e) {
        }
    }
}