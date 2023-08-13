package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DiscordUnlinkListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if(event.getChannelType() != ChannelType.PRIVATE) return;
        if(event.getUser().equals(Main.getInstance().getBot().getJda().getSelfUser())) return;
        if(!event.getPrivateChannel().equals(event.getUser().openPrivateChannel().complete())) return;

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUserIdLong());
        if(!discordUtilsUser.isLinked()) return;

        long messageId = event.getMessageIdLong();
        UUID uuid = discordUtilsUser.getOfflinePlayer().getUniqueId();

        if(Main.getInstance().getBot().getUnlinkPlayers().containsKey(uuid)) {
            if(Main.getInstance().getBot().getUnlinkPlayers().get(uuid).getIdLong() == messageId) {

                if(event.getReaction().getReactionEmote().getName().equals("✅")) {
                    Main.getInstance().getBot().getJda().getGuilds().forEach(guild -> {
                        Role verifiedRole = Main.getInstance().getBot().getVerifiedRole();
                        Member member = guild.getMemberById(discordUtilsUser.getUser().getIdLong());
                        if(verifiedRole != null && member != null) if(member.getRoles().contains(verifiedRole)) {
                            try {
                                guild.removeRoleFromMember(member, verifiedRole).queue();
                            } catch (HierarchyException ignored) {}
                        }
                    });

                    Main.getInstance().getBot().getUnlinkPlayers().remove(uuid);
                    if(discordUtilsUser.getOfflinePlayer().isOnline()) Message.ACCOUNT_SUCCESSFULLY_UNLINKED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getStringList("CommandsAfterUnlink").forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", discordUtilsUser.getOfflinePlayer().getName())));
                    });

                    discordUtilsUser.unregister();
                }
                if(event.getReaction().getReactionEmote().getName().equals("❎")) {
                    Main.getInstance().getBot().getUnlinkPlayers().remove(uuid);
                    if(discordUtilsUser.getOfflinePlayer().isOnline()) Message.ACCOUNT_UNLINK_CANCELLED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);
                }

                event.getChannel().deleteMessageById(event.getMessageId()).queue();

            }
        }
    }

}