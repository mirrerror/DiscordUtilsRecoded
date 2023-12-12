package md.mirrerror.discordutils.events;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
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
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(event.getPlayer().getName());
        builder.setAvatarUrl("https://mc-heads.net/avatar/" + event.getPlayer().getName());
        builder.setContent(event.getMessage());
        client.send(builder.build());
    }

}
