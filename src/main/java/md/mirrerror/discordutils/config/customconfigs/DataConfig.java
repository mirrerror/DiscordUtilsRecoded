package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class DataConfig extends CustomConfig {
    public DataConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public Map<String, Object> initializeFields() {
        Map<String, Object> fields = new HashMap<>();

        fields.put("DiscordLink", new HashMap<>());

        return fields;
    }
}
