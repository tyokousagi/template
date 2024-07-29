package me.tyokousagi.template.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import me.tyokousagi.template.DiscordBot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerChat {
    private final ProxyServer server;

    public PlayerChat(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage();
        String player = event.getPlayer().getUsername();
        String serverName = event.getPlayer().getCurrentServer().get().getServerInfo().getName();

        String discordMessage = "[" + serverName + "]" + "<" + player + ">" + message;
        DiscordBot.sendMessageToDiscord(discordMessage);

        Component minecraftMessage = Component.text()
                .append(Component.text("[" + serverName + "]").color(NamedTextColor.GREEN))
                .append(Component.text(" <" + player + "> ").color(NamedTextColor.WHITE))
                .append(Component.text(message).color(NamedTextColor.WHITE))
                .build();

        server.getAllPlayers().forEach(p -> p.sendMessage(minecraftMessage));

        event.setResult(PlayerChatEvent.ChatResult.denied());
    }
}