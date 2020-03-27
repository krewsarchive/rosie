package com.eu.habbo.rosie.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.map.hash.THashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class BuyRoomCommand extends Command {
    public BuyRoomCommand(String permission, String[] keys) {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception {
        synchronized (SellRoomCommand.sellingRooms) {
            final Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

            if (!SellRoomCommand.sellingRooms.containsKey(room.getId()) || room.hasGuild()) {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("rosie.sellroom.notforsale"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }

            if (strings.length == 1) {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("rosie.sellroom.forsale").replace("%currency%", SellRoomCommand.sellingRooms.get(room.getId()) + "").replace("%ownername%", room.getOwnerName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;

            } else if (strings.length == 2 && strings[1].equalsIgnoreCase(Emulator.getTexts().getValue("rosie.sellroom.confirmkey"))) {
                Habbo owner = Emulator.getGameEnvironment().getHabboManager().getHabbo(room.getOwnerId());

                if (owner == null) {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("rosie.sellroom.owneroffline").replace("%username%", room.getOwnerName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;

                } else if (owner == gameClient.getHabbo()) {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("rosie.sellroom.selfbuy").replace("%username%", room.getOwnerName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                if (room.hasGuild()) {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("rosie.buyroom.has_guild"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }


                int points = SellRoomCommand.sellingRooms.get(room.getId());

                if (gameClient.getHabbo().getHabboInfo().getCurrencyAmount(Emulator.getConfig().getInt("rosie.buyroom.currency.type")) < points) {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("rosie.buyroom.invalid_amount").replace("%username%", room.getOwnerName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                if (gameClient.getHabbo().getHabboInfo().getCurrencyAmount(Emulator.getConfig().getInt("rosie.buyroom.currency.type")) >= points) {
                    synchronized (room) {
                        SellRoomCommand.sellingRooms.remove(room.getId());
                        for (HabboItem item : room.getFloorItems()) {
                            if (item.getUserId() == room.getOwnerId()) {
                                item.setUserId(gameClient.getHabbo().getHabboInfo().getId());
                                item.needsUpdate(true);
                            }
                        }

                        for (HabboItem item : room.getWallItems()) {
                            if (item.getUserId() == room.getOwnerId()) {
                                item.setUserId(gameClient.getHabbo().getHabboInfo().getId());
                                item.needsUpdate(true);
                            }
                        }
                        int roomId = room.getId();
                        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                                try (PreparedStatement statementt = connection.prepareStatement("UPDATE rooms SET is_forsale = '0' WHERE id = '"+roomId+"'")) {
                                    statementt.execute();
                                }
                            }
                        catch(SQLException e) {}
                        owner.givePoints(Emulator.getConfig().getInt("rosie.buyroom.currency.type"), points);
                        gameClient.getHabbo().givePoints(Emulator.getConfig().getInt("rosie.buyroom.currency.type"), -points);
                        room.setOwnerId(gameClient.getHabbo().getHabboInfo().getId());
                        room.setOwnerName(gameClient.getHabbo().getHabboInfo().getUsername());
                        room.setNeedsUpdate(true);
                        Collection<Habbo> habbos = new ArrayList<>(room.getHabbos());
                        Emulator.getGameEnvironment().getRoomManager().unloadRoom(room);
                        ServerMessage forwarder = new ForwardToRoomComposer(room.getId()).compose();
                        THashMap<String, String> notify_keys = new THashMap<>();
                        notify_keys.put("display", "BUBBLE");
                        notify_keys.put("image", Emulator.getConfig().getValue("rosie.bubble.image.url"));
                        notify_keys.put("message",
                                Emulator.getTexts().getValue("rosie.buyroom.bubble.message").replace("%newowner%", gameClient.getHabbo().getHabboInfo().getUsername()).replace("%oldowner%", owner.getHabboInfo().getUsername()));
                        ServerMessage bubblemessage = new BubbleAlertComposer("mentioned", notify_keys).compose();
                        for (Habbo habbo : habbos) {
                            GameClient client = habbo.getClient();
                            if (client != null) {
                                client.sendResponse(forwarder);
                                client.sendResponse(bubblemessage);
                            }
                        }
                        }

                    }
                }
                else {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("rosie.sellroom.buyroom.notenough").replace("%key%", this.keys[0]).replace("%confirmkey%", Emulator.getTexts().getValue("rosie.sellroom.confirmkey")));
                }
            }

            return true;
        }
    }

