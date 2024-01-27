package md.mirrerror.discordutils.events;

import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerActivityListener implements Listener {

    private final DiscordUtilsBot bot;
    private final EmbedManager embedManager;
    private final BotSettings botSettings;

    public ServerActivityListener(DiscordUtilsBot discordUtilsBot, BotSettings botSettings) {
        this.bot = discordUtilsBot;
        this.botSettings = botSettings;
        this.embedManager = new EmbedManager(botSettings);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(!botSettings.SERVER_ACTIVITY_LOGGING_CHAT_ENABLED) return;

        Player player = event.getPlayer();
        String message = event.getMessage();
        bot.getServerActivityLoggingTextChannel().sendMessageEmbeds(
                embedManager.embed(
                        Message.CHAT_LOGGING_EMBED_TITLE.getText().replace("%player%", player.getName()).replace("%message%", message),
                        Message.CHAT_LOGGING_EMBED_TEXT.getText().replace("%player%", player.getName()).replace("%message%", message),
                        botSettings.SERVER_ACTIVITY_LOGGING_CHAT_EMBED_COLOR
                )
        ).queue();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(!botSettings.SERVER_ACTIVITY_LOGGING_JOIN_ENABLED) return;

        Player player = event.getPlayer();
        bot.getServerActivityLoggingTextChannel().sendMessageEmbeds(
                embedManager.embed(
                        Message.JOIN_LOGGING_EMBED_TITLE.getText().replace("%player%", player.getName()),
                        Message.JOIN_LOGGING_EMBED_TEXT.getText().replace("%player%", player.getName()),
                        botSettings.SERVER_ACTIVITY_LOGGING_JOIN_EMBED_COLOR
                )
        ).queue();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(!botSettings.SERVER_ACTIVITY_LOGGING_QUIT_ENABLED) return;

        Player player = event.getPlayer();
        bot.getServerActivityLoggingTextChannel().sendMessageEmbeds(
                embedManager.embed(
                        Message.QUIT_LOGGING_EMBED_TITLE.getText().replace("%player%", player.getName()),
                        Message.QUIT_LOGGING_EMBED_TEXT.getText().replace("%player%", player.getName()),
                        botSettings.SERVER_ACTIVITY_LOGGING_QUIT_EMBED_COLOR
                )
        ).queue();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if(!botSettings.SERVER_ACTIVITY_LOGGING_DEATH_ENABLED) return;

        Player player = event.getEntity();
        bot.getServerActivityLoggingTextChannel().sendMessageEmbeds(
                embedManager.embed(
                        Message.DEATH_LOGGING_EMBED_TITLE.getText().replace("%player%", player.getName()),
                        Message.DEATH_LOGGING_EMBED_TEXT.getText().replace("%player%", player.getName()),
                        botSettings.SERVER_ACTIVITY_LOGGING_DEATH_EMBED_COLOR
                )
        ).queue();
    }

}
