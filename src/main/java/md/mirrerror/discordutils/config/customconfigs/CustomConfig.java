package md.mirrerror.discordutils.config.customconfigs;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

@Getter
public abstract class CustomConfig {

    private final Plugin plugin;
    private final File file;
    private FileConfiguration fileConfiguration;

    private final Map<String, Object> fields;

    public CustomConfig(Plugin plugin, String fileName) {
        this.file = new File(plugin.getDataFolder(), fileName);
        this.plugin = plugin;
        this.fields = initializeFields();
        initializeConfigFile();
    }

    public void initializeConfigFile() {
        if(!file.exists()) {
            saveDefaultConfig();
            plugin.getLogger().info("Config file '" + file.getName() + "' has been successfully created.");
        }

        reloadConfigFile();
        setMissingDefaults();
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfigFile();
        }
        return fileConfiguration;
    }

    public void saveConfigFile() {
        if (fileConfiguration != null && file != null) {
            try {
                getConfig().save(file);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + file, ex);
            }
        }
    }

    public void saveDefaultConfig() {
        if (!file.exists()) this.plugin.saveResource(file.getName(), false);
    }

    public void reloadConfigFile() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);

//        InputStream defConfigStream = plugin.getResource(file.getName());
//        if (defConfigStream != null) {
//            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
//            fileConfiguration.setDefaults(defConfig);
//        }
    }

    public abstract Map<String, Object> initializeFields();

    public void setMissingDefaults() {
        if (fileConfiguration == null) reloadConfigFile();

        for (Map.Entry<String, Object> entry : fields.entrySet())
            if (fileConfiguration.get(entry.getKey()) == null)
                fileConfiguration.set(entry.getKey(), entry.getValue());

        saveConfigFile();
    }

}
