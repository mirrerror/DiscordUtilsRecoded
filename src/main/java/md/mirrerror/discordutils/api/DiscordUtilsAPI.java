package md.mirrerror.discordutils.api;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.ConfigManager;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.config.settings.MainSettings;
import md.mirrerror.discordutils.data.DataManager;
import md.mirrerror.discordutils.integrations.permissions.PermissionsIntegration;
import md.mirrerror.discordutils.integrations.placeholders.PAPIManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;

public class DiscordUtilsAPI {

    public static DataManager getDataManager() {
        return Main.getInstance().getDataManager();
    }

    public static PAPIManager getPapiManager() {
        return Main.getInstance().getPapiManager();
    }

    public static ConfigManager getConfigManager() {
        return Main.getInstance().getConfigManager();
    }

    public static DiscordUtilsBot getBot() {
        return Main.getInstance().getBot();
    }

    public static BotSettings getBotSettings() {
        return Main.getInstance().getBotSettings();
    }

    public static MainSettings getMainSettings() {
        return Main.getInstance().getMainSettings();
    }

    public static PermissionsIntegration getPermissionsIntegration() {
        return Main.getInstance().getPermissionsIntegration();
    }

    public static boolean isMainReady() {
        return Main.isMainReady();
    }

    public static boolean isBotReady() {
        return Main.isBotReady();
    }

}
