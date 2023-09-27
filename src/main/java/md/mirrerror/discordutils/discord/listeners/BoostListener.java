package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public class BoostListener extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event) {
        OffsetDateTime newTime = event.getNewTimeBoosted();
        User user = event.getUser();
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(user.getIdLong());

        if(!discordUtilsUser.isLinked()) return;

        if(newTime != null) {
            Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getStringList("CommandsAfterServerBoosting").forEach(command -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", discordUtilsUser.getOfflinePlayer().getName()).replace("%user%", user.getAsTag()));
            });
        } else {
            Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getStringList("CommandsAfterStoppingServerBoosting").forEach(command -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", discordUtilsUser.getOfflinePlayer().getName()).replace("%user%", user.getAsTag()));
            });
        }
    }

}
