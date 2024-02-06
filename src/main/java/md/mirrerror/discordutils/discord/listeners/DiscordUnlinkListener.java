package md.mirrerror.discordutils.discord.listeners;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.events.custom.AccountUnlinkEvent;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class DiscordUnlinkListener extends ListenerAdapter {

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

        long messageId = event.getMessageIdLong();
        UUID uuid = discordUtilsUser.getOfflinePlayer().getUniqueId();

        if(bot.getUnlinkPlayers().containsKey(uuid)) {
            if(bot.getUnlinkPlayers().get(uuid).getIdLong() == messageId) {

                if(event.getComponentId().equals("accept")) {
                    bot.unAssignVerifiedRole(discordUtilsUser.getUser().getIdLong());

                    bot.getUnlinkPlayers().remove(uuid);
                    if(discordUtilsUser.getOfflinePlayer().isOnline()) Message.ACCOUNT_SUCCESSFULLY_UNLINKED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        botSettings.COMMANDS_AFTER_UNLINKING.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", discordUtilsUser.getOfflinePlayer().getName())));
                    });

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        AccountUnlinkEvent accountUnlinkEvent = new AccountUnlinkEvent(discordUtilsUser, bot);
                        Bukkit.getPluginManager().callEvent(accountUnlinkEvent);
                    });

                    discordUtilsUser.unregister();
                }
                if(event.getComponentId().equals("decline")) {
                    bot.getUnlinkPlayers().remove(uuid);
                    if(discordUtilsUser.getOfflinePlayer().isOnline()) Message.ACCOUNT_UNLINK_CANCELLED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);
                }

                event.getChannel().deleteMessageById(event.getMessageId()).queue();

            }
        }
    }

}
