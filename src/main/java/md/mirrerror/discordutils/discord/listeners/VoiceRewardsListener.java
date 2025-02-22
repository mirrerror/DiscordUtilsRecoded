package md.mirrerror.discordutils.discord.listeners;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class VoiceRewardsListener extends ListenerAdapter {

    private final Plugin plugin;
    private final DiscordUtilsBot bot;
    private final BotSettings botSettings;

    private final Map<Long, BukkitTask> rewardTimers = new HashMap<>();
    private static final Map<Long, Long> voiceTime = new HashMap<>();

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if(event.getChannelJoined() != null) {
            if(!botSettings.GUILD_VOICE_REWARDS_ENABLED) return;
            if(bot.getVoiceRewardsBlacklistedChannels().contains(event.getChannelJoined().getIdLong())) return;

            Member member = event.getMember();
            DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getMember().getUser().getIdLong());

            if(!discordUtilsUser.isLinked()) return;

            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                GuildVoiceState voiceState = member.getVoiceState();
                if(voiceState == null) return;
                if(voiceState.isSelfDeafened() || voiceState.isSelfMuted()) return;
                if(event.getChannelJoined() == null) return;
                if(event.getChannelJoined().getMembers().size() < botSettings.GUILD_VOICE_REWARDS_MIN_MEMBERS) return;

                long id = member.getIdLong();

                if(voiceTime.containsKey(id)) voiceTime.put(id, voiceTime.get(id)+1L);
                else voiceTime.put(id, 1L);

                long minTime = botSettings.GUILD_VOICE_REWARDS_TIME;
                long time = voiceTime.get(id);

                if(time >= minTime) {
                    botSettings.GUILD_VOICE_REWARDS_REWARD.forEach(entry -> {
                        String command = entry.replace("%player%", discordUtilsUser.getOfflinePlayer().getName());
                        Bukkit.getScheduler().callSyncMethod(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                        voiceTime.put(id, 0L);
                    });
                }
            }, 0L, 20L);

            rewardTimers.put(member.getIdLong(), bukkitTask);
        }

        if(event.getChannelLeft() != null) {
            if(!botSettings.GUILD_VOICE_REWARDS_ENABLED) return;
            if(bot.getVoiceRewardsBlacklistedChannels().contains(event.getChannelLeft().getIdLong())) return;

            Member member = event.getMember();
            DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getMember().getUser().getIdLong());
            long memberId = member.getIdLong();

            if(!discordUtilsUser.isLinked()) return;
            if(!voiceTime.containsKey(memberId)) return;

            if(rewardTimers.containsKey(memberId)) rewardTimers.get(memberId).cancel();
            rewardTimers.remove(memberId);
            voiceTime.remove(memberId);
        }
    }

}
