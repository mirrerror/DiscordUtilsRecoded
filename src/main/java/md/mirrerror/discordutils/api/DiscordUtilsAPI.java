/**
 * The DiscordUtilsAPI class provides static access to various components of the DiscordUtils plugin,
 * allowing easy retrieval of essential instances and information related to the Discord bot integration.
 * This class serves as a convenient entry point for interacting with the DiscordUtils plugin.
 *
 * @author mirrerror
 * @version 4.8
 * @since 2024-02-07
 */
package md.mirrerror.discordutils.api;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.CommandsManager;
import md.mirrerror.discordutils.config.ConfigManager;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.config.settings.MainSettings;
import md.mirrerror.discordutils.data.DataManager;
import md.mirrerror.discordutils.integrations.permissions.PermissionsIntegration;
import md.mirrerror.discordutils.integrations.placeholders.PAPIManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;

public class DiscordUtilsAPI {

    /**
     * Retrieves the DataManager instance, which manages data related functionalities.
     *
     * @return The DataManager instance.
     */
    public static DataManager getDataManager() {
        return Main.getInstance().getDataManager();
    }

    /**
     * Retrieves the PAPIManager instance, responsible for managing PlaceholderAPI integration.
     *
     * @return The PAPIManager instance.
     */
    public static PAPIManager getPapiManager() {
        return Main.getInstance().getPapiManager();
    }

    /**
     * Retrieves the ConfigManager instance, responsible for handling configuration settings.
     *
     * @return The ConfigManager instance.
     */
    public static ConfigManager getConfigManager() {
        return Main.getInstance().getConfigManager();
    }

    /**
     * Retrieves the DiscordUtilsBot instance, representing the Discord bot integrated with the plugin.
     *
     * @return The DiscordUtilsBot instance.
     * @throws IllegalStateException if the bot is not ready. Use {@link #isBotReady()} to check the bot's readiness.
     */
    public static DiscordUtilsBot getBot() {
        if (!isBotReady()) {
            throw new IllegalStateException("Bot is not ready. Check bot readiness using isBotReady() method before calling getBot().");
        }
        return Main.getInstance().getBot();
    }

    /**
     * Retrieves the BotSettings instance, containing settings specific to the Discord bot.
     *
     * @return The BotSettings instance.
     */
    public static BotSettings getBotSettings() {
        return Main.getInstance().getBotSettings();
    }

    /**
     * Retrieves the MainSettings instance, containing the general settings.
     *
     * @return The MainSettings instance.
     */
    public static MainSettings getMainSettings() {
        return Main.getInstance().getMainSettings();
    }

    /**
     * Retrieves the PermissionsIntegration instance, managing permissions-related functionalities.
     *
     * @return The PermissionsIntegration instance.
     */
    public static PermissionsIntegration getPermissionsIntegration() {
        return Main.getInstance().getPermissionsIntegration();
    }

    /**
     * Retrieves the CommandsManager instance, responsible for managing plugin commands.
     *
     * @return The CommandsManager instance.
     */
    public static CommandsManager getCommandsManager() {
        return Main.getInstance().getCommandsManager();
    }

    /**
     * Checks if the main class is ready.
     *
     * @return True if the main application is ready; otherwise, false.
     */
    public static boolean isMainReady() {
        return Main.isMainReady();
    }

    /**
     * Checks if the Discord bot is ready.
     *
     * @return True if the Discord bot is ready; otherwise, false.
     */
    public static boolean isBotReady() {
        return Main.isBotReady();
    }
}
