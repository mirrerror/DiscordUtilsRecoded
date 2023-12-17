package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DiscordSecondFactorDisableListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if(event.getChannelType() != ChannelType.PRIVATE) return;
        if(event.getUser().equals(Main.getInstance().getBot().getJda().getSelfUser())) return;
        if(!event.getChannel().asPrivateChannel().equals(event.getUser().openPrivateChannel().complete())) return;

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUserIdLong());
        if(!discordUtilsUser.isLinked()) return;

        long messageId = event.getMessageIdLong();
        UUID uuid = discordUtilsUser.getOfflinePlayer().getUniqueId();

        if(Main.getInstance().getBot().getSecondFactorDisablePlayers().containsKey(uuid)) {
            if(Main.getInstance().getBot().getSecondFactorDisablePlayers().get(uuid).getIdLong() == messageId) {

                if(event.getReaction().getEmoji().getName().equals("✅")) {

                    discordUtilsUser.setSecondFactor(false);

                    Main.getInstance().getBot().getSecondFactorDisablePlayers().remove(uuid);

                    if(discordUtilsUser.getOfflinePlayer().isOnline())
                        discordUtilsUser.getOfflinePlayer().getPlayer().sendMessage(Message.DISCORDUTILS_SECONDFACTOR_SUCCESSFUL.getText(true).replace("%status%", Message.DISABLED.getText()));

                }
                if(event.getReaction().getEmoji().getName().equals("❎")) {
                    Main.getInstance().getBot().getSecondFactorDisablePlayers().remove(uuid);
                    if(discordUtilsUser.getOfflinePlayer().isOnline()) Message.SECONDFACTOR_DISABLE_CANCELLED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);
                }

                event.getChannel().deleteMessageById(event.getMessageId()).queue();

            }
        }
    }

}
