package md.mirrerror.discordutils.discord.listeners;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.discord.SecondFactorSession;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class DiscordSecondFactorListener extends ListenerAdapter {

    private final Plugin plugin;
    private final DiscordUtilsBot bot;
    private final BotSettings botSettings;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getChannelType() != ChannelType.PRIVATE) return;
        if(event.getUser().equals(bot.getJda().getSelfUser())) return;
        if(!event.getChannel().asPrivateChannel().equals(event.getUser().openPrivateChannel().complete())) return;

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUser().getIdLong());
        if(!discordUtilsUser.isLinked()) return;

        Player player = discordUtilsUser.getOfflinePlayer().getPlayer();
        if(player == null) return;

        long messageId = event.getMessageIdLong();
        UUID uuid = discordUtilsUser.getOfflinePlayer().getUniqueId();

        if(bot.getSecondFactorPlayers().containsKey(uuid)) {
            if(messageId == Long.parseLong(bot.getSecondFactorPlayers().get(uuid))) {
                if(event.getComponentId().equals("accept")) {
                    bot.getSecondFactorPlayers().remove(uuid);
                    Message.SECONDFACTOR_AUTHORIZED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);
                    bot.getSecondFactorSessions().put(uuid, new SecondFactorSession(StringUtils.remove(player.getAddress().getAddress().toString(), '/'),
                            LocalDateTime.now().plusSeconds(botSettings.SECOND_FACTOR_SESSION_TIME)));
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        botSettings.COMMANDS_AFTER_SECOND_FACTOR_PASSING.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", discordUtilsUser.getOfflinePlayer().getName())));
                    });
                }
                if(event.getComponentId().equals("decline")) {
                    bot.getSecondFactorPlayers().remove(uuid);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.kickPlayer(Message.SECONDFACTOR_REJECTED.getText());
                        botSettings.COMMANDS_AFTER_SECOND_FACTOR_DECLINING.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", discordUtilsUser.getOfflinePlayer().getName())));
                    });
                }

                event.getChannel().deleteMessageById(event.getMessageId()).queue();
            }
        }
    }

}
