package me.tyokousagi.template.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.dv8tion.jda.api.hooks.EventListener;

public class ServerCommand implements SimpleCommand {
    private final ProxyServer proxy;

    public ServerCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (source instanceof Player) {
            Player player = (Player) source;
            String command = invocation.alias();
            proxy.getServer(command).ifPresentOrElse(
                    targetServer -> player.createConnectionRequest(targetServer).fireAndForget(),
                    () -> player.sendMessage(Component.text("サーバーが見つかりません: " + command))
            );
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true; // 必要に応じて権限チェックを追加
    }
}
