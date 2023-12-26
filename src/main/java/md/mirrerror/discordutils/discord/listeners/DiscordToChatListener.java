package md.mirrerror.discordutils.discord.listeners;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DiscordToChatListener extends ListenerAdapter {

    private final DiscordUtilsBot bot;
    private final BotSettings botSettings;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(bot.getChatTextChannel() == null) return;
        if(event.getAuthor().isBot() || event.isWebhookMessage()) return;
        if(!event.isFromGuild()) return;
        if(event.getChannel().getIdLong() != bot.getChatTextChannel().getIdLong()) return;

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getAuthor().getIdLong());
        EmbedManager embedManager = new EmbedManager(botSettings);
        if(!discordUtilsUser.isLinked()) {
            bot.sendTimedMessageEmbed(event.getGuildChannel().asTextChannel(), embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText()), 10);
            return;
        }

        Bukkit.broadcastMessage(Message.DISCORD_TO_CHAT_FORMAT.getText().replace("%user%", event.getAuthor().getName())
                .replace("%message%", event.getMessage().getContentRaw()).replace("%player%", discordUtilsUser.getOfflinePlayer().getName()));
    }

}
