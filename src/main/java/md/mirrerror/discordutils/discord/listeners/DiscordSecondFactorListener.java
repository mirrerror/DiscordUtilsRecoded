package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.SecondFactorSession;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class DiscordSecondFactorListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if(event.getChannelType() != ChannelType.PRIVATE) return;
        if(event.getUser().equals(Main.getInstance().getBot().getJda().getSelfUser())) return;
        if(!event.getPrivateChannel().equals(event.getUser().openPrivateChannel().complete())) return;

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUserIdLong());
        if(!discordUtilsUser.isLinked()) return;

        Player player = discordUtilsUser.getOfflinePlayer().getPlayer();
        if(player == null) return;

        long messageId = event.getMessageIdLong();
        UUID uuid = discordUtilsUser.getOfflinePlayer().getUniqueId();

        if(Main.getInstance().getBot().getSecondFactorPlayers().containsKey(uuid)) {
            if(messageId == Long.parseLong(Main.getInstance().getBot().getSecondFactorPlayers().get(uuid))) {
                if(event.getReaction().getReactionEmote().getName().equals("✅")) {
                    Main.getInstance().getBot().getSecondFactorPlayers().remove(uuid);
                    Message.SECONDFACTOR_AUTHORIZED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);
                    Main.getInstance().getBot().getSecondFactorSessions().put(uuid, new SecondFactorSession(StringUtils.remove(player.getAddress().getAddress().toString(), '/'),
                            LocalDateTime.now().plusSeconds(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLong("2FASessionTime"))));
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getStringList("CommandsAfter2FAPassing").forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", discordUtilsUser.getOfflinePlayer().getName())));
                    });
                }
                if(event.getReaction().getReactionEmote().getName().equals("❎")) {
                    Main.getInstance().getBot().getSecondFactorPlayers().remove(uuid);
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        player.kickPlayer(Message.SECONDFACTOR_REJECTED.getText().getText());
                        Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getStringList("CommandsAfter2FADeclining").forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", discordUtilsUser.getOfflinePlayer().getName())));
                    });
                }

                event.getChannel().deleteMessageById(event.getMessageId()).queue();
            }
        }
    }

}
