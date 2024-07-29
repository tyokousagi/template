package me.tyokousagi.template.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import me.tyokousagi.template.DiscordBot;

import java.awt.*;

public class ServerConnected {
    @Subscribe
    public void onServerConnected(LoginEvent event) {
        String player = event.getPlayer().getUsername();
        DiscordBot.sendEmbedMessage(Color.GREEN, null, "参加", player,true);
    }
}
