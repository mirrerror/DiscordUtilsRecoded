package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.BotSettingsManager;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MentionsListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!BotSettingsManager.NOTIFY_ABOUT_MENTIONS_ENABLED) return;
        if(event.getAuthor().isBot() || event.isWebhookMessage()) return;
        if(!event.isFromGuild()) return;
        if(Main.getInstance().getBot().getNotifyAboutMentionsBlacklistedChannels().contains(event.getChannel().asTextChannel().getIdLong())) return;

        for(Member member : event.getMessage().getMentions().getMembers()) {
            User user = member.getUser();
            DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(user.getIdLong());

            if(!discordUtilsUser.isLinked()) continue;
            if(!discordUtilsUser.getOfflinePlayer().isOnline()) continue;

            Player player = discordUtilsUser.getOfflinePlayer().getPlayer();

            if(BotSettingsManager.NOTIFY_ABOUT_MENTIONS_TITLE_ENABLED) {
                int fadeIn = BotSettingsManager.NOTIFY_ABOUT_MENTIONS_TITLE_FADE_IN;
                int stay = BotSettingsManager.NOTIFY_ABOUT_MENTIONS_TITLE_STAY;
                int fadeOut = BotSettingsManager.NOTIFY_ABOUT_MENTIONS_TITLE_FADE_OUT;
                String title = ChatColor.translateAlternateColorCodes('&', BotSettingsManager.NOTIFY_ABOUT_MENTIONS_TITLE_TITLE);
                String subtitle = ChatColor.translateAlternateColorCodes('&', BotSettingsManager.NOTIFY_ABOUT_MENTIONS_TITLE_SUBTITLE);

                player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }

            if(BotSettingsManager.NOTIFY_ABOUT_MENTIONS_MESSAGE_ENABLED) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', BotSettingsManager.NOTIFY_ABOUT_MENTIONS_MESSAGE_TEXT));
            }

            if(BotSettingsManager.NOTIFY_ABOUT_MENTIONS_SOUND_ENABLED) {
                String soundType = BotSettingsManager.NOTIFY_ABOUT_MENTIONS_SOUND_TYPE;
                float volume = BotSettingsManager.NOTIFY_ABOUT_MENTIONS_SOUND_VOLUME;
                float pitch = BotSettingsManager.NOTIFY_ABOUT_MENTIONS_SOUND_PITCH;
                player.playSound(player.getLocation(), Sound.valueOf(soundType.toUpperCase()), volume, pitch);
            }

        }
    }

}
