package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BotSettingsConfig extends CustomConfig {
    public BotSettingsConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public Map<String, Object> initializeFields() {
        Map<String, Object> fields = new HashMap<>();

        fields.put("MainSettings.BotToken", "");
        fields.put("MainSettings.BotCommandTextChannels", Collections.emptyList());
        fields.put("MainSettings.AsyncBotLoading", true);
        fields.put("MainSettings.OnlineStatus", "online");
        fields.put("Activities.Enabled", true);
        fields.put("Activities.UpdateDelay", 10);
        fields.put("Activities.1.Type", "playing");
        fields.put("Activities.1.Text", "Minecraft");
        fields.put("EmbedMessages.SuccessfulEmbedColor", "#0AAC00");
        fields.put("EmbedMessages.InformationEmbedColor", "#ECC846");
        fields.put("EmbedMessages.ErrorEmbedColor", "#A80000");
        fields.put("Roles.GroupRoles", new HashMap<>());
        fields.put("Roles.RolesToGroups", new HashMap<>());
        fields.put("Roles.AdminRoles", Collections.emptyList());
        fields.put("Roles.VerifiedRole.Enabled", false);
        fields.put("Roles.VerifiedRole.ID", -1);
        fields.put("2FA.CodeLength", 10);
        fields.put("2FA.Type", "reaction");
        fields.put("2FA.BlockPlayerJoin", false);
        fields.put("2FA.SessionsEnabled", true);
        fields.put("2FA.SessionTime", 900);
        fields.put("2FA.TimeToAuthorize", 30);
        fields.put("2FA.Default2FAValue", false);
        fields.put("2FA.ForcedRoles", Collections.emptyList());
        fields.put("2FA.ForcedGroups", Collections.emptyList());
        fields.put("2FA.NotifyAboutDisabled2FA", true);
        fields.put("2FA.AllowedCommandsBeforePassing2FA", Collections.emptyList());
        fields.put("2FA.ActionsAfterFailing2FA", new HashMap<>());
        fields.put("2FA.CommandsAfter2FADeclining", Collections.emptyList());
        fields.put("2FA.CommandsAfter2FAPassing", Collections.emptyList());
        fields.put("Linking.ForceLinking", false);
        fields.put("Linking.CommandsAfterLinking", Collections.emptyList());
        fields.put("Linking.CommandsAfterUnlinking", Collections.emptyList());
        fields.put("Linking.CommandsAfterLeavingGuild", Collections.emptyList());
        fields.put("Boosting.CommandsAfterServerBoosting", Collections.emptyList());
        fields.put("Boosting.CommandsAfterStoppingServerBoosting", Collections.emptyList());
        fields.put("RolesSynchronization.Enabled", true);
        fields.put("RolesSynchronization.AssignOnlyPrimaryGroup", true);
        fields.put("RolesSynchronization.AssignGroupsOnlyByPrimaryRole", true);
        fields.put("RolesSynchronization.DelayedRolesCheck.Enabled", true);
        fields.put("RolesSynchronization.DelayedRolesCheck.Delay", 30);
        fields.put("NamesSynchronization.Enabled", true);
        fields.put("NamesSynchronization.NamesSyncFormat", "%player%");
        fields.put("NamesSynchronization.DelayedNamesCheck.Enabled", true);
        fields.put("NamesSynchronization.DelayedNamesCheck.Delay", 30);
        fields.put("BansSynchronization.Enabled", false);
        fields.put("BansSynchronization.MinecraftToDiscord", false);
        fields.put("BansSynchronization.DiscordToMinecraft", false);
        fields.put("GuildVoiceRewards.Enabled", true);
        fields.put("GuildVoiceRewards.Time", 300);
        fields.put("GuildVoiceRewards.Reward", Collections.singletonList("eco give %player% 100"));
        fields.put("GuildVoiceRewards.BlacklistedChannels", Collections.emptyList());
        fields.put("GuildVoiceRewards.MinMembers", 1);
        fields.put("ServerActivityLogging.Enabled", false);
        fields.put("ServerActivityLogging.ChannelID", -1);
        fields.put("ServerActivityLogging.Join.EmbedColor", "#8de113");
        fields.put("ServerActivityLogging.Join.Enabled", true);
        fields.put("ServerActivityLogging.Quit.EmbedColor", "#f34520");
        fields.put("ServerActivityLogging.Quit.Enabled", true);
        fields.put("ServerActivityLogging.Death.EmbedColor", "#f34520");
        fields.put("ServerActivityLogging.Death.Enabled", true);
        fields.put("ServerActivityLogging.Chat.EmbedColor", "#e8d725");
        fields.put("ServerActivityLogging.Chat.Enabled", true);
        fields.put("Console.Enabled", false);
        fields.put("Console.ClearOnEveryInit", true);
        fields.put("Console.ChannelID", -1);
        fields.put("Console.DeleteMessagesDelay", 10);
        fields.put("Console.BlacklistedCommands", Collections.emptyList());
        fields.put("Chat.Enabled", false);
        fields.put("Chat.WebhookUrl", "");
        fields.put("Chat.ChannelID", -1);
        fields.put("NotifyAboutMentions.Enabled", true);
        fields.put("NotifyAboutMentions.BlacklistedChannels", Collections.emptyList());
        fields.put("NotifyAboutMentions.Title.Enabled", true);
        fields.put("NotifyAboutMentions.Title.FadeIn", 3);
        fields.put("NotifyAboutMentions.Title.Stay", 50);
        fields.put("NotifyAboutMentions.Title.FadeOut", 3);
        fields.put("NotifyAboutMentions.Title.Title", "&bNew mention!");
        fields.put("NotifyAboutMentions.Title.Subtitle", "&fCheck your &9Discord&f.");
        fields.put("NotifyAboutMentions.Message.Enabled", false);
        fields.put("NotifyAboutMentions.Message.Text", "&9DiscordUtils &7/&f You have been mentioned in &9Discord&f.");
        fields.put("NotifyAboutMentions.Sound.Enabled", true);
        fields.put("NotifyAboutMentions.Sound.Type", "ENTITY_EXPERIENCE_ORB_PICKUP");
        fields.put("NotifyAboutMentions.Sound.Volume", 1);
        fields.put("NotifyAboutMentions.Sound.Pitch", 1);
        fields.put("CustomTriggers.InGameEvents", new HashMap<>());
        fields.put("InfoChannels", new HashMap<>());

        return fields;
    }
}
