package md.mirrerror.discordutils.config;

import lombok.Data;
import md.mirrerror.discordutils.config.customconfigs.*;

@Data
public class ConfigManager {

    public ConfigManager() {
        initializeConfigFiles();
    }

    private MainConfig config;
    private BotSettingsConfig botSettings;
    private DataConfig data;
    private LangConfig lang;

    public void initializeConfigFiles() {
        config = new MainConfig("config.yml");
        botSettings = new BotSettingsConfig("bot_settings.yml");
        data = new DataConfig("data.yml");
        lang = new LangConfig("lang.yml");
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
