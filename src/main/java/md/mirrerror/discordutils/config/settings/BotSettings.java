package md.mirrerror.discordutils.config.settings;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import net.dv8tion.jda.api.OnlineStatus;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotSettings {

    private static final FileConfiguration config = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration();

    public final String BOT_TOKEN = config.getString("MainSettings.BotToken");
    public final List<Long> BOT_COMMAND_TEXT_CHANNELS = config.getLongList("MainSettings.BotCommandTextChannels");
    public final boolean ASYNC_BOT_LOADING = config.getBoolean("MainSettings.AsyncBotLoading");
    public final OnlineStatus ONLINE_STATUS = OnlineStatus.fromKey(config.getString("MainSettings.OnlineStatus"));
    public final boolean ACTIVITIES_ENABLED = config.getBoolean("Activities.Enabled");
    public final long ACTIVITIES_UPDATE_DELAY = config.getLong("Activities.UpdateDelay");
    public final Color SUCCESSFUL_EMBED_COLOR = Color.decode(config.getString("EmbedMessages.SuccessfulEmbedColor"));
    public final Color INFORMATION_EMBED_COLOR = Color.decode(config.getString("EmbedMessages.InformationEmbedColor"));
    public final Color ERROR_EMBED_COLOR = Color.decode(config.getString("EmbedMessages.ErrorEmbedColor"));
    public final Map<Long, List<String>> GROUP_ROLES;
    {
        GROUP_ROLES = new HashMap<>();

        for(String role : config.getConfigurationSection("Roles.GroupRoles").getKeys(false)) {
            try {
                if(config.isList("Roles.GroupRoles." + role)) {
                    GROUP_ROLES.put(Long.parseLong(role), config.getStringList("Roles.GroupRoles." + role));
                } else {
                    List<String> groups = new ArrayList<>();
                    groups.add(config.getString("Roles.GroupRoles." + role));
                    GROUP_ROLES.put(Long.parseLong(role), groups);
                }
            } catch (NumberFormatException ignored) {
                Main.getInstance().getLogger().warning("Found an unknown role ID in the group roles section: " + role + ". Skipping it...");
            }
        }
    }
    public final List<Long> ADMIN_ROLES = config.getLongList("Roles.AdminRoles");
    public final boolean VERIFIED_ROLE_ENABLED = config.getBoolean("Roles.VerifiedRole.Enabled");
    public final long VERIFIED_ROLE_ID = config.getLong("Roles.VerifiedRole.ID");
    public final int SECOND_FACTOR_CODE_LENGTH = config.getInt("2FA.CodeLength");
    public final DiscordUtilsBot.SecondFactorType SECOND_FACTOR_TYPE = DiscordUtilsBot.SecondFactorType.fromString(config.getString("2FA.Type"));
    public final boolean SECOND_FACTOR_BLOCK_PLAYER_JOIN = config.getBoolean("2FA.BlockPlayerJoin");
    public final boolean SECOND_FACTOR_SESSIONS_ENABLED = config.getBoolean("2FA.SessionsEnabled");
    public final long SECOND_FACTOR_SESSION_TIME = config.getLong("2FA.SessionTime");
    public final long SECOND_FACTOR_TIME_TO_AUTHORIZE = config.getLong("2FA.TimeToAuthorize");
    public final boolean DEFAULT_SECOND_FACTOR_VALUE = config.getBoolean("2FA.Default2FAValue");
    public final List<Long> SECOND_FACTOR_FORCED_ROLES = config.getLongList("2FA.ForcedRoles");
    public final List<String> SECOND_FACTOR_FORCED_GROUPS = config.getStringList("2FA.ForcedGroups");
    public final boolean NOTIFY_ABOUT_DISABLED_SECOND_FACTOR = config.getBoolean("2FA.NotifyAboutDisabled2FA");
    public final List<String> ALLOWED_COMMANDS_BEFORE_PASSING_SECOND_FACTOR = config.getStringList("2FA.AllowedCommandsBeforePassing2FA");
    public final List<String> COMMANDS_AFTER_SECOND_FACTOR_DECLINING = config.getStringList("2FA.CommandsAfter2FADeclining");
    public final List<String> COMMANDS_AFTER_SECOND_FACTOR_PASSING = config.getStringList("2FA.CommandsAfter2FAPassing");
    public final boolean FORCE_LINKING_ENABLED = config.getBoolean("Linking.ForceLinking");
    public final List<String> COMMANDS_AFTER_LINKING = config.getStringList("Linking.CommandsAfterLinking");
    public final List<String> COMMANDS_AFTER_UNLINKING = config.getStringList("Linking.CommandsAfterUnlinking");
    public final List<String> COMMANDS_AFTER_LEAVING_GUILD = config.getStringList("Linking.CommandsAfterLeavingGuild");
    public final List<String> COMMANDS_AFTER_SERVER_BOOSTING = config.getStringList("CommandsAfterServerBoosting");
    public final List<String> COMMANDS_AFTER_STOPPING_SERVER_BOOSTING = config.getStringList("Boosting.CommandsAfterStoppingServerBoosting");
    public final boolean ROLES_SYNCHRONIZATION_ENABLED = config.getBoolean("RolesSynchronization.Enabled");
    public final boolean ROLES_SYNCHRONIZATION_ASSIGN_ONLY_PRIMARY_GROUP = config.getBoolean("RolesSynchronization.AssignOnlyPrimaryGroup");
    public final boolean DELAYED_ROLES_CHECK_ENABLED = config.getBoolean("RolesSynchronization.DelayedRolesCheck.Enabled");
    public final long DELAYED_ROLES_CHECK_DELAY = config.getLong("RolesSynchronization.DelayedRolesCheck.Delay");
    public final boolean NAMES_SYNCHRONIZATION_ENABLED = config.getBoolean("NamesSynchronization.Enabled");
    public final String NAMES_SYNCHRONIZATION_FORMAT = config.getString("NamesSynchronization.NamesSyncFormat");
    public final boolean DELAYED_NAMES_CHECK_ENABLED = config.getBoolean("NamesSynchronization.DelayedNamesCheck.Enabled");
    public final long DELAYED_NAMES_CHECK_DELAY = config.getLong("NamesSynchronization.DelayedNamesCheck.Delay");
    public final boolean BANS_SYNCHRONIZATION_ENABLED = config.getBoolean("BansSynchronization.Enabled");
    public final boolean BANS_SYNCHRONIZATION_MINECRAFT_TO_DISCORD_ENABLED = config.getBoolean("BansSynchronization.MinecraftToDiscord");
    public final boolean BANS_SYNCHRONIZATION_DISCORD_TO_MINECRAFT_ENABLED = config.getBoolean("BansSynchronization.DiscordToMinecraft");
    public final boolean GUILD_VOICE_REWARDS_ENABLED = config.getBoolean("GuildVoiceRewards.Enabled");
    public final long GUILD_VOICE_REWARDS_TIME = config.getLong("GuildVoiceRewards.Time");
    public final String GUILD_VOICE_REWARDS_REWARD = config.getString("GuildVoiceRewards.Reward");
    public final List<Long> GUILD_VOICE_REWARDS_BLACKLISTED_CHANNELS = config.getLongList("GuildVoiceRewards.BlacklistedChannels");
    public final int GUILD_VOICE_REWARDS_MIN_MEMBERS = config.getInt("GuildVoiceRewards.MinMembers");
    public final boolean SERVER_ACTIVITY_LOGGING_ENABLED = config.getBoolean("ServerActivityLogging.Enabled");
    public final long SERVER_ACTIVITY_LOGGING_CHANNEL_ID = config.getLong("ServerActivityLogging.ChannelID");
    public final boolean SERVER_ACTIVITY_LOGGING_JOIN_ENABLED = config.getBoolean("ServerActivityLogging.Join.Enabled");
    public final Color SERVER_ACTIVITY_LOGGING_JOIN_EMBED_COLOR = Color.decode(config.getString("ServerActivityLogging.Join.EmbedColor"));
    public final boolean SERVER_ACTIVITY_LOGGING_QUIT_ENABLED = config.getBoolean("ServerActivityLogging.Quit.Enabled");
    public final Color SERVER_ACTIVITY_LOGGING_QUIT_EMBED_COLOR = Color.decode(config.getString("ServerActivityLogging.Quit.EmbedColor"));
    public final boolean SERVER_ACTIVITY_LOGGING_DEATH_ENABLED = config.getBoolean("ServerActivityLogging.Death.Enabled");
    public final Color SERVER_ACTIVITY_LOGGING_DEATH_EMBED_COLOR = Color.decode(config.getString("ServerActivityLogging.Death.EmbedColor"));
    public final boolean SERVER_ACTIVITY_LOGGING_CHAT_ENABLED = config.getBoolean("ServerActivityLogging.Chat.Enabled");
    public final Color SERVER_ACTIVITY_LOGGING_CHAT_EMBED_COLOR = Color.decode(config.getString("ServerActivityLogging.Chat.EmbedColor"));
    public final boolean CONSOLE_ENABLED = config.getBoolean("Console.Enabled");
    public final boolean CONSOLE_CLEAR_ON_EVERY_INIT = config.getBoolean("Console.ClearOnEveryInit");
    public final long CONSOLE_CHANNEL_ID = config.getLong("Console.ChannelID");
    public final int CONSOLE_DELETE_MESSAGES_DELAY = config.getInt("Console.DeleteMessagesDelay");
    public final List<String> CONSOLE_BLACKLISTED_COMMANDS = config.getStringList("Console.BlacklistedCommands");
    public final boolean CHAT_ENABLED = config.getBoolean("Chat.Enabled");
    public final String CHAT_WEBHOOK_URL = config.getString("Chat.WebhookUrl");
    public final long CHAT_CHANNEL_ID = config.getLong("Chat.ChannelID");
    public final boolean NOTIFY_ABOUT_MENTIONS_ENABLED = config.getBoolean("NotifyAboutMentions.Enabled");
    public final List<Long> NOTIFY_ABOUT_MENTIONS_BLACKLISTED_CHANNELS = config.getLongList("NotifyAboutMentions.BlacklistedChannels");
    public final boolean NOTIFY_ABOUT_MENTIONS_TITLE_ENABLED = config.getBoolean("NotifyAboutMentions.Title.Enabled");
    public final int NOTIFY_ABOUT_MENTIONS_TITLE_FADE_IN = config.getInt("NotifyAboutMentions.Title.FadeIn");
    public final int NOTIFY_ABOUT_MENTIONS_TITLE_STAY = config.getInt("NotifyAboutMentions.Title.Stay");
    public final int NOTIFY_ABOUT_MENTIONS_TITLE_FADE_OUT = config.getInt("NotifyAboutMentions.Title.FadeOut");
    public final String NOTIFY_ABOUT_MENTIONS_TITLE_TITLE = config.getString("NotifyAboutMentions.Title.Title");
    public final String NOTIFY_ABOUT_MENTIONS_TITLE_SUBTITLE = config.getString("NotifyAboutMentions.Title.Subtitle");
    public final boolean NOTIFY_ABOUT_MENTIONS_MESSAGE_ENABLED = config.getBoolean("NotifyAboutMentions.Message.Enabled");
    public final String NOTIFY_ABOUT_MENTIONS_MESSAGE_TEXT = config.getString("NotifyAboutMentions.Message.Text");
    public final boolean NOTIFY_ABOUT_MENTIONS_SOUND_ENABLED = config.getBoolean("NotifyAboutMentions.Sound.Enabled");
    public final String NOTIFY_ABOUT_MENTIONS_SOUND_TYPE = config.getString("NotifyAboutMentions.Sound.Type");
    public final float NOTIFY_ABOUT_MENTIONS_SOUND_VOLUME = (float) config.getDouble("NotifyAboutMentions.Sound.Volume");
    public final float NOTIFY_ABOUT_MENTIONS_SOUND_PITCH = (float) config.getDouble("NotifyAboutMentions.Sound.Pitch");

}
