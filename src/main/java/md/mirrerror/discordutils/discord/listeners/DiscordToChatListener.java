package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class DiscordToChatListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(Main.getInstance().getBot().getChatTextChannel() == null) return;
        if(event.getAuthor().isBot() || event.isWebhookMessage()) return;
        if(!event.isFromGuild()) return;
        if(event.getChannel().asTextChannel().getIdLong() != Main.getInstance().getBot().getChatTextChannel().getIdLong()) return;

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getAuthor().getIdLong());
        EmbedManager embedManager = new EmbedManager();
        if(!discordUtilsUser.isLinked()) {
            Main.getInstance().getBot().sendTimedMessageEmbeds(event.getGuildChannel().asTextChannel(), embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText()), 10);
            return;
        }

        Bukkit.broadcastMessage(Message.DISCORD_TO_CHAT_FORMAT.getText().replace("%user%", event.getAuthor().getAsTag())
                .replace("%message%", event.getMessage().getContentRaw()).replace("%player%", discordUtilsUser.getOfflinePlayer().getName()));
    }

}
