package md.mirrerror.discordutils.discord.listeners;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class BoostListener extends ListenerAdapter {

    private final Plugin plugin;
    private final BotSettings botSettings;

    @Override
    public void onGuildMemberUpdate(@NotNull GuildMemberUpdateEvent event) {
        User user = event.getUser();
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(user.getIdLong());

        if(!discordUtilsUser.isLinked()) return;

        OffsetDateTime timeBoosted = event.getMember().getTimeBoosted();
        OffsetDateTime lastBoostingTime = discordUtilsUser.getLastBoostingTime();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if(!event.getMember().isBoosting() && discordUtilsUser.getLastBoostingTime() != null) {

                botSettings.COMMANDS_AFTER_STOPPING_SERVER_BOOSTING.forEach(command ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", discordUtilsUser.getOfflinePlayer().getName()).replace("%user%", user.getName())));

            } else if(timeBoosted != null && lastBoostingTime != null && timeBoosted.isAfter(lastBoostingTime)) {

                botSettings.COMMANDS_AFTER_SERVER_BOOSTING.forEach(command ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", discordUtilsUser.getOfflinePlayer().getName()).replace("%user%", user.getName())));
                discordUtilsUser.setLastBoostingTime(OffsetDateTime.now());

            }
        });
    }

}
