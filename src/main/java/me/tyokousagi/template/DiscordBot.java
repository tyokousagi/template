package me.tyokousagi.template;

import com.velocitypowered.api.proxy.ProxyServer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

public class DiscordBot extends ListenerAdapter implements EventListener {
    public static JDA jda;
    public static TextChannel channel;
    public static String TOKEN;
    public static String GUILD_ID;
    public static String CHANNEL_ID;
    public static final Logger LOGGER = LoggerFactory.getLogger("template-plugin");
    private static ProxyServer proxyServer;

    static {
        try (InputStream input = DiscordBot.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            } else {
                // load a properties file from class path, inside static method
                prop.load(input);
                // get the property value and assign to variables
                TOKEN = prop.getProperty("TOKEN");
                GUILD_ID = prop.getProperty("GUILD_ID");
                CHANNEL_ID = prop.getProperty("CHANNEL_ID");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // パスの設定

    static void BOT() {
        if (TOKEN == null || TOKEN.isEmpty()) {
            throw new IllegalArgumentException("DISCORD_TOKEN environment variable is not set or empty.");
        }
        try {
            jda = JDABuilder.createDefault(TOKEN)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.playing("m1rutanのボット"))
                    .addEventListeners(new DiscordBot())
                    .build();
            jda.awaitReady();
            channel = jda.getGuildById(GUILD_ID).getTextChannelById(CHANNEL_ID);

            System.out.println("Bot is ready!");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Initialization interrupted: " + e.getMessage());
        }
    }

    // その他のメソッドはそのまま



public static void sendEmbedMessage(Color color, String title_1, String title_2, String playerName, boolean includeAuthor) {
        if (channel != null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(title_1);
            embedBuilder.setColor(color);
            if (includeAuthor && playerName != null && !playerName.isEmpty()) {
                embedBuilder.setAuthor(playerName + "が" + title_2 + "しました", null, "https://mc-heads.net/avatar/" + playerName + ".png");
            }
            MessageEmbed embed = embedBuilder.build();
            channel.sendMessageEmbeds(embed).queue(
                    success -> System.out.println("メッセージが送信されました"),
                    error -> {
                        error.printStackTrace();
                        System.out.println("メッセージの送信に失敗しました");
                    }
            );
        } else {
            System.out.println("チャンネルが設定されていません");
        }
    }

    public static void sendMessageToDiscord(String message) {
        if (channel != null) {
            channel.sendMessage(message).queue(
                    success -> System.out.println("Discordにメッセージが送信されました: " + message),
                    error -> {
                        error.printStackTrace();
                        System.out.println("Discordへのメッセージ送信に失敗しました: " + message);
                    }
            );
        } else {
            System.out.println("チャンネルが設定されていません");
        }
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        LOGGER.info("Message received: " + event.getMessage().getContentDisplay());
        LOGGER.info("Author: " + event.getAuthor().getName());
        LOGGER.info("Channel: " + event.getChannel().getName());
        LOGGER.info("Guild: " + (event.isFromGuild() ? event.getGuild().getName() : "DM"));
        LOGGER.info("Message received: " + event.getMessage().getContentDisplay());

        // ボットのメッセージは無視
        if (event.getAuthor().isBot()) {
            LOGGER.info("Ignoring bot message");
            return;
        }

        // メッセージが送信されたギルドをチェック
        if (event.isFromGuild()) {
            LOGGER.info("Message is from guild: " + event.getGuild().getId());
            if (event.getGuild().getId().equals(GUILD_ID)) {
                LOGGER.info("Guild ID matches");
                // 特定のチャンネルからのメッセージのみ処理
                if (event.getChannel().getId().equals(CHANNEL_ID)) {
                    LOGGER.info("Channel ID matches");
                    String authorName = event.getAuthor().getName();
                    String content = event.getMessage().getContentDisplay();

                    LOGGER.info("Sending to Minecraft: " + authorName + ": " + content);
                    // Minecraftのチャットに送信
                    sendToMinecraft(authorName, content);
                } else {
                    LOGGER.info("Channel ID does not match. Expected: " + CHANNEL_ID + ", Actual: " + event.getChannel().getId());
                }
            } else {
                LOGGER.info("Guild ID does not match. Expected: " + GUILD_ID + ", Actual: " + event.getGuild().getId());
            }
        } else {
            LOGGER.info("Message is not from a guild");
        }
    }

    public static void sendToMinecraft(String authorName, String content) {
        if (proxyServer != null) {
            Component message = Component.text()
                    .append(Component.text("[Discord] ", NamedTextColor.GREEN))
                    .append(Component.text("<" + authorName + "> ", NamedTextColor.WHITE))
                    .append(Component.text(content, NamedTextColor.WHITE))
                    .build();

            proxyServer.getAllPlayers().forEach(player -> player.sendMessage(message));
            LOGGER.info("Sent to Minecraft: " + authorName + ": " + content);
        } else {
            LOGGER.error("ProxyServer is not set. Cannot send message to Minecraft.");
        }
    }

    public static void setProxyServer(ProxyServer server) {
        proxyServer = server;
    }
}
