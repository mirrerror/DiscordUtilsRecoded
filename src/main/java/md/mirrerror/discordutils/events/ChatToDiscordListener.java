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
        if (!discordUtilsUser.isLinked()) return;

        String message = event.getMessage();

        StringBuilder messageBuilder = new StringBuilder();
        String[] words = message.split(" ");
        for (String word : words) {
            if (word.startsWith("@")) {
                String mentionPart = word.substring(1);
                String mentionedPlayerName = extractMinecraftUsername(mentionPart);

                if (!mentionedPlayerName.isEmpty()) {

                    OfflinePlayer mentionedOfflinePlayer = Bukkit.getOfflinePlayer(mentionedPlayerName);
                    DiscordUtilsUser mentionedDiscordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(mentionedOfflinePlayer.getUniqueId());

                    if (mentionedDiscordUtilsUser.isLinked()) {
                        String mention = mentionedDiscordUtilsUser.getUser().getAsMention();
                        String remainingPart = mentionPart.substring(mentionedPlayerName.length());
                        messageBuilder.append(mention).append(remainingPart);
                    } else {
                        messageBuilder.append(word);
                    }

                } else {
                    messageBuilder.append(word);
                }
            } else {
                messageBuilder.append(word);
            }
            messageBuilder.append(" ");
        }

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(event.getPlayer().getName());
        builder.setAvatarUrl("https://mc-heads.net/avatar/" + event.getPlayer().getName());
        builder.setContent(messageBuilder.toString().trim());
        client.send(builder.build());
    }

    private String extractMinecraftUsername(String input) {
        StringBuilder username = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == '_') {
                username.append(c);
            } else {
                break;
            }
        }

        return username.toString();
    }

}