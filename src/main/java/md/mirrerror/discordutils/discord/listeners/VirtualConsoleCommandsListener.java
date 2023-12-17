package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.BotSettingsManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class VirtualConsoleCommandsListener extends ListenerAdapter {

    private final EmbedManager embedManager = new EmbedManager();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!BotSettingsManager.CONSOLE_ENABLED) return;
        if(event.getAuthor().isBot() || event.isWebhookMessage()) return;
        if(!event.isFromGuild()) return;
        if(event.getChannelType() != ChannelType.TEXT) return;

        TextChannel textChannel = event.getChannel().asTextChannel();
        int deleteDelay = BotSettingsManager.CONSOLE_DELETE_MESSAGES_DELAY;

        if(!textChannel.getId().equals(Main.getInstance().getBot().getConsoleLoggingTextChannel().getId())) return;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getAuthor().getIdLong());
        if(!discordUtilsUser.isAdmin(event.getGuild())) {
            if(deleteDelay < 1) textChannel.sendMessageEmbeds(embedManager.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText())).queue();
            else Main.getInstance().getBot().sendTimedMessageEmbed(textChannel, embedManager.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText()), deleteDelay);
            return;
        }

        for(String cmd : Main.getInstance().getBot().getVirtualConsoleBlacklistedCommands()) if(event.getMessage().getContentRaw().startsWith(cmd)) {
            if(deleteDelay < 1) textChannel.sendMessageEmbeds(embedManager.errorEmbed(Message.THIS_COMMAND_IS_BLACKLISTED.getText())).queue();
            else Main.getInstance().getBot().sendTimedMessageEmbed(textChannel, embedManager.errorEmbed(Message.THIS_COMMAND_IS_BLACKLISTED.getText()), deleteDelay);
            return;
        }

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentRaw()));
        if(deleteDelay < 1) textChannel.sendMessageEmbeds(embedManager.successfulEmbed(Message.COMMAND_EXECUTED.getText())).queue();
        else Main.getInstance().getBot().sendTimedMessageEmbed(textChannel, embedManager.successfulEmbed(Message.COMMAND_EXECUTED.getText()), deleteDelay);
    }

}
