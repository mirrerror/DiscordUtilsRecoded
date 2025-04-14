package md.mirrerror.discordutils.utils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.EmbedMessagesBuilder;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.util.List;

public class DiscordValidator {

    private static final EmbedMessagesBuilder EMBED_MESSAGES_BUILDER = new EmbedMessagesBuilder(Main.getInstance().getBotSettings());

    public static boolean validateLinkedUser(MessageChannelUnion messageChannelUnion, DiscordUtilsUser discordUtilsUser) {
        if(!discordUtilsUser.isLinked()) {
            messageChannelUnion.sendMessageEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText()).build()).queue();
            return false;
        }
        return true;
    }

    public static boolean validateLinkedUser(InteractionHook interactionHook, DiscordUtilsUser discordUtilsUser) {
        if(!discordUtilsUser.isLinked()) {
            interactionHook.editOriginalEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText()).build()).queue();
            return false;
        }
        return true;
    }

    public static boolean validateNotLinkedUser(MessageChannelUnion messageChannelUnion, DiscordUtilsUser discordUtilsUser) {
        if(discordUtilsUser.isLinked()) {
            messageChannelUnion.sendMessageEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.ACCOUNT_ALREADY_VERIFIED.getText()).build()).queue();
            return false;
        }
        return true;
    }

    public static boolean validateNotLinkedUser(InteractionHook interactionHook, DiscordUtilsUser discordUtilsUser) {
        if(discordUtilsUser.isLinked()) {
            interactionHook.editOriginalEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.ACCOUNT_ALREADY_VERIFIED.getText()).build()).queue();
            return false;
        }
        return true;
    }

    public static boolean validateColor(MessageChannelUnion messageChannelUnion, Color color) {
        if(color == null) {
            messageChannelUnion.sendMessageEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.INVALID_COLOR_VALUE.getText()).build()).queue();
            return false;
        }
        return true;
    }

    public static boolean validateColor(InteractionHook interactionHook, Color color) {
        if(color == null) {
            interactionHook.editOriginalEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.INVALID_COLOR_VALUE.getText()).build()).queue();
            return false;
        }
        return true;
    }

    public static boolean validateCommandChannel(MessageChannelUnion messageChannelUnion) {
        List<Long> botCommandTextChannels = Main.getInstance().getBotSettings().BOT_COMMAND_TEXT_CHANNELS;
        if(!botCommandTextChannels.isEmpty()) {
            if(!botCommandTextChannels.contains(messageChannelUnion.getIdLong())) {
                messageChannelUnion.sendMessageEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.COMMANDS_ARE_NOT_WORKING_IN_THIS_CHANNEL.getText()).build()).queue();
                return false;
            }
        }
        return true;
    }

    public static boolean validateCommandChannel(SlashCommandInteractionEvent event) {
        List<Long> botCommandTextChannels = Main.getInstance().getBotSettings().BOT_COMMAND_TEXT_CHANNELS;
        if(!botCommandTextChannels.isEmpty()) {
            if(!botCommandTextChannels.contains(event.getChannel().getIdLong())) {
                event.replyEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.COMMANDS_ARE_NOT_WORKING_IN_THIS_CHANNEL.getText()).build()).queue();
                return false;
            }
        }
        return true;
    }

    public static boolean validateLinkAvailability(MessageChannelUnion messageChannelUnion, User user) {
        if(Main.getInstance().getBot().getLinkCodes().containsValue(user.getIdLong())) {
            messageChannelUnion.sendMessageEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.LINK_ALREADY_INITIATED.getText()).build()).queue();
            return false;
        }
        return true;
    }

    public static boolean validateLinkAvailability(InteractionHook interactionHook, User user) {
        if(Main.getInstance().getBot().getLinkCodes().containsValue(user.getIdLong())) {
            interactionHook.editOriginalEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.LINK_ALREADY_INITIATED.getText()).build()).queue();
            return false;
        }
        return true;
    }

    public static boolean validateAdminPermissions(MessageChannelUnion messageChannelUnion, Guild guild, DiscordUtilsUser discordUtilsUser) {
        if(!discordUtilsUser.isAdmin(guild)) {
            messageChannelUnion.sendMessageEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText()).build()).queue();
            return false;
        }
        return true;
    }

    public static boolean validateAdminPermissions(InteractionHook interactionHook, Guild guild, DiscordUtilsUser discordUtilsUser) {
        if(!discordUtilsUser.isAdmin(guild)) {
            interactionHook.editOriginalEmbeds(EMBED_MESSAGES_BUILDER.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText()).build()).queue();
            return false;
        }
        return true;
    }
}
