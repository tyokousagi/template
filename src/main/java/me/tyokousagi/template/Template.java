package me.tyokousagi.template;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.tyokousagi.template.commands.ServerCommand;
import me.tyokousagi.template.events.*;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "template",
        name = "template",
        version = BuildConstants.VERSION
)
public class Template {
    private final Logger logger;
    private final ProxyServer proxy;
    private final Path dataDirectory;
    @Inject
    public Template(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        logger.info("TabListManager initialized");
        DiscordBot.setProxyServer(proxy);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxy.getEventManager().register(this, new ServerConnected());
        proxy.getEventManager().register(this, new ServerDisconnected());
        proxy.getEventManager().register(this, new PlayerChat(proxy));
        proxy.getEventManager().register(this, new DiscordBot());

        DiscordBot.BOT();

        logger.info("Template plugin has been initialized!");

        ServerCommand command = new ServerCommand(proxy);
        proxy.getAllServers().forEach(registeredServer -> {
            String serverName = registeredServer.getServerInfo().getName();
            proxy.getCommandManager().register(serverName, command, serverName);
        });
        logger.info("動的サーバーコマンドが登録されました。");
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

}