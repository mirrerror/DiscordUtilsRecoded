package md.mirrerror.discordutils.config;

import lombok.Data;
import md.mirrerror.discordutils.config.customconfigs.*;
import org.bukkit.plugin.Plugin;

@Data
public class ConfigManager {

    private final Plugin plugin;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        initializeConfigFiles();
    }

    private MainConfig config;
    private BotSettingsConfig botSettings;
    private DataConfig data;
    private LangConfig lang;

    public void initializeConfigFiles() {
        config = new MainConfig(plugin, "config.yml");
        botSettings = new BotSettingsConfig(plugin, "bot_settings.yml");
        data = new DataConfig(plugin, "data.yml");
        lang = new LangConfig(plugin, "lang.yml");
    }

    public void saveConfigFiles() {
        config.saveConfigFile();
        botSettings.saveConfigFile();
        data.saveConfigFile();
        lang.saveConfigFile();
    }

    public void reloadConfigFiles() {
        config.reloadConfigFile();
        botSettings.reloadConfigFile();
        data.reloadConfigFile();
        lang.reloadConfigFile();
    }

}
