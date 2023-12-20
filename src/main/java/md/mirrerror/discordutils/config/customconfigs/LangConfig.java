package md.mirrerror.discordutils.config.customconfigs;

import java.util.Arrays;

public class LangConfig extends CustomConfig {
    public LangConfig(String fileName) {
        super(fileName);
    }

    @Override
    public void initializeFields() {
        getFileConfiguration().addDefault("PREFIX", "&9DiscordUtils &7/&f");
        getFileConfiguration().addDefault("INSUFFICIENT_PERMISSIONS", "Insufficient permissions.");
        getFileConfiguration().addDefault("ACCOUNT_SUCCESSFULLY_LINKED", "Your account has been successfully verified.");
        getFileConfiguration().addDefault("ACCOUNT_UNLINK_REQUEST_SENT", "Account unlink request has been sent. Check your DMs.");
        getFileConfiguration().addDefault("ACCOUNT_SUCCESSFULLY_UNLINKED", "Account has been successfully unlinked.");
        getFileConfiguration().addDefault("ACCOUNT_UNLINK_CONFIRMATION", "Somebody is trying to unlink your account. Choose if I should unlink your account or no. IP: %playerIp%.");
        getFileConfiguration().addDefault("SECONDFACTOR_DISABLE_CONFIRMATION", "Somebody is trying to disable the 2FA on your account. Choose if I should do this or no. IP: %playerIp%.");
        getFileConfiguration().addDefault("SECONDFACTOR_DISABLE_REQUEST_SENT", "2FA disable request has been sent. Check your DMs.");
        getFileConfiguration().addDefault("SECONDFACTOR_DISABLE_CANCELLED", "2FA disable request has been cancelled.");
        getFileConfiguration().addDefault("ACCOUNT_UNLINK_CANCELLED", "Account unlink request has been cancelled.");
        getFileConfiguration().addDefault("INVALID_LINK_CODE", "Invalid code.");
        getFileConfiguration().addDefault("SECONDFACTOR_REJECTED", "The account owner has rejected authorization of the account.");
        getFileConfiguration().addDefault("SECONDFACTOR_TIME_TO_AUTHORIZE_HAS_EXPIRED", "You haven't managed to pass the 2FA. Please, try again.");
        getFileConfiguration().addDefault("ACCOUNT_ALREADY_VERIFIED", "Your account is already verified.");
        getFileConfiguration().addDefault("DISCORDUTILS_LINK_USAGE", "Usage: &b/discordutils link [code]");
        getFileConfiguration().addDefault("DISCORDUTILS_GETDISCORD_USAGE", "Usage: &b/discordutils getdiscord [player]");
        getFileConfiguration().addDefault("GETDISCORD_SUCCESSFUL", "Player's linked Discord account: &b%discord%");
        getFileConfiguration().addDefault("SENDER_IS_NOT_A_PLAYER", "Only players can use this command.");
        getFileConfiguration().addDefault("CONFIG_FILES_RELOADED", "Configuration files reloaded.");
        getFileConfiguration().addDefault("ACCOUNT_IS_NOT_VERIFIED", "Your account has to be verified in order to do this.");
        getFileConfiguration().addDefault("ENABLED", "&aenabled");
        getFileConfiguration().addDefault("DISABLED", "&cdisabled");
        getFileConfiguration().addDefault("DISCORDUTILS_SECONDFACTOR_SUCCESSFUL", "2FA: %status%");
        getFileConfiguration().addDefault("SECONDFACTOR_NEEDED", "You have to authorize. Check your DMs in Discord.");
        getFileConfiguration().addDefault("VERIFICATION_NEEDED", "You have to verify your account. In our to do it, you have to use the '&b!link&f' in our Discord server.");
        getFileConfiguration().addDefault("SECONDFACTOR_AUTHORIZED", "You have successfully authorized. Have fun! :)");
        getFileConfiguration().addDefault("SECONDFACTOR_CODE_MESSAGE", "Your authorization code is: || %code% ||. Don't show it for anybody! IP: %playerIp%.");
        getFileConfiguration().addDefault("SECONDFACTOR_REACTION_MESSAGE", "Somebody is trying to log in into your account. Choose if I should authorize him or no. IP: %playerIp%.");
        getFileConfiguration().addDefault("SECONDFACTOR_DISABLED_REMINDER", "&cWarning!&f You have 2FA disabled. We advise you to make sure to enable it in order to make your account safer. In order to do it, use this command: &b/discordutils twofactor");
        getFileConfiguration().addDefault("VERIFICATION_MESSAGE", "Your verification code has been sent to you. Check your DMs.");
        getFileConfiguration().addDefault("VERIFICATION_CODE_MESSAGE", "Your verification code is: || %code% ||. Don't show it for anybody! In order to finish the verification process, use this command on the server: /discordutils link [code].");
        getFileConfiguration().addDefault("CAN_NOT_SEND_MESSAGE", "I can't send you a DM. Probably, your DMs are closed.");
        getFileConfiguration().addDefault("UNKNOWN_SUBCOMMAND", "Unknown subcommand.");
        getFileConfiguration().addDefault("LINK_ALREADY_INITIATED", "You have already requested a verification code.");
        getFileConfiguration().addDefault("UNLINK_ALREADY_INITIATED", "You have already started the unlink process. Check your DMs in Discord.");
        getFileConfiguration().addDefault("DISCORDUTILS_SENDTODISCORD_USAGE", "Usage: &b/discordutils sendtodiscord [title] [color] [text]");
        getFileConfiguration().addDefault("SENDTODISCORD_SENT_BY", "Sent by: %sender%");
        getFileConfiguration().addDefault("DISCORDUTILS_SENDTODISCORD_SUCCESSFUL", "You have successfully sent the message to Discord.");
        getFileConfiguration().addDefault("DISCORDUTILSADMIN_FORCEUNLINK_USAGE", "Usage: &b/discordutilsadmin forceunlink [player]");
        getFileConfiguration().addDefault("DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_SENDER", "You have successfully unlinked &b%target%&f's linked account.");
        getFileConfiguration().addDefault("DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_TARGET", "Your Discord account has been unlinked from your in-game account by &b%sender%&f.");
        getFileConfiguration().addDefault("COMMAND_DISABLED", "This command is disabled by the server administration.");
        getFileConfiguration().addDefault("INVALID_COLOR_VALUE", "Invalid color value.");
        getFileConfiguration().addDefault("INVALID_PLAYER_NAME_OR_UNVERIFIED", "The given player is unverified or doesn't exist.");
        getFileConfiguration().addDefault("PLUGIN_IS_NOT_READY_YET", "&cThe server is still loading, please, wait a bit and try joining again.");
        getFileConfiguration().addDefault("ONLINE", "Players online: **%online%**");
        getFileConfiguration().addDefault("COMMAND_EXECUTED", "The command has been successfully executed.");
        getFileConfiguration().addDefault("DISCORD_SUDO_USAGE", "Usage: !sudo [command]");
        getFileConfiguration().addDefault("DISCORD_EMBED_USAGE", "Usage: !embed [title] [color] [text]");
        getFileConfiguration().addDefault("VOICE_INVITE_SENT", "Invite has been successfully sent.");
        getFileConfiguration().addDefault("VOICE_INVITE", "Player &b%sender%&f invites all the online players for conversation in the voice channel. &bClick&f on this message to join the voice channel.");
        getFileConfiguration().addDefault("VOICE_INVITE_HOVER", "&bClick&f to join the voice channel.");
        getFileConfiguration().addDefault("SENDER_IS_NOT_IN_A_VOICE_CHANNEL", "You are not in a voice channel.");
        getFileConfiguration().addDefault("UNKNOWN_ERROR", "Unknown error.");
        getFileConfiguration().addDefault("COMMANDS_ARE_NOT_WORKING_IN_THIS_CHANNEL", "You can't use any commands in this channel.");
        getFileConfiguration().addDefault("THIS_COMMAND_IS_BLACKLISTED", "This command is blacklisted.");
        getFileConfiguration().addDefault("EMBED_SENT_BY", "Sent by: %sender%");
        getFileConfiguration().addDefault("ERROR", "Error");
        getFileConfiguration().addDefault("INFORMATION", "Information");
        getFileConfiguration().addDefault("SUCCESSFULLY", "Success");
        getFileConfiguration().addDefault("EMBED_FOOTER", "Bot by mirrerror");
        getFileConfiguration().addDefault("DISCORD_TO_CHAT_FORMAT", "&b%user%&7 (&b%player%&7)&f: %message%");
        getFileConfiguration().addDefault("STATS_FORMAT", Arrays.asList("Nickname: %player_name%",
                "Last join date: %player_last_join_date%"));
        getFileConfiguration().addDefault("DISCORD_HELP", Arrays.asList("**Available commands:**",
                "**!link** — link your Discord account with your in-game account",
                "**!online** — check your current server online",
                "**!stats [player]** — check your or other player's stats (if you don't specify a player, it will output your stats)",
                "**!sudo [command]** — execute a server command as a console sender (admins only)",
                "**!embed [title] [color] [text]** — send an embed message (admins only)"));
        getFileConfiguration().addDefault("HELP", Arrays.asList("&9DiscordUtils&f plugin help:",
                "&7-&b /du help&f - help command",
                "&7-&b /du link [code]&f - link your Discord account with your server account",
                "&7-&b /du unlink&f - unlink your Discord account from your server account",
                "&7-&b /du 2fa&f - enable/disable 2FA",
                "&7-&b /du sendtodiscord [title] [color] [text]&f - send an embed message to the Discord server",
                "&7-&b /du voiceinvite&f - invite all online players for conversation in the voice channel",
                "&7-&b /du getdiscord [player]&f - check the player's linked Discord account",
                "&7-&b /dua reload&f - reload the configuration files",
                "&7-&b /dua forceunlink [player]&f - unlink some player's Discord account from his in-game account",
                "&7-&b /dua stats&f - check the admin statistics"));
        getFileConfiguration().addDefault("DISCORDUTILSADMIN_STATS_FORMAT", Arrays.asList("&9DiscordUtils&f plugin stats:",
                "&7-&f Linked players: &b%linkedPlayers%"));
        getFileConfiguration().addDefault("CHAT_LOGGING_EMBED_TITLE", "Chat message from: %player%");
        getFileConfiguration().addDefault("CHAT_LOGGING_EMBED_TEXT", "%message%");
        getFileConfiguration().addDefault("JOIN_LOGGING_EMBED_TITLE", "Connection");
        getFileConfiguration().addDefault("JOIN_LOGGING_EMBED_TEXT", "%player% has connected to the server");
        getFileConfiguration().addDefault("QUIT_LOGGING_EMBED_TITLE", "Disconnection");
        getFileConfiguration().addDefault("QUIT_LOGGING_EMBED_TEXT", "%player% has disconnected from the server");
        getFileConfiguration().addDefault("DEATH_LOGGING_EMBED_TITLE", "Death");
        getFileConfiguration().addDefault("DEATH_LOGGING_EMBED_TEXT", "%player% has died");
        getFileConfiguration().addDefault("YES", "&ayes");
        getFileConfiguration().addDefault("NO", "&cno");
        getFileConfiguration().addDefault("NOT_AVAILABLE", "&cn/a");
        getFileConfiguration().addDefault("LINK_SLASH_COMMAND_DESCRIPTION", "Links your in-game account with your server account.");
        getFileConfiguration().addDefault("ONLINE_SLASH_COMMAND_DESCRIPTION", "Sends you the current online players on the server count.");
        getFileConfiguration().addDefault("SUDO_SLASH_COMMAND_DESCRIPTION", "Allows you to execute console commands from Discord.");
        getFileConfiguration().addDefault("SUDO_SLASH_COMMAND_FIRST_ARGUMENT_NAME", "command");
        getFileConfiguration().addDefault("SUDO_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION", "The command that will be executed.");
        getFileConfiguration().addDefault("EMBED_SLASH_COMMAND_DESCRIPTION", "Sends an embedded message with the given parameters in the current channel.");
        getFileConfiguration().addDefault("EMBED_SLASH_COMMAND_FIRST_ARGUMENT_NAME", "title");
        getFileConfiguration().addDefault("EMBED_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION", "The title of the embedded message.");
        getFileConfiguration().addDefault("EMBED_SLASH_COMMAND_SECOND_ARGUMENT_NAME", "color");
        getFileConfiguration().addDefault("EMBED_SLASH_COMMAND_SECOND_ARGUMENT_DESCRIPTION", "The color of the embedded message.");
        getFileConfiguration().addDefault("EMBED_SLASH_COMMAND_THIRD_ARGUMENT_NAME", "text");
        getFileConfiguration().addDefault("EMBED_SLASH_COMMAND_THIRD_ARGUMENT_DESCRIPTION", "The text of the embedded message.");
        getFileConfiguration().addDefault("STATS_SLASH_COMMAND_DESCRIPTION", "Provides your''s or other player''s stats (if specified).");
        getFileConfiguration().addDefault("STATS_SLASH_COMMAND_FIRST_ARGUMENT_NAME", "name");
        getFileConfiguration().addDefault("STATS_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION", "Player''s name.");
        getFileConfiguration().addDefault("HELP_SLASH_COMMAND_DESCRIPTION", "Sends you the list of the commands.");
        getFileConfiguration().addDefault("ACCEPT", "Accept");
        getFileConfiguration().addDefault("DECLINE", "Decline");
        getFileConfiguration().options().copyDefaults(true);
        getFileConfiguration().options().copyHeader(true);
        saveConfigFile();
    }
}
