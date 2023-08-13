package md.mirrerror.discordutils.events;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.Message;
import md.mirrerror.discordutils.discord.EmbedManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;

public class ServerActivityListener implements Listener {

    private final EmbedManager embedManager = new EmbedManager();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        Main.getInstance().getBot().getServerActivityLoggingTextChannel().sendMessageEmbeds(
                embedManager.embed(
                        Message.CHAT_LOGGING_EMBED_TITLE.getText().getText().replace("%player%", player.getName()).replace("%message%", message),
                        Message.CHAT_LOGGING_EMBED_TEXT.getText().getText().replace("%player%", player.getName()).replace("%message%", message),
                        Color.decode(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("ServerActivityLogging.ChatEmbedColor"))
                )
        ).queue();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Main.getInstance().getBot().getServerActivityLoggingTextChannel().sendMessageEmbeds(
                embedManager.embed(
                        Message.JOIN_LOGGING_EMBED_TITLE.getText().getText().replace("%player%", player.getName()),
                        Message.JOIN_LOGGING_EMBED_TEXT.getText().getText().replace("%player%", player.getName()),
                        Color.decode(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("ServerActivityLogging.JoinEmbedColor"))
                )
        ).queue();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Main.getInstance().getBot().getServerActivityLoggingTextChannel().sendMessageEmbeds(
                embedManager.embed(
                        Message.QUIT_LOGGING_EMBED_TITLE.getText().getText().replace("%player%", player.getName()),
                        Message.QUIT_LOGGING_EMBED_TEXT.getText().getText().replace("%player%", player.getName()),
                        Color.decode(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("ServerActivityLogging.QuitEmbedColor"))
                )
        ).queue();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Main.getInstance().getBot().getServerActivityLoggingTextChannel().sendMessageEmbeds(
                embedManager.embed(
                        Message.DEATH_LOGGING_EMBED_TITLE.getText().getText().replace("%player%", player.getName()),
                        Message.DEATH_LOGGING_EMBED_TEXT.getText().getText().replace("%player%", player.getName()),
                        Color.decode(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("ServerActivityLogging.DeathEmbedColor"))
                )
        ).queue();
    }

}