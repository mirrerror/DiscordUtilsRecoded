package md.mirrerror.discordutils.config.customconfigs;

import org.bukkit.plugin.Plugin;

public class MainConfig extends CustomConfig {
    public MainConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public void initializeFields() {
        getFileConfiguration().addDefault("PermissionsPlugin", "LuckPerms");
        getFileConfiguration().addDefault("CheckForUpdates", true);
        getFileConfiguration().addDefault("Language", "");
        getFileConfiguration().addDefault("Database.Type", "");
        getFileConfiguration().addDefault("Database.Host", "localhost");
        getFileConfiguration().addDefault("Database.Port", 3306);
        getFileConfiguration().addDefault("Database.Database", "discordutils");
        getFileConfiguration().addDefault("Database.Username", "root");
        getFileConfiguration().addDefault("Database.Password", "");
        getFileConfiguration().addDefault("Database.ConnectionUrl", "jdbc:mysql://%host%:%port%/%database%?autoReconnect=true&useSSL=false");
        getFileConfiguration().options().copyDefaults(true);
        getFileConfiguration().options().copyHeader(true);
        saveConfigFile();
    }
}
