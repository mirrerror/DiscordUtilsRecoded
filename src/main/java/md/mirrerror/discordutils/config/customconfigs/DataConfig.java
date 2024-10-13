package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

public class DataConfig extends CustomConfig {
    public DataConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public void initializeFields() {
        getConfig().addDefaultsFromInputStream();
    }
}
