package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

public class LangConfig extends CustomConfig {
    public LangConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public void initializeFields() {
        getConfig().addDefaultsFromInputStream();
    }
}
