package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

public class MainConfig extends CustomConfig {
    public MainConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public void initializeFields() {
        getConfig().addDefaultsFromInputStream();
    }
}
