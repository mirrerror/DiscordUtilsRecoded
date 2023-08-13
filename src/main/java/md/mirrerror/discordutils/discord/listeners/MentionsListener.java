package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
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
        if(!Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("NotifyAboutMentions.Enabled")) return;
        if(event.getAuthor().isBot() || event.isWebhookMessage()) return;
        if(!event.isFromGuild()) return;
        if(Main.getInstance().getBot().getNotifyAboutMentionsBlacklistedChannels().contains(event.getTextChannel().getIdLong())) return;

        for(Member member : event.getMessage().getMentions().getMembers()) {
            User user = member.getUser();
            DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(user.getIdLong());

            if(!discordUtilsUser.isLinked()) continue;
            if(!discordUtilsUser.getOfflinePlayer().isOnline()) continue;

            Player player = discordUtilsUser.getOfflinePlayer().getPlayer();

            if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("NotifyAboutMentions.Title.Enabled")) {
                int fadeIn = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getInt("NotifyAboutMentions.Title.FadeIn");
                int stay = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getInt("NotifyAboutMentions.Title.Stay");
                int fadeOut = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getInt("NotifyAboutMentions.Title.FadeOut");
                String title = ChatColor.translateAlternateColorCodes('&',
                        Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("NotifyAboutMentions.Title.Title"));
                String subtitle = ChatColor.translateAlternateColorCodes('&',
                        Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("NotifyAboutMentions.Title.Subtitle"));

                player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }

            if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("NotifyAboutMentions.Message.Enabled")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("NotifyAboutMentions.Message.Text")));
            }

            if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("NotifyAboutMentions.Sound.Enabled")) {
                String soundType = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("NotifyAboutMentions.Sound.Type");
                float volume = (float) Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getDouble("NotifyAboutMentions.Sound.Volume");
                float pitch = (float) Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getDouble("NotifyAboutMentions.Sound.Pitch");
                player.playSound(player.getLocation(), Sound.valueOf(soundType.toUpperCase()), volume, pitch);
            }

        }
    }

}
