package md.mirrerror.discordutils.config.customconfigs;

import md.mirrerror.discordutils.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class CustomConfig {

    private final File file;
    private FileConfiguration fileConfiguration;

    public CustomConfig(String fileName) {
        file = new File(Main.getInstance().getDataFolder(), fileName);
        initializeConfigFile();
        initializeFields();
    }

    public void initializeConfigFile() {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            Main.getInstance().saveResource(file.getName(), false);
            Main.getInstance().getLogger().info("Config file '" + file.getName() + "' has been successfully created.");
        }

        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            Main.getInstance().getLogger().severe("Something went wrong while initializing the config file named '" + file.getName() + "'!");
            Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
    }

    public void saveConfigFile() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("Something went wrong while saving the config file named '" + file.getName() + "'!");
            Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
    }

    public void reloadConfigFile() {
        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            Main.getInstance().getLogger().severe("Something went wrong while loading the config file named '" + file.getName() + "'!");
            Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
    }

    public abstract void initializeFields();

    public File getFile() {
        return file;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }
}
