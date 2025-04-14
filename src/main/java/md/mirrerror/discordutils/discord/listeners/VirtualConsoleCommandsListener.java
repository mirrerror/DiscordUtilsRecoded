package md.mirrerror.discordutils.discord.listeners;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.discord.EmbedMessagesBuilder;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class VirtualConsoleCommandsListener extends ListenerAdapter {

    private final Plugin plugin;
    private final DiscordUtilsBot bot;
    private final BotSettings botSettings;
    private final EmbedMessagesBuilder embedMessagesBuilder;

    public VirtualConsoleCommandsListener(Plugin plugin, DiscordUtilsBot bot, BotSettings botSettings) {
        this.plugin = plugin;
        this.bot = bot;
        this.botSettings = botSettings;
        this.embedMessagesBuilder = new EmbedMessagesBuilder(botSettings);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!botSettings.CONSOLE_ENABLED) return;
        if(event.getAuthor().isBot() || event.isWebhookMessage()) return;
        if(!event.isFromGuild()) return;
        if(event.getChannelType() != ChannelType.TEXT) return;

        TextChannel textChannel = event.getChannel().asTextChannel();
        int deleteDelay = botSettings.CONSOLE_DELETE_MESSAGES_DELAY;

        if(!textChannel.getId().equals(bot.getConsoleLoggingTextChannel().getId())) return;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getAuthor().getIdLong());
        if(!discordUtilsUser.isAdmin(event.getGuild())) {
            if(deleteDelay < 1) textChannel.sendMessageEmbeds(embedMessagesBuilder.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText()).build()).queue();
            else bot.sendTimedMessageEmbed(textChannel, embedMessagesBuilder.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText()).build(), deleteDelay);
            return;
        }

        for(String cmd : bot.getVirtualConsoleBlacklistedCommands()) if(event.getMessage().getContentRaw().startsWith(cmd)) {
            if(deleteDelay < 1) textChannel.sendMessageEmbeds(embedMessagesBuilder.errorEmbed(Message.THIS_COMMAND_IS_BLACKLISTED.getText()).build()).queue();
            else bot.sendTimedMessageEmbed(textChannel, embedMessagesBuilder.errorEmbed(Message.THIS_COMMAND_IS_BLACKLISTED.getText()).build(), deleteDelay);
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentRaw()));
        if(deleteDelay < 1) textChannel.sendMessageEmbeds(embedMessagesBuilder.successfulEmbed(Message.COMMAND_EXECUTED.getText()).build()).queue();
        else bot.sendTimedMessageEmbed(textChannel, embedMessagesBuilder.successfulEmbed(Message.COMMAND_EXECUTED.getText()).build(), deleteDelay);
    }

}
