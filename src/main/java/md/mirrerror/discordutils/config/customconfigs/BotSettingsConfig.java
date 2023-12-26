package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;

public class BotSettingsConfig extends CustomConfig {
    public BotSettingsConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public void initializeFields() {
        getFileConfiguration().addDefault("MainSettings.BotToken", "");
        getFileConfiguration().addDefault("MainSettings.BotCommandTextChannels", Collections.emptyList());
        getFileConfiguration().addDefault("MainSettings.AsyncBotLoading", true);
        getFileConfiguration().addDefault("MainSettings.OnlineStatus", "online");
        getFileConfiguration().addDefault("Activities.Enabled", true);
        getFileConfiguration().addDefault("Activities.UpdateDelay", 10);
        getFileConfiguration().addDefault("Activities.1.Type", "playing");
        getFileConfiguration().addDefault("Activities.1.Text", "Minecraft");
        getFileConfiguration().addDefault("EmbedMessages.SuccessfulEmbedColor", "#0AAC00");
        getFileConfiguration().addDefault("EmbedMessages.InformationEmbedColor", "#ECC846");
        getFileConfiguration().addDefault("EmbedMessages.ErrorEmbedColor", "#A80000");
        getFileConfiguration().addDefault("Roles.GroupRoles", new HashMap<>());
        getFileConfiguration().addDefault("Roles.AdminRoles", Collections.emptyList());
        getFileConfiguration().addDefault("Roles.VerifiedRole.Enabled", false);
        getFileConfiguration().addDefault("Roles.VerifiedRole.ID", -1);
        getFileConfiguration().addDefault("2FA.CodeLength", 10);
        getFileConfiguration().addDefault("2FA.Type", "reaction");
        getFileConfiguration().addDefault("2FA.BlockPlayerJoin", false);
        getFileConfiguration().addDefault("2FA.SessionsEnabled", true);
        getFileConfiguration().addDefault("2FA.SessionTime", 900);
        getFileConfiguration().addDefault("2FA.TimeToAuthorize", 30);
        getFileConfiguration().addDefault("2FA.Default2FAValue", false);
        getFileConfiguration().addDefault("2FA.ForcedRoles", Collections.emptyList());
        getFileConfiguration().addDefault("2FA.ForcedGroups", Collections.emptyList());
        getFileConfiguration().addDefault("2FA.NotifyAboutDisabled2FA", true);
        getFileConfiguration().addDefault("2FA.AllowedCommandsBeforePassing2FA", Collections.emptyList());
        getFileConfiguration().addDefault("2FA.ActionsAfterFailing2FA", new HashMap<>());
        getFileConfiguration().addDefault("2FA.CommandsAfter2FADeclining", Collections.emptyList());
        getFileConfiguration().addDefault("2FA.CommandsAfter2FAPassing", Collections.emptyList());
        getFileConfiguration().addDefault("Linking.ForceLinking", false);
        getFileConfiguration().addDefault("Linking.CommandsAfterLinking", Collections.emptyList());
        getFileConfiguration().addDefault("Linking.CommandsAfterUnlinking", Collections.emptyList());
        getFileConfiguration().addDefault("Boosting.CommandsAfterServerBoosting", Collections.emptyList());
        getFileConfiguration().addDefault("Boosting.CommandsAfterStoppingServerBoosting", Collections.emptyList());
        getFileConfiguration().addDefault("RolesSynchronization.Enabled", true);
        getFileConfiguration().addDefault("RolesSynchronization.AssignOnlyPrimaryGroup", true);
        getFileConfiguration().addDefault("RolesSynchronization.DelayedRolesCheck.Enabled", true);
        getFileConfiguration().addDefault("RolesSynchronization.DelayedRolesCheck.Delay", 30);
        getFileConfiguration().addDefault("NamesSynchronization.Enabled", true);
        getFileConfiguration().addDefault("NamesSynchronization.NamesSyncFormat", "%player%");
        getFileConfiguration().addDefault("NamesSynchronization.DelayedNamesCheck.Enabled", true);
        getFileConfiguration().addDefault("NamesSynchronization.DelayedNamesCheck.Delay", 30);
        getFileConfiguration().addDefault("GuildVoiceRewards.Enabled", true);
        getFileConfiguration().addDefault("GuildVoiceRewards.Time", 300);
        getFileConfiguration().addDefault("GuildVoiceRewards.Reward", "eco give %player% 100");
        getFileConfiguration().addDefault("GuildVoiceRewards.BlacklistedChannels", Collections.emptyList());
        getFileConfiguration().addDefault("GuildVoiceRewards.MinMembers", 1);
        getFileConfiguration().addDefault("ServerActivityLogging.Enabled", false);
        getFileConfiguration().addDefault("ServerActivityLogging.ChannelID", -1);
        getFileConfiguration().addDefault("ServerActivityLogging.JoinEmbedColor", "#8de113");
        getFileConfiguration().addDefault("ServerActivityLogging.QuitEmbedColor", "#f34520");
        getFileConfiguration().addDefault("ServerActivityLogging.DeathEmbedColor", "#f34520");
        getFileConfiguration().addDefault("ServerActivityLogging.ChatEmbedColor", "#e8d725");
        getFileConfiguration().addDefault("Console.Enabled", false);
        getFileConfiguration().addDefault("Console.ClearOnEveryInit", true);
        getFileConfiguration().addDefault("Console.ChannelID", -1);
        getFileConfiguration().addDefault("Console.DeleteMessagesDelay", 10);
        getFileConfiguration().addDefault("Console.BlacklistedCommands", Collections.emptyList());
        getFileConfiguration().addDefault("Chat.Enabled", false);
        getFileConfiguration().addDefault("Chat.WebhookUrl", "");
        getFileConfiguration().addDefault("Chat.ChannelID", -1);
        getFileConfiguration().addDefault("NotifyAboutMentions.Enabled", true);
        getFileConfiguration().addDefault("NotifyAboutMentions.BlacklistedChannels", Collections.emptyList());
        getFileConfiguration().addDefault("NotifyAboutMentions.Title.Enabled", true);
        getFileConfiguration().addDefault("NotifyAboutMentions.Title.FadeIn", 3);
        getFileConfiguration().addDefault("NotifyAboutMentions.Title.Stay", 50);
        getFileConfiguration().addDefault("NotifyAboutMentions.Title.FadeOut", 3);
        getFileConfiguration().addDefault("NotifyAboutMentions.Title.Title", "&bNew mention!");
        getFileConfiguration().addDefault("NotifyAboutMentions.Title.Subtitle", "&fCheck your &9Discord&f.");
        getFileConfiguration().addDefault("NotifyAboutMentions.Message.Enabled", false);
        getFileConfiguration().addDefault("NotifyAboutMentions.Message.Text", "&9DiscordUtils &7/&f You have been mentioned in &9Discord&f.");
        getFileConfiguration().addDefault("NotifyAboutMentions.Sound.Enabled", true);
        getFileConfiguration().addDefault("NotifyAboutMentions.Sound.Type", "ENTITY_EXPERIENCE_ORB_PICKUP");
        getFileConfiguration().addDefault("NotifyAboutMentions.Sound.Volume", 1);
        getFileConfiguration().addDefault("NotifyAboutMentions.Sound.Pitch", 1);
        getFileConfiguration().addDefault("CustomTriggers.InGameEvents", new HashMap<>());
        getFileConfiguration().addDefault("InfoChannels", new HashMap<>());
        getFileConfiguration().options().copyDefaults(true);
        getFileConfiguration().options().copyHeader(true);
        saveConfigFile();
    }
}
