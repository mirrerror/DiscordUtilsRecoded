package md.mirrerror.discordutils.config;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import net.dv8tion.jda.api.OnlineStatus;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotSettings {

    private static final FileConfiguration config = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration();

    public static final String BOT_TOKEN = config.getString("MainSettings.BotToken");
    public static final List<Long> BOT_COMMAND_TEXT_CHANNELS = config.getLongList("MainSettings.BotCommandTextChannels");
    public static final boolean ASYNC_BOT_LOADING = config.getBoolean("MainSettings.AsyncBotLoading");
    public static final OnlineStatus ONLINE_STATUS = OnlineStatus.fromKey(config.getString("MainSettings.OnlineStatus"));
    public static final boolean ACTIVITIES_ENABLED = config.getBoolean("Activities.Enabled");
    public static final long ACTIVITIES_UPDATE_DELAY = config.getLong("Activities.UpdateDelay");
    public static final String SUCCESSFUL_EMBED_COLOR = config.getString("EmbedMessages.SuccessfulEmbedColor");
    public static final String INFORMATION_EMBED_COLOR = config.getString("EmbedMessages.InformationEmbedColor");
    public static final String ERROR_EMBED_COLOR = config.getString("EmbedMessages.ErrorEmbedColor");
    public static final Map<Long, List<String>> GROUP_ROLES;
    static {
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
    public static final List<Long> ADMIN_ROLES = config.getLongList("Roles.AdminRoles");
    public static final boolean VERIFIED_ROLE_ENABLED = config.getBoolean("Roles.VerifiedRole.Enabled");
    public static final long VERIFIED_ROLE_ID = config.getLong("Roles.VerifiedRole.ID");
    public static final int SECOND_FACTOR_CODE_LENGTH = config.getInt("2FA.CodeLength");
    public static final DiscordUtilsBot.SecondFactorType SECOND_FACTOR_TYPE = DiscordUtilsBot.SecondFactorType.fromString(config.getString("2FA.Type"));
    public static final boolean SECOND_FACTOR_SESSIONS_ENABLED = config.getBoolean("2FA.SessionsEnabled");
    public static final long SECOND_FACTOR_SESSION_TIME = config.getLong("2FA.SessionTime");
    public static final long SECOND_FACTOR_TIME_TO_AUTHORIZE = config.getLong("2FA.TimeToAuthorize");
    public static final boolean DEFAULT_SECOND_FACTOR_VALUE = config.getBoolean("2FA.Default2FAValue");
    public static final List<Long> SECOND_FACTOR_FORCED_ROLES = config.getLongList("2FA.ForcedRoles");
    public static final List<String> SECOND_FACTOR_FORCED_GROUPS = config.getStringList("2FA.ForcedGroups");
    public static final boolean NOTIFY_ABOUT_DISABLED_SECOND_FACTOR = config.getBoolean("2FA.NotifyAboutDisabled2FA");
    public static final List<String> ALLOWED_COMMANDS_BEFORE_PASSING_SECOND_FACTOR = config.getStringList("2FA.AllowedCommandsBeforePassing2FA");
    public static final List<String> COMMANDS_AFTER_SECOND_FACTOR_DECLINING = config.getStringList("2FA.CommandsAfter2FADeclining");
    public static final List<String> COMMANDS_AFTER_SECOND_FACTOR_PASSING = config.getStringList("2FA.CommandsAfter2FAPassing");
    public static final boolean FORCE_LINKING_ENABLED = config.getBoolean("Linking.ForceLinking");
    public static final List<String> COMMANDS_AFTER_LINKING = config.getStringList("Linking.CommandsAfterLinking");
    public static final List<String> COMMANDS_AFTER_UNLINKING = config.getStringList("Linking.CommandsAfterUnlinking");
    public static final List<String> COMMANDS_AFTER_SERVER_BOOSTING = config.getStringList("CommandsAfterServerBoosting");
    public static final List<String> COMMANDS_AFTER_STOPPING_SERVER_BOOSTING = config.getStringList("Boosting.CommandsAfterStoppingServerBoosting");
    public static final boolean ROLES_SYNCHRONIZATION_ENABLED = config.getBoolean("RolesSynchronization.Enabled");
    public static final boolean ROLES_SYNCHRONIZATION_ASSIGN_ONLY_PRIMARY_GROUP = config.getBoolean("RolesSynchronization.AssignOnlyPrimaryGroup");
    public static final boolean DELAYED_ROLES_CHECK_ENABLED = config.getBoolean("RolesSynchronization.DelayedRolesCheck.Enabled");
    public static final long DELAYED_ROLES_CHECK_DELAY = config.getLong("RolesSynchronization.DelayedRolesCheck.Delay");
    public static final boolean NAMES_SYNCHRONIZATION_ENABLED = config.getBoolean("NamesSynchronization.Enabled");
    public static final String NAMES_SYNCHRONIZATION_FORMAT = config.getString("NamesSynchronization.NamesSyncFormat");
    public static final boolean DELAYED_NAMES_CHECK_ENABLED = config.getBoolean("NamesSynchronization.DelayedNamesCheck.Enabled");
    public static final long DELAYED_NAMES_CHECK_DELAY = config.getLong("NamesSynchronization.DelayedNamesCheck.Delay");
    public static final boolean GUILD_VOICE_REWARDS_ENABLED = config.getBoolean("GuildVoiceRewards.Enabled");
    public static final long GUILD_VOICE_REWARDS_TIME = config.getLong("GuildVoiceRewards.Time");
    public static final String GUILD_VOICE_REWARDS_REWARD = config.getString("GuildVoiceRewards.Reward");
    public static final List<Long> GUILD_VOICE_REWARDS_BLACKLISTED_CHANNELS = config.getLongList("GuildVoiceRewards.BlacklistedChannels");
    public static final int GUILD_VOICE_REWARDS_MIN_MEMBERS = config.getInt("GuildVoiceRewards.MinMembers");
    public static final boolean MESSAGES_CHANNEL_ENABLED = config.getBoolean("MessagesChannel.Enabled");
    public static final long MESSAGES_CHANNEL_ID = config.getLong("MessagesChannel.ID");
    public static final boolean SERVER_ACTIVITY_LOGGING_ENABLED = config.getBoolean("ServerActivityLogging.Enabled");
    public static final long SERVER_ACTIVITY_LOGGING_CHANNEL_ID = config.getLong("ServerActivityLogging.ChannelID");
    public static final String SERVER_ACTIVITY_LOGGING_JOIN_EMBED_COLOR = config.getString("ServerActivityLogging.JoinEmbedColor");
    public static final String SERVER_ACTIVITY_LOGGING_QUIT_EMBED_COLOR = config.getString("ServerActivityLogging.QuitEmbedColor");
    public static final String SERVER_ACTIVITY_LOGGING_DEATH_EMBED_COLOR = config.getString("ServerActivityLogging.DeathEmbedColor");
    public static final String SERVER_ACTIVITY_LOGGING_CHAT_EMBED_COLOR = config.getString("ServerActivityLogging.ChatEmbedColor");
    public static final boolean CONSOLE_ENABLED = config.getBoolean("Console.Enabled");
    public static final boolean CONSOLE_CLEAR_ON_EVERY_INIT = config.getBoolean("Console.ClearOnEveryInit");
    public static final long CONSOLE_CHANNEL_ID = config.getLong("Console.ChannelID");
    public static final int CONSOLE_DELETE_MESSAGES_DELAY = config.getInt("Console.DeleteMessagesDelay");
    public static final List<String> CONSOLE_BLACKLISTED_COMMANDS = config.getStringList("Console.BlacklistedCommands");
    public static final boolean CHAT_ENABLED = config.getBoolean("Chat.Enabled");
    public static final String CHAT_WEBHOOK_URL = config.getString("Chat.WebhookUrl");
    public static final long CHAT_CHANNEL_ID = config.getLong("Chat.ChannelID");
    public static final boolean NOTIFY_ABOUT_MENTIONS_ENABLED = config.getBoolean("NotifyAboutMentions.Enabled");
    public static final List<Long> NOTIFY_ABOUT_MENTIONS_BLACKLISTED_CHANNELS = config.getLongList("NotifyAboutMentions.BlacklistedChannels");
    public static final boolean NOTIFY_ABOUT_MENTIONS_TITLE_ENABLED = config.getBoolean("NotifyAboutMentions.Title.Enabled");
    public static final int NOTIFY_ABOUT_MENTIONS_TITLE_FADE_IN = config.getInt("NotifyAboutMentions.Title.FadeIn");
    public static final int NOTIFY_ABOUT_MENTIONS_TITLE_STAY = config.getInt("NotifyAboutMentions.Title.Stay");
    public static final int NOTIFY_ABOUT_MENTIONS_TITLE_FADE_OUT = config.getInt("NotifyAboutMentions.Title.FadeOut");
    public static final String NOTIFY_ABOUT_MENTIONS_TITLE_TITLE = config.getString("NotifyAboutMentions.Title.Title");
    public static final String NOTIFY_ABOUT_MENTIONS_TITLE_SUBTITLE = config.getString("NotifyAboutMentions.Title.Subtitle");
    public static final boolean NOTIFY_ABOUT_MENTIONS_MESSAGE_ENABLED = config.getBoolean("NotifyAboutMentions.Message.Enabled");
    public static final String NOTIFY_ABOUT_MENTIONS_MESSAGE_TEXT = config.getString("NotifyAboutMentions.Message.Text");
    public static final boolean NOTIFY_ABOUT_MENTIONS_SOUND_ENABLED = config.getBoolean("NotifyAboutMentions.Sound.Enabled");
    public static final String NOTIFY_ABOUT_MENTIONS_SOUND_TYPE = config.getString("NotifyAboutMentions.Sound.Type");
    public static final float NOTIFY_ABOUT_MENTIONS_SOUND_VOLUME = (float) config.getDouble("NotifyAboutMentions.Sound.Volume");
    public static final float NOTIFY_ABOUT_MENTIONS_SOUND_PITCH = (float) config.getDouble("NotifyAboutMentions.Sound.Pitch");

}
