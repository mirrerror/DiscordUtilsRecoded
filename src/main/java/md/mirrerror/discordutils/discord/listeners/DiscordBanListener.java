package md.mirrerror.discordutils.discord.listeners;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class DiscordBanListener implements Listener {

    private final DiscordUtilsBot bot;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(event.getPlayer().getUniqueId());

        if(!discordUtilsUser.isLinked()) return;

        BanList banList = Bukkit.getBanList(BanList.Type.NAME);

        bot.getJda().getGuilds().forEach(guild -> {
            Member member = guild.getMember(discordUtilsUser.getUser());

            if(member == null) return;

            guild.retrieveBan(discordUtilsUser.getUser()).queue(
                    success -> {
                        if(!banList.isBanned(discordUtilsUser.getOfflinePlayer().getName())) {
                            banList.addBan(discordUtilsUser.getOfflinePlayer().getName(), "Banned by Discord", null, "DiscordUtils");
                            event.getPlayer().kickPlayer("Banned by Discord");
                        }
                    },
                    failure -> {
                        if(banList.isBanned(discordUtilsUser.getOfflinePlayer().getName()))
                            banList.pardon(discordUtilsUser.getOfflinePlayer().getName());
                    }
            );
        });
    }

//    @Override
//    public void onGuildBan(@NotNull GuildBanEvent event) {
//        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUser().getIdLong());
//        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
//
//        if(discordUtilsUser.isLinked() && !banList.isBanned(discordUtilsUser.getOfflinePlayer().getName()))
//            banList.addBan(discordUtilsUser.getOfflinePlayer().getName(), "Banned by Discord", null, "DiscordUtils");
//    }
//
//    @Override
//    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
//        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUser().getIdLong());
//        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
//
//        if(discordUtilsUser.isLinked() && banList.isBanned(discordUtilsUser.getOfflinePlayer().getName()))
//            banList.pardon(discordUtilsUser.getOfflinePlayer().getName());
//    }

}
