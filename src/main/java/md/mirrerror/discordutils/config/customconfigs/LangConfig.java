package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LangConfig extends CustomConfig {
    public LangConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public Map<String, Object> initializeFields() {
        Map<String, Object> fields = new HashMap<>();

        fields.put("PREFIX", "&9DiscordUtils &7/&f");
        fields.put("INSUFFICIENT_PERMISSIONS", "Insufficient permissions.");
        fields.put("ACCOUNT_SUCCESSFULLY_LINKED", "Your account has been successfully verified.");
        fields.put("ACCOUNT_UNLINK_REQUEST_SENT", "Account unlink request has been sent. Check your DMs.");
        fields.put("ACCOUNT_SUCCESSFULLY_UNLINKED", "Account has been successfully unlinked.");
        fields.put("ACCOUNT_UNLINK_CONFIRMATION", "Somebody is trying to unlink your account. Choose if I should unlink your account or no. IP: %playerIp%.");
        fields.put("SECONDFACTOR_DISABLE_CONFIRMATION", "Somebody is trying to disable the 2FA on your account. Choose if I should do this or no. IP: %playerIp%.");
        fields.put("SECONDFACTOR_DISABLE_REQUEST_SENT", "2FA disable request has been sent. Check your DMs.");
        fields.put("SECONDFACTOR_DISABLE_CANCELLED", "2FA disable request has been cancelled.");
        fields.put("ACCOUNT_UNLINK_CANCELLED", "Account unlink request has been cancelled.");
        fields.put("INVALID_LINK_CODE", "Invalid code.");
        fields.put("SECONDFACTOR_REJECTED", "The account owner has rejected authorization of the account.");
        fields.put("SECONDFACTOR_TIME_TO_AUTHORIZE_HAS_EXPIRED", "You haven't managed to pass the 2FA. Please, try again.");
        fields.put("ACCOUNT_ALREADY_VERIFIED", "Your account is already verified.");
        fields.put("DISCORDUTILS_LINK_USAGE", "Usage: &b/discordutils link [code]");
        fields.put("DISCORDUTILS_GETDISCORD_USAGE", "Usage: &b/discordutils getdiscord [player]");
        fields.put("GETDISCORD_SUCCESSFUL", "Player's linked Discord account: &b%discord%");
        fields.put("SENDER_IS_NOT_A_PLAYER", "Only players can use this command.");
        fields.put("CONFIG_FILES_RELOADED", "Configuration files reloaded.");
        fields.put("ACCOUNT_IS_NOT_VERIFIED", "Your account has to be verified in order to do this.");
        fields.put("ENABLED", "&aenabled");
        fields.put("DISABLED", "&cdisabled");
        fields.put("DISCORDUTILS_SECONDFACTOR_SUCCESSFUL", "2FA: %status%");
        fields.put("SECONDFACTOR_NEEDED", "You have to authorize. Check your DMs in Discord.");
        fields.put("SECONDFACTOR_NEEDED_KICK", "You have to authorize. Check your DMs in Discord.");
        fields.put("VERIFICATION_NEEDED", "You have to verify your account. In our to do it, you have to use the '&b!link&f' in our Discord server.");
        fields.put("SECONDFACTOR_AUTHORIZED", "You have successfully authorized. Have fun! :)");
        fields.put("SECONDFACTOR_CODE_MESSAGE", "Your authorization code is: || %code% ||. Don't show it for anybody! IP: %playerIp%.");
        fields.put("SECONDFACTOR_REACTION_MESSAGE", "Somebody is trying to log in into your account. Choose if I should authorize him or no. IP: %playerIp%.");
        fields.put("SECONDFACTOR_DISABLED_REMINDER", "&cWarning!&f You have 2FA disabled. We advise you to make sure to enable it in order to make your account safer. In order to do it, use this command: &b/discordutils twofactor");
        fields.put("VERIFICATION_MESSAGE", "Your verification code has been sent to you. Check your DMs.");
        fields.put("VERIFICATION_CODE_MESSAGE", "Your verification code is: || %code% ||. Don't show it for anybody! In order to finish the verification process, use this command on the server: /discordutils link [code].");
        fields.put("CAN_NOT_SEND_MESSAGE", "I can't send you a DM. Probably, your DMs are closed.");
        fields.put("UNKNOWN_SUBCOMMAND", "Unknown subcommand.");
        fields.put("LINK_ALREADY_INITIATED", "You have already requested a verification code.");
        fields.put("UNLINK_ALREADY_INITIATED", "You have already started the unlink process. Check your DMs in Discord.");
        fields.put("DISCORDUTILS_SENDTODISCORD_USAGE", "Usage: &b/discordutils sendtodiscord [channel] [title] [color] [text]");
        fields.put("SENDTODISCORD_SENT_BY", "Sent by: %sender%");
        fields.put("DISCORDUTILS_SENDTODISCORD_SUCCESSFUL", "You have successfully sent the message to Discord.");
        fields.put("DISCORDUTILSADMIN_FORCEUNLINK_USAGE", "Usage: &b/discordutilsadmin forceunlink [player]");
        fields.put("DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_SENDER", "You have successfully unlinked &b%target%&f's linked account.");
        fields.put("DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_TARGET", "Your Discord account has been unlinked from your in-game account by &b%sender%&f.");
        fields.put("COMMAND_DISABLED", "This command is disabled by the server administration.");
        fields.put("INVALID_COLOR_VALUE", "Invalid color value.");
        fields.put("INVALID_PLAYER_NAME_OR_UNVERIFIED", "The given player is unverified or doesn't exist.");
        fields.put("PLUGIN_IS_NOT_READY_YET", "&cThe server is still loading, please, wait a bit and try joining again.");
        fields.put("MIGRATE_DATA_MANAGER_FAILED_TO_INITIALIZE", "The data manager that you want to migrate from failed to initialize. Check your settings.");
        fields.put("SUCCESSFULLY_MIGRATED", "You have successfully migrated all of your data to your current database.");
        fields.put("SOMETHING_WENT_WRONG_WHILE_MIGRATING", "Something went wrong while migrating all of your data to your current database.");
        fields.put("SECONDFACTOR_DISABLING_IS_NOT_AVAILABLE", "2FA disabling is not available for you.");
        fields.put("ONLINE", "Players online: **%online%**");
        fields.put("COMMAND_EXECUTED", "The command has been successfully executed.");
        fields.put("VOICE_INVITE_SENT", "Invite has been successfully sent.");
        fields.put("VOICE_INVITE", "Player &b%sender%&f invites all the online players for conversation in the voice channel. &bClick&f on this message to join the voice channel.");
        fields.put("VOICE_INVITE_HOVER", "&bClick&f to join the voice channel.");
        fields.put("SENDER_IS_NOT_IN_A_VOICE_CHANNEL", "You are not in a voice channel.");
        fields.put("UNKNOWN_ERROR", "Unknown error.");
        fields.put("COMMANDS_ARE_NOT_WORKING_IN_THIS_CHANNEL", "You can't use any commands in this channel.");
        fields.put("THIS_COMMAND_IS_BLACKLISTED", "This command is blacklisted.");
        fields.put("CHANNEL_DOES_NOT_EXIST", "Channel with this ID does not exist.");
        fields.put("TARGET_IS_OFFLINE", "The specified player is offline.");
        fields.put("BAN_SYNCHRONIZATION_SOURCE", "DiscordUtils");
        fields.put("BAN_SYNCHRONIZATION_REASON", "You have been banned by the ban synchronization system");
        fields.put("EMBED_SENT_BY", "Sent by: %sender%");
        fields.put("ERROR", "Error");
        fields.put("INFORMATION", "Information");
        fields.put("SUCCESSFULLY", "Success");
        fields.put("EMBED_FOOTER", "Bot by mirrerror");
        fields.put("WAITING_FOR_THE_RESPONSE", "Waiting for the response...");
        fields.put("DISCORD_TO_CHAT_FORMAT", "&b%user%&7 (&b%player%&7)&f: %message%");
        fields.put("STATS_FORMAT", Arrays.asList("Nickname: %player_name%",
                "Last join date: %player_last_join_date%"));
        fields.put("DISCORD_HELP", Arrays.asList("**Available commands:**",
                "**/link** — link your Discord account with your in-game account",
                "**/online** — check your current server online",
                "**/stats [player]** — check your or other player's stats (if you don't specify a player, it will output your stats)",
                "**/sudo [command]** — execute a server command as a console sender (admins only)",
                "**/embed [title] [color] [text]** — send an embed message (admins only)"));
        fields.put("HELP", Arrays.asList("&9DiscordUtils&f plugin help:",
                "&7-&b /du help&f - help command",
                "&7-&b /du link [code]&f - link your Discord account with your server account",
                "&7-&b /du unlink&f - unlink your Discord account from your server account",
                "&7-&b /du 2fa&f - enable/disable 2FA",
                "&7-&b /du sendtodiscord [channel] [title] [color] [text]&f - send an embed message to the Discord server",
                "&7-&b /du voiceinvite&f - invite all online players for conversation in the voice channel",
                "&7-&b /du getdiscord [player]&f - check the player's linked Discord account",
                "&7-&b /dua reload&f - reload the configuration files",
                "&7-&b /dua forceunlink [player]&f - unlink some player's Discord account from his in-game account",
                "&7-&b /dua stats&f - check the admin statistics",
                "&7-&b /dua migrate&f - migrate from the config/database to your current data manager (this option overwrites the existing users)"));
        fields.put("DISCORDUTILSADMIN_STATS_FORMAT", Arrays.asList("&9DiscordUtils&f plugin stats:",
                "&7-&f Linked players: &b%linkedPlayers%"));
        fields.put("CHAT_LOGGING_EMBED_TITLE", "Chat message from: %player%");
        fields.put("CHAT_LOGGING_EMBED_TEXT", "%message%");
        fields.put("JOIN_LOGGING_EMBED_TITLE", "Connection");
        fields.put("JOIN_LOGGING_EMBED_TEXT", "%player% has connected to the server");
        fields.put("QUIT_LOGGING_EMBED_TITLE", "Disconnection");
        fields.put("QUIT_LOGGING_EMBED_TEXT", "%player% has disconnected from the server");
        fields.put("DEATH_LOGGING_EMBED_TITLE", "Death");
        fields.put("DEATH_LOGGING_EMBED_TEXT", "%player% has died");
        fields.put("YES", "&ayes");
        fields.put("NO", "&cno");
        fields.put("NOT_AVAILABLE", "&cn/a");
        fields.put("LINK_SLASH_COMMAND_DESCRIPTION", "Links your in-game account with your server account.");
        fields.put("UNLINK_SLASH_COMMAND_DESCRIPTION", "Unlinks your in-game account from your server account.");
        fields.put("ONLINE_SLASH_COMMAND_DESCRIPTION", "Sends you the current online players on the server count.");
        fields.put("SUDO_SLASH_COMMAND_DESCRIPTION", "Allows you to execute console commands from Discord.");
        fields.put("SUDO_SLASH_COMMAND_FIRST_ARGUMENT_NAME", "command");
        fields.put("SUDO_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION", "The command that will be executed.");
        fields.put("EMBED_SLASH_COMMAND_DESCRIPTION", "Sends an embedded message with the given parameters in the current channel.");
        fields.put("EMBED_SLASH_COMMAND_FIRST_ARGUMENT_NAME", "title");
        fields.put("EMBED_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION", "The title of the embedded message.");
        fields.put("EMBED_SLASH_COMMAND_SECOND_ARGUMENT_NAME", "color");
        fields.put("EMBED_SLASH_COMMAND_SECOND_ARGUMENT_DESCRIPTION", "The color of the embedded message.");
        fields.put("EMBED_SLASH_COMMAND_THIRD_ARGUMENT_NAME", "text");
        fields.put("EMBED_SLASH_COMMAND_THIRD_ARGUMENT_DESCRIPTION", "The text of the embedded message.");
        fields.put("STATS_SLASH_COMMAND_DESCRIPTION", "Provides your''s or other player''s stats (if specified).");
        fields.put("STATS_SLASH_COMMAND_FIRST_ARGUMENT_NAME", "name");
        fields.put("STATS_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION", "Player''s name.");
        fields.put("HELP_SLASH_COMMAND_DESCRIPTION", "Sends you the list of the commands.");
        fields.put("ACCEPT", "Accept");
        fields.put("DECLINE", "Decline");

        return fields;
    }
}
