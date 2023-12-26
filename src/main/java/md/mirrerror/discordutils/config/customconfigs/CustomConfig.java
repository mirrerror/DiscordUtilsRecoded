package md.mirrerror.discordutils.config.customconfigs;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

@Getter
public abstract class CustomConfig {

    private final Plugin plugin;
    private final File file;
    private FileConfiguration fileConfiguration;

    public CustomConfig(Plugin plugin, String fileName) {
        this.file = new File(plugin.getDataFolder(), fileName);
        this.plugin = plugin;
        initializeConfigFile();
        initializeFields();
    }

    public void initializeConfigFile() {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(file.getName(), false);
            plugin.getLogger().info("Config file '" + file.getName() + "' has been successfully created.");
        }

        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Something went wrong while initializing the config file named '" + file.getName() + "'!");
            plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
    }

    public void saveConfigFile() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Something went wrong while saving the config file named '" + file.getName() + "'!");
            plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
    }

    public void reloadConfigFile() {
        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Something went wrong while loading the config file named '" + file.getName() + "'!");
            plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
    }

    public abstract void initializeFields();

}
