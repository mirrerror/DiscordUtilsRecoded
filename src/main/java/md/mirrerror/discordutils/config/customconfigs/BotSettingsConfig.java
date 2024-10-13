package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

public class BotSettingsConfig extends CustomConfig {
    public BotSettingsConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public void initializeFields() {
        getConfig().addDefaultsFromInputStream();
    }
}
