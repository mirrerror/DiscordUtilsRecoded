package md.mirrerror.discordutils;

import lombok.Getter;
import md.mirrerror.discordutils.commands.CommandsManager;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.commands.discordutils.*;
import md.mirrerror.discordutils.commands.discordutilsadmin.ForceUnlink;
import md.mirrerror.discordutils.commands.discordutilsadmin.Migrate;
import md.mirrerror.discordutils.commands.discordutilsadmin.Reload;
import md.mirrerror.discordutils.commands.discordutilsadmin.Stats;
import md.mirrerror.discordutils.config.ConfigManager;
import md.mirrerror.discordutils.config.messages.TranslationsManager;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.config.settings.MainSettings;
import md.mirrerror.discordutils.data.ConfigDataManager;
import md.mirrerror.discordutils.data.DataManager;
import md.mirrerror.discordutils.data.MySQLDataManager;
import md.mirrerror.discordutils.events.BukkitSecondFactorListener;
import md.mirrerror.discordutils.events.CacheListener;
import md.mirrerror.discordutils.events.CustomTriggersListener;
import md.mirrerror.discordutils.integrations.permissions.LuckPermsIntegration;
import md.mirrerror.discordutils.integrations.permissions.PermissionsIntegration;
import md.mirrerror.discordutils.integrations.permissions.VaultIntegration;
import md.mirrerror.discordutils.integrations.placeholders.PAPIManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.utils.Metrics;
import md.mirrerror.discordutils.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    private ConfigManager configManager;
    private DataManager dataManager;
    private PAPIManager papiManager;
    private PermissionsIntegration permissionsIntegration;

    private DiscordUtilsBot bot;

    @Getter
    private static boolean isMainReady;
    @Getter
    private static boolean isBotReady;

    private BotSettings botSettings;
    private MainSettings mainSettings;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        papiManager = new PAPIManager(this);

        mainSettings = new MainSettings();
        botSettings = new BotSettings();

        String permissionsPlugin = mainSettings.PERMISSIONS_PLUGIN.toLowerCase();
        switch (permissionsPlugin) {
            case "vault": {
                permissionsIntegration = new VaultIntegration(this);
                break;
            }
            default: {
                permissionsIntegration = new LuckPermsIntegration(this);
                break;
            }
        }

        String dataType = mainSettings.DATABASE_TYPE.toLowerCase();
        switch (dataType) {
            case "mysql": {
                dataManager = new MySQLDataManager(this, mainSettings);
                break;
            }
            default: {
                dataManager = new ConfigDataManager(configManager.getData());
                break;
            }
        }

        dataManager.setup().whenComplete((unused, throwable) -> {
            if(throwable != null) {
                Main.getInstance().getLogger().severe("Something went wrong while connecting to the database. Disabling the plugin...");
                Main.getInstance().getLogger().severe("Cause: " + throwable.getCause() + "; message: " + throwable.getMessage() + ".");
                Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
            }
        });

        bot = new DiscordUtilsBot(this, configManager.getBotSettings(), botSettings, papiManager, dataManager, permissionsIntegration);

        if(botSettings.ASYNC_BOT_LOADING) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> bot.setupBot());
        } else {
            bot.setupBot();
        }

        Bukkit.getPluginManager().registerEvents(new CacheListener(),this);
        Bukkit.getPluginManager().registerEvents(new BukkitSecondFactorListener(configManager, this, bot, botSettings), this);
        CustomTriggersListener customTriggersListener = new CustomTriggersListener(this, bot, configManager.getBotSettings().getFileConfiguration(),
                botSettings, papiManager);
        customTriggersListener.initialize();
        Bukkit.getPluginManager().registerEvents(customTriggersListener, this);
        getLogger().info("The Bukkit listeners have been successfully loaded.");

        registerCommands();
        getLogger().info("The commands have been successfully loaded.");

        String chosenTranslation = mainSettings.LANGUAGE;
        if(!chosenTranslation.isEmpty()) {
            new TranslationsManager(this, configManager).downloadTranslation(chosenTranslation);
        } else {
            getLogger().info("The chosen translation doesn't exist or you disabled this option.");
        }

        isMainReady = true;

        setupMetrics();
        if(mainSettings.CHECK_FOR_UPDATES) UpdateChecker.checkForUpdates();
    }

    @Override
    public void onDisable() {
        bot.setOnDisableInfoChannelNames();
    }

    private void setupMetrics() {
        Metrics metrics = new Metrics(this, 13243);
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
    }

    private void registerCommands() {
        CommandsManager commandManager = new CommandsManager(this);

        List<SubCommand> discordUtilsSubCommands = new ArrayList<>();
        discordUtilsSubCommands.add(new Link(botSettings, bot));
        discordUtilsSubCommands.add(new SecondFactor(bot));
        discordUtilsSubCommands.add(new Help());
        discordUtilsSubCommands.add(new SendToDiscord(bot, botSettings));
        discordUtilsSubCommands.add(new VoiceInvite(bot, this));
        discordUtilsSubCommands.add(new Unlink(bot));
        discordUtilsSubCommands.add(new GetDiscord(dataManager, this, bot));
        commandManager.registerCommand("discordutils", discordUtilsSubCommands);

        List<SubCommand> discordUtilsAdminSubCommands = new ArrayList<>();
        discordUtilsAdminSubCommands.add(new Reload(configManager));
        discordUtilsAdminSubCommands.add(new ForceUnlink(bot, botSettings, this));
        discordUtilsAdminSubCommands.add(new Stats(bot));
        discordUtilsAdminSubCommands.add(new Migrate(dataManager, this));
        commandManager.registerCommand("discordutilsadmin", discordUtilsAdminSubCommands);
    }

    public static void setBotReady(boolean isBotReady) {
        Main.isBotReady = isBotReady;
    }

}
