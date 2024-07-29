package me.tyokousagi.template.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import me.tyokousagi.template.DiscordBot;

import java.awt.*;

public class ServerDisconnected {
    @Subscribe
    public void onServerDisconnect(DisconnectEvent event) {
        String player = event.getPlayer().getUsername();
        DiscordBot.sendEmbedMessage(Color.GRAY, null, "退出", player,true);
    }
}
