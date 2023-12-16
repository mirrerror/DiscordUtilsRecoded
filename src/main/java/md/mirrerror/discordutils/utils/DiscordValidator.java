package md.mirrerror.discordutils.utils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.awt.*;
import java.util.List;

public class DiscordValidator {

    private static final EmbedManager embedManager = new EmbedManager();

    public static boolean validateLinkedUser(MessageChannelUnion messageChannelUnion, DiscordUtilsUser discordUtilsUser) {
        if(!discordUtilsUser.isLinked()) {
            messageChannelUnion.sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText())).queue();
            return false;
        }
        return true;
    }

    public static boolean validateNotLinkedUser(MessageChannelUnion messageChannelUnion, DiscordUtilsUser discordUtilsUser) {
        if(discordUtilsUser.isLinked()) {
            messageChannelUnion.sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_ALREADY_VERIFIED.getText())).queue();
            return false;
        }
        return true;
    }

    public static boolean validateColor(MessageChannelUnion messageChannelUnion, Color color) {
        if(color == null) {
            messageChannelUnion.sendMessageEmbeds(embedManager.errorEmbed(Message.INVALID_COLOR_VALUE.getText())).queue();
            return false;
        }
        return true;
    }

    public static boolean validateCommandChannel(MessageChannelUnion messageChannelUnion) {
        List<Long> botCommandTextChannels = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLongList("BotCommandTextChannels");
        if(!botCommandTextChannels.isEmpty()) {
            if(!Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLongList("BotCommandTextChannels").contains(messageChannelUnion.getIdLong())) {
                messageChannelUnion.sendMessageEmbeds(embedManager.errorEmbed(Message.COMMANDS_ARE_NOT_WORKING_IN_THIS_CHANNEL.getText())).queue();
                return false;
            }
        }
        return true;
    }

    public static boolean validateLinkAvailability(MessageChannelUnion messageChannelUnion, User user) {
        if(Main.getInstance().getBot().getLinkCodes().containsValue(user.getIdLong())) {
            messageChannelUnion.sendMessageEmbeds(embedManager.errorEmbed(Message.LINK_ALREADY_INITIATED.getText())).queue();
            return false;
        }
        return true;
    }

    public static boolean validateAdminPermissions(MessageChannelUnion messageChannelUnion, Guild guild, DiscordUtilsUser discordUtilsUser) {
        if(!discordUtilsUser.isAdmin(guild)) {
            messageChannelUnion.sendMessageEmbeds(embedManager.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText())).queue();
            return false;
        }
        return true;
    }
}
