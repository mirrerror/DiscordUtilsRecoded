package md.mirrerror.discordutils.discord.listeners;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class DiscordSecondFactorDisableListener extends ListenerAdapter {

    private final DiscordUtilsBot bot;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getChannelType() != ChannelType.PRIVATE) return;
        if(event.getUser().equals(bot.getJda().getSelfUser())) return;
        if(!event.getChannel().asPrivateChannel().equals(event.getUser().openPrivateChannel().complete())) return;

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUser().getIdLong());
        if(!discordUtilsUser.isLinked()) return;

        long messageId = event.getMessageIdLong();
        UUID uuid = discordUtilsUser.getOfflinePlayer().getUniqueId();

        if(bot.getSecondFactorDisablePlayers().containsKey(uuid)) {
            if(bot.getSecondFactorDisablePlayers().get(uuid).getIdLong() == messageId) {

                if (event.getComponentId().equals("accept")) {

                    discordUtilsUser.setSecondFactor(false);

                    bot.getSecondFactorDisablePlayers().remove(uuid);

                    if (discordUtilsUser.getOfflinePlayer().isOnline())
                        discordUtilsUser.getOfflinePlayer().getPlayer().sendMessage(Message.DISCORDUTILS_SECONDFACTOR_SUCCESSFUL.getText(true).replace("%status%", Message.DISABLED.getText()));

                } else if (event.getComponentId().equals("decline")) {

                    bot.getSecondFactorDisablePlayers().remove(uuid);
                    if (discordUtilsUser.getOfflinePlayer().isOnline())
                        Message.SECONDFACTOR_DISABLE_CANCELLED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);

                }

                event.getChannel().deleteMessageById(event.getMessageId()).queue();
            }
        }

    }

}
