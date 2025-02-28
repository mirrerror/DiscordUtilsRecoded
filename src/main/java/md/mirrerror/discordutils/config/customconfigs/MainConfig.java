package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class MainConfig extends CustomConfig {
    public MainConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public Map<String, Object> initializeFields() {
        Map<String, Object> fields = new HashMap<>();

        fields.put("PermissionsPlugin", "LuckPerms");
        fields.put("CheckForUpdates", true);
        fields.put("Language", "");
        fields.put("Database.Type", "");
        fields.put("Database.Host", "localhost");
        fields.put("Database.Port", 3306);
        fields.put("Database.Database", "discordutils");
        fields.put("Database.Username", "root");
        fields.put("Database.Password", "");
        fields.put("Database.ConnectionUrl", "jdbc:mysql://%host%:%port%/%database%?autoReconnect=true&useSSL=false");

        return fields;
    }
}
