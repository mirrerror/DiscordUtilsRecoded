package md.mirrerror.discordutils.config.customconfigs;

import de.leonhard.storage.Config;
import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Getter
public abstract class CustomConfig {

    private final Plugin plugin;
    private final File file;
    private Config config;

    public CustomConfig(Plugin plugin, String fileName) {
        this.file = new File(plugin.getDataFolder(), fileName);
        this.plugin = plugin;
        initializeConfigFile();
        initializeFields();
    }

    public void initializeConfigFile() {
        config = SimplixBuilder
                .fromFile(file)
                .addInputStreamFromResource(file.getName())
                .setDataType(DataType.SORTED)
                .setReloadSettings(ReloadSettings.INTELLIGENT)
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .createConfig();
    }

    public void reloadConfigFile() {
        config.forceReload();
    }

    public abstract void initializeFields();

}
