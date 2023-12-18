package md.mirrerror.discordutils;

import lombok.Getter;
import lombok.Setter;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.commands.CommandsManager;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.commands.discordutils.*;
import md.mirrerror.discordutils.commands.discordutilsadmin.ForceUnlink;
import md.mirrerror.discordutils.commands.discordutilsadmin.Reload;
import md.mirrerror.discordutils.commands.discordutilsadmin.Stats;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.config.ConfigManager;
import md.mirrerror.discordutils.config.settings.MainSettings;
import md.mirrerror.discordutils.config.messages.TranslationsManager;
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

    private boolean isMainReady;
    @Setter
    private boolean isBotReady;

    @Setter
    private BotSettings botSettings;
    @Setter
    private MainSettings mainSettings;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        papiManager = new PAPIManager();

        mainSettings = new MainSettings();
        botSettings = new BotSettings();

        String permissionsPlugin = mainSettings.PERMISSIONS_PLUGIN.toLowerCase();
        switch (permissionsPlugin) {
            case "vault": {
                permissionsIntegration = new VaultIntegration();
                break;
            }
            default: {
                permissionsIntegration = new LuckPermsIntegration();
                break;
            }
        }

        String dataType = mainSettings.DATABASE_TYPE.toLowerCase();
        switch (dataType) {
            case "mysql": {
                dataManager = new MySQLDataManager();
                break;
            }
            default: {
                dataManager = new ConfigDataManager();
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

        if(botSettings.ASYNC_BOT_LOADING) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                bot = new DiscordUtilsBot(botSettings.BOT_TOKEN);
                bot.setupBot();
            });
        } else {
            bot = new DiscordUtilsBot(botSettings.BOT_TOKEN);
            bot.setupBot();
        }

        DiscordUtilsUsersCacheManager.initialize();

        Bukkit.getPluginManager().registerEvents(new CacheListener(),this);
        Bukkit.getPluginManager().registerEvents(new BukkitSecondFactorListener(), this);
        CustomTriggersListener customTriggersListener = new CustomTriggersListener(this);
        customTriggersListener.initialize();
        Bukkit.getPluginManager().registerEvents(customTriggersListener, this);
        getLogger().info("The Bukkit listeners have been successfully loaded.");

        registerCommands();
        getLogger().info("The commands have been successfully loaded.");

        String chosenTranslation = mainSettings.LANGUAGE;
        if(!chosenTranslation.isEmpty()) {
            TranslationsManager.downloadTranslation(chosenTranslation);
        } else {
            getLogger().info("The chosen translation doesn't exist or you disabled this option.");
        }

        isMainReady = true;

        setupMetrics();
        if(mainSettings.CHECK_FOR_UPDATES) UpdateChecker.checkForUpdates();
    }

    @Override
    public void onDisable() {}

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
        CommandsManager commandManager = new CommandsManager();

        List<SubCommand> discordUtilsSubCommands = new ArrayList<>();
        discordUtilsSubCommands.add(new Link());
        discordUtilsSubCommands.add(new SecondFactor());
        discordUtilsSubCommands.add(new Help());
        discordUtilsSubCommands.add(new SendToDiscord());
        discordUtilsSubCommands.add(new VoiceInvite());
        discordUtilsSubCommands.add(new Unlink());
        discordUtilsSubCommands.add(new GetDiscord());
        commandManager.registerCommand("discordutils", discordUtilsSubCommands);

        List<SubCommand> discordUtilsAdminSubCommands = new ArrayList<>();
        discordUtilsAdminSubCommands.add(new Reload());
        discordUtilsAdminSubCommands.add(new ForceUnlink());
        discordUtilsAdminSubCommands.add(new Stats());
        commandManager.registerCommand("discordutilsadmin", discordUtilsAdminSubCommands);
    }

}
