package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VoiceRewardsListener extends ListenerAdapter {

    private final Map<Long, BukkitTask> rewardTimers = new HashMap<>();
    private static final Map<Long, Long> voiceTime = new HashMap<>();

    /*@Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        if(!Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("GuildVoiceRewards.Enabled")) return;
        if(Main.getInstance().getBot().getVoiceRewardsBlacklistedChannels().contains(event.getChannelJoined().getIdLong())) return;

        Member member = event.getMember();
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getMember().getUser().getIdLong());

        if(!discordUtilsUser.isLinked()) return;

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            GuildVoiceState voiceState = member.getVoiceState();
            if(voiceState == null) return;
            if(voiceState.isSelfDeafened() || voiceState.isSelfMuted()) return;
            if(event.getChannelJoined() == null) return;
            if(event.getChannelJoined().getMembers().size() < Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getInt("GuildVoiceRewards.MinMembers")) return;

            long id = member.getIdLong();

            if(voiceTime.containsKey(id)) voiceTime.put(id, voiceTime.get(id)+1L);
            else voiceTime.put(id, 1L);

            long minTime = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLong("GuildVoiceRewards.Time");
            long time = voiceTime.get(id);

            if(time >= minTime) {
                String command = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("GuildVoiceRewards.Reward")
                        .replace("%player%", discordUtilsUser.getOfflinePlayer().getName());
                Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                voiceTime.put(id, 0L);
            }
        }, 0L, 20L);

        rewardTimers.put(member.getIdLong(), bukkitTask);
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        if(!Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("GuildVoiceRewards.Enabled")) return;
        if(Main.getInstance().getBot().getVoiceRewardsBlacklistedChannels().contains(event.getChannelLeft().getIdLong())) return;

        Member member = event.getMember();
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getMember().getUser().getIdLong());
        long memberId = member.getIdLong();

        if(!discordUtilsUser.isLinked()) return;
        if(!voiceTime.containsKey(memberId)) return;

        if(rewardTimers.containsKey(memberId)) rewardTimers.get(memberId).cancel();
        rewardTimers.remove(memberId);
        voiceTime.remove(memberId);
    }*/

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if(event.getChannelJoined() != null) {
            if(!Main.getInstance().getBotSettings().GUILD_VOICE_REWARDS_ENABLED) return;
            if(Main.getInstance().getBot().getVoiceRewardsBlacklistedChannels().contains(event.getChannelJoined().getIdLong())) return;

            Member member = event.getMember();
            DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getMember().getUser().getIdLong());

            if(!discordUtilsUser.isLinked()) return;

            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                GuildVoiceState voiceState = member.getVoiceState();
                if(voiceState == null) return;
                if(voiceState.isSelfDeafened() || voiceState.isSelfMuted()) return;
                if(event.getChannelJoined() == null) return;
                if(event.getChannelJoined().getMembers().size() < Main.getInstance().getBotSettings().GUILD_VOICE_REWARDS_MIN_MEMBERS) return;

                long id = member.getIdLong();

                if(voiceTime.containsKey(id)) voiceTime.put(id, voiceTime.get(id)+1L);
                else voiceTime.put(id, 1L);

                long minTime = Main.getInstance().getBotSettings().GUILD_VOICE_REWARDS_TIME;
                long time = voiceTime.get(id);

                if(time >= minTime) {
                    String command = Main.getInstance().getBotSettings().GUILD_VOICE_REWARDS_REWARD.replace("%player%", discordUtilsUser.getOfflinePlayer().getName());
                    Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                    voiceTime.put(id, 0L);
                }
            }, 0L, 20L);

            rewardTimers.put(member.getIdLong(), bukkitTask);
        }

        if(event.getChannelLeft() != null) {
            if(!Main.getInstance().getBotSettings().GUILD_VOICE_REWARDS_ENABLED) return;
            if(Main.getInstance().getBot().getVoiceRewardsBlacklistedChannels().contains(event.getChannelLeft().getIdLong())) return;

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
