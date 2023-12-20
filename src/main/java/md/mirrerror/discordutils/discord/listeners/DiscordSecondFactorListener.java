package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.SecondFactorSession;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class DiscordSecondFactorListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getChannelType() != ChannelType.PRIVATE) return;
        if(event.getUser().equals(Main.getInstance().getBot().getJda().getSelfUser())) return;
        if(!event.getChannel().asPrivateChannel().equals(event.getUser().openPrivateChannel().complete())) return;

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUser().getIdLong());
        if(!discordUtilsUser.isLinked()) return;

        Player player = discordUtilsUser.getOfflinePlayer().getPlayer();
        if(player == null) return;

        long messageId = event.getMessageIdLong();
        UUID uuid = discordUtilsUser.getOfflinePlayer().getUniqueId();

        if(Main.getInstance().getBot().getSecondFactorPlayers().containsKey(uuid)) {
            if(messageId == Long.parseLong(Main.getInstance().getBot().getSecondFactorPlayers().get(uuid))) {
                if(event.getComponentId().equals("accept")) {
                    Main.getInstance().getBot().getSecondFactorPlayers().remove(uuid);
                    Message.SECONDFACTOR_AUTHORIZED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);
                    Main.getInstance().getBot().getSecondFactorSessions().put(uuid, new SecondFactorSession(StringUtils.remove(player.getAddress().getAddress().toString(), '/'),
                            LocalDateTime.now().plusSeconds(Main.getInstance().getBotSettings().SECOND_FACTOR_SESSION_TIME)));
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        Main.getInstance().getBotSettings().COMMANDS_AFTER_SECOND_FACTOR_PASSING.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", discordUtilsUser.getOfflinePlayer().getName())));
                    });
                }
                if(event.getComponentId().equals("decline")) {
                    Main.getInstance().getBot().getSecondFactorPlayers().remove(uuid);
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        player.kickPlayer(Message.SECONDFACTOR_REJECTED.getText());
                        Main.getInstance().getBotSettings().COMMANDS_AFTER_SECOND_FACTOR_DECLINING.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", discordUtilsUser.getOfflinePlayer().getName())));
                    });
                }

                event.getChannel().deleteMessageById(event.getMessageId()).queue();
            }
        }
    }

}
