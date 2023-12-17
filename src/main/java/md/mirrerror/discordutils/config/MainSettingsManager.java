package md.mirrerror.discordutils.config;

import md.mirrerror.discordutils.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class MainSettingsManager {

    private static final FileConfiguration config = Main.getInstance().getConfigManager().getConfig().getFileConfiguration();

    public static final String PERMISSIONS_PLUGIN = config.getString("PermissionsPlugin");
    public static final boolean CHECK_FOR_UPDATES = config.getBoolean("CheckForUpdates");
    public static final String LANGUAGE = config.getString("Language");
    public static final String DATABASE_TYPE = config.getString("Database.Type");
    public static final String DATABASE_HOST = config.getString("Database.Host");
    public static final int DATABASE_PORT = config.getInt("Database.Port");
    public static final String DATABASE_DATABASE = config.getString("Database.Database");
    public static final String DATABASE_USERNAME = config.getString("Database.Username");
    public static final String DATABASE_PASSWORD = config.getString("Database.Password");
    public static final String DATABASE_CONNECTION_URL = config.getString("Database.ConnectionUrl");

}
