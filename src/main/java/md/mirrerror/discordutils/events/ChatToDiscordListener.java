package md.mirrerror.discordutils.events;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatToDiscordListener implements Listener {

    private final WebhookClient client;

    public ChatToDiscordListener(String webhookUrl) {
        WebhookClientBuilder builder = new WebhookClientBuilder(webhookUrl);
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("du_chat_to_discord");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);
        this.client = builder.build();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());
        if (discordUtilsUser == null) return;

        String message = event.getMessage();

        if (!message.contains("@")) return;

        StringBuilder messageBuilder = new StringBuilder();
        String[] words = message.split(" ");
        for (String word : words) {
            if (word.startsWith("@")) {
                String mentionedPlayerName = word.substring(1);
                Player mentionedPlayer = Bukkit.getPlayer(mentionedPlayerName);
                if (mentionedPlayer != null) return;
                OfflinePlayer mentionedOfflinePlayer = Bukkit.getOfflinePlayer(mentionedPlayerName);
                DiscordUtilsUser mentionedDiscordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(mentionedOfflinePlayer.getUniqueId());
                if (mentionedDiscordUtilsUser == null) return;
                String mention = mentionedDiscordUtilsUser.getUser().getAsMention();
                messageBuilder.append(mention);
            } else {
                messageBuilder.append(word);
            }
            messageBuilder.append(" ");
        }

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(event.getPlayer().getName());
        builder.setAvatarUrl("https://mc-heads.net/avatar/" + event.getPlayer().getName());
        builder.setContent(messageBuilder.toString());
        client.send(builder.build());
    }

}
