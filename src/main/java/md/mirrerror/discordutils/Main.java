package md.mirrerror.discordutils;

import md.mirrerror.discordutils.commands.CommandsManager;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.commands.discordutils.*;
import md.mirrerror.discordutils.commands.discordutilsadmin.ForceUnlink;
import md.mirrerror.discordutils.commands.discordutilsadmin.Reload;
import md.mirrerror.discordutils.commands.discordutilsadmin.Stats;
import md.mirrerror.discordutils.data.ConfigDataManager;
import md.mirrerror.discordutils.data.DataManager;
import md.mirrerror.discordutils.data.MySQLDataManager;
import md.mirrerror.discordutils.discord.DiscordUtilsBot;
import md.mirrerror.discordutils.config.ConfigManager;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.events.BukkitSecondFactorListener;
import md.mirrerror.discordutils.events.CacheListener;
import md.mirrerror.discordutils.integrations.permissions.LuckPermsIntegration;
import md.mirrerror.discordutils.integrations.permissions.PermissionsIntegration;
import md.mirrerror.discordutils.integrations.permissions.VaultIntegration;
import md.mirrerror.discordutils.integrations.placeholders.PAPIManager;
import md.mirrerror.discordutils.utils.Metrics;
import md.mirrerror.discordutils.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;
    private DataManager dataManager;
    private PAPIManager papiManager;
    private PermissionsIntegration permissionsIntegration;

    private DiscordUtilsBot bot;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        configManager = new ConfigManager();
        papiManager = new PAPIManager();

        String permissionsPlugin = configManager.getConfig().getFileConfiguration().getString("PermissionsPlugin").toLowerCase();
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

        String dataType = configManager.getConfig().getFileConfiguration().getString("Database.Type").toLowerCase();
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
        dataManager.setup();

        if(configManager.getBotSettings().getFileConfiguration().getBoolean("AsyncBotLoading")) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                bot = new DiscordUtilsBot(configManager.getBotSettings().getFileConfiguration().getString("BotToken"),
                        configManager.getBotSettings().getFileConfiguration().getString("BotPrefix"));
                bot.setupBot();
            });
        } else {
            bot = new DiscordUtilsBot(configManager.getBotSettings().getFileConfiguration().getString("BotToken"),
                    configManager.getBotSettings().getFileConfiguration().getString("BotPrefix"));
            bot.setupBot();
        }

        DiscordUtilsUsersCacheManager.initialize();

        Bukkit.getPluginManager().registerEvents(new CacheListener(),this);
        Bukkit.getPluginManager().registerEvents(new BukkitSecondFactorListener(), this);
        getLogger().info("The Bukkit listeners have been successfully loaded.");

        registerCommands();
        getLogger().info("The commands have been successfully loaded.");

        setupMetrics();
        UpdateChecker.checkForUpdates();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

    public DiscordUtilsBot getBot() {
        return bot;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public PAPIManager getPapiManager() {
        return papiManager;
    }

    public PermissionsIntegration getPermissionsIntegration() {
        return permissionsIntegration;
    }

    public static Main getInstance() {
        return instance;
    }
}