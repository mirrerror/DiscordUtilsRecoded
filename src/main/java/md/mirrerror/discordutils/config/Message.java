package md.mirrerror.discordutils.config;

import md.mirrerror.discordutils.Main;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public enum Message {

    PREFIX(false),
    INSUFFICIENT_PERMISSIONS(false),
    ACCOUNT_SUCCESSFULLY_LINKED(false),
    ACCOUNT_UNLINK_REQUEST_SENT(false),
    ACCOUNT_SUCCESSFULLY_UNLINKED(false),
    ACCOUNT_UNLINK_CONFIRMATION(false),
    ACCOUNT_UNLINK_CANCELLED(false),
    INVALID_LINK_CODE(false),
    SECONDFACTOR_REJECTED(false),
    SECONDFACTOR_TIME_TO_AUTHORIZE_HAS_EXPIRED(false),
    ACCOUNT_ALREADY_VERIFIED(false),
    DISCORDUTILS_LINK_USAGE(false),
    DISCORDUTILS_GETDISCORD_USAGE(false),
    GETDISCORD_SUCCESSFUL(false),
    SENDER_IS_NOT_A_PLAYER(false),
    CONFIG_FILES_RELOADED(false),
    ACCOUNT_IS_NOT_VERIFIED(false),
    ENABLED(false),
    DISABLED(false),
    DISCORDUTILS_SECONDFACTOR_SUCCESSFUL(false),
    SECONDFACTOR_NEEDED(false),
    VERIFICATION_NEEDED(false),
    SECONDFACTOR_AUTHORIZED(false),
    SECONDFACTOR_CODE_MESSAGE(false),
    SECONDFACTOR_REACTION_MESSAGE(false),
    SECONDFACTOR_DISABLED_REMINDER(false),
    VERIFICATION_MESSAGE(false),
    VERIFICATION_CODE_MESSAGE(false),
    CAN_NOT_SEND_MESSAGE(false),
    UNKNOWN_SUBCOMMAND(false),
    LINK_ALREADY_INITIATED(false),
    UNLINK_ALREADY_INITIATED(false),
    DISCORDUTILS_SENDTODISCORD_USAGE(false),
    SENDTODISCORD_SENT_BY(false),
    DISCORDUTILS_SENDTODISCORD_SUCCESSFUL(false),
    DISCORDUTILSADMIN_FORCEUNLINK_USAGE(false),
    DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_SENDER(false),
    DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_TARGET(false),
    COMMAND_DISABLED(false),
    INVALID_COLOR_VALUE(false),
    INVALID_PLAYER_NAME_OR_UNVERIFIED(false),
    ONLINE(false),
    COMMAND_EXECUTED(false),
    DISCORD_SUDO_USAGE(false),
    DISCORD_EMBED_USAGE(false),
    VOICE_INVITE_SENT(false),
    VOICE_INVITE(false),
    VOICE_INVITE_HOVER(false),
    SENDER_IS_NOT_IN_A_VOICE_CHANNEL(false),
    UNKNOWN_ERROR(false),
    COMMANDS_ARE_NOT_WORKING_IN_THIS_CHANNEL(false),
    THIS_COMMAND_IS_BLACKLISTED(false),
    EMBED_SENT_BY(false),
    ERROR(false),
    INFORMATION(false),
    SUCCESSFULLY(false),
    EMBED_FOOTER(false),
    DISCORD_TO_CHAT_FORMAT(false),
    STATS_FORMAT(true),
    DISCORD_HELP(true),
    HELP(true),
    DISCORDUTILSADMIN_STATS_FORMAT(true),

    CHAT_LOGGING_EMBED_TITLE(false),
    CHAT_LOGGING_EMBED_TEXT(false),
    JOIN_LOGGING_EMBED_TITLE(false),
    JOIN_LOGGING_EMBED_TEXT(false),
    QUIT_LOGGING_EMBED_TITLE(false),
    QUIT_LOGGING_EMBED_TEXT(false),
    DEATH_LOGGING_EMBED_TITLE(false),
    DEATH_LOGGING_EMBED_TEXT(false),

    YES(false),
    NO(false),
    NOT_AVAILABLE(false),

    LINK_SLASH_COMMAND_DESCRIPTION(false),
    ONLINE_SLASH_COMMAND_DESCRIPTION(false),
    SUDO_SLASH_COMMAND_DESCRIPTION(false),
    SUDO_SLASH_COMMAND_FIRST_ARGUMENT_NAME(false),
    SUDO_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION(false),
    EMBED_SLASH_COMMAND_DESCRIPTION(false),
    EMBED_SLASH_COMMAND_FIRST_ARGUMENT_NAME(false),
    EMBED_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION(false),
    EMBED_SLASH_COMMAND_SECOND_ARGUMENT_NAME(false),
    EMBED_SLASH_COMMAND_SECOND_ARGUMENT_DESCRIPTION(false),
    EMBED_SLASH_COMMAND_THIRD_ARGUMENT_NAME(false),
    EMBED_SLASH_COMMAND_THIRD_ARGUMENT_DESCRIPTION(false),
    STATS_SLASH_COMMAND_DESCRIPTION(false),
    STATS_SLASH_COMMAND_FIRST_ARGUMENT_NAME(false),
    STATS_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION(false),
    HELP_SLASH_COMMAND_DESCRIPTION(false);

    private boolean isList;

    Message(boolean isList) {
        this.isList = isList;
    }

    public TextComponent getText() {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfigManager().getLang().getFileConfiguration().getString(String.valueOf(this))));
    }

    public TextComponent getText(boolean addPrefix) {
        if(addPrefix) {
            return new TextComponent(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfigManager().getLang().getFileConfiguration().getString(String.valueOf(PREFIX))
                    + " " + Main.getInstance().getConfigManager().getLang().getFileConfiguration().getString(String.valueOf(this))));
        }
        return this.getText();
    }

    public List<TextComponent> getTextList() {
        List<TextComponent> stringList = new ArrayList<>();
        Main.getInstance().getConfigManager().getLang().getFileConfiguration().getStringList(String.valueOf(this)).forEach(s -> stringList.add(new TextComponent(ChatColor.translateAlternateColorCodes('&', s))));
        return stringList;
    }

    public List<TextComponent> getTextList(boolean addPrefix) {
        List<TextComponent> stringList = new ArrayList<>();
        if(addPrefix) {
            for(String s : Main.getInstance().getConfigManager().getLang().getFileConfiguration().getStringList(String.valueOf(this))) {
                stringList.add(new TextComponent(new ComponentBuilder(PREFIX.getText()).append(" " + s).create()));
            }
        } else return this.getTextList();
        return stringList;
    }

    public void send(CommandSender commandSender) {
        if(isList) getTextList().forEach(msg -> commandSender.spigot().sendMessage(msg));
        else commandSender.spigot().sendMessage(getText());
    }

    public void send(CommandSender commandSender, boolean addPrefix) {
        if(addPrefix) {
            if(isList) getTextList().forEach(msg -> commandSender.spigot().sendMessage(new TextComponent(new ComponentBuilder(PREFIX.getText()).append(" ").append(msg).create())));
            else commandSender.spigot().sendMessage(new TextComponent(new ComponentBuilder(PREFIX.getText()).append(" ").append(getText()).create()));
        } else {
            send(commandSender);
        }
    }

    public boolean isList() {
        return isList;
    }

}