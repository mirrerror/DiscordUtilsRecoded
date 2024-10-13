package md.mirrerror.discordutils.config.settings;

import de.leonhard.storage.Config;
import md.mirrerror.discordutils.Main;

public class MainSettings {

    private static final Config config = Main.getInstance().getConfigManager().getConfig().getConfig();

    public final String PERMISSIONS_PLUGIN = config.getString("PermissionsPlugin");
    public final boolean CHECK_FOR_UPDATES = config.getBoolean("CheckForUpdates");
    public final String LANGUAGE = config.getString("Language");
    public final String DATABASE_TYPE = config.getString("Database.Type");
    public final String DATABASE_HOST = config.getString("Database.Host");
    public final int DATABASE_PORT = config.getInt("Database.Port");
    public final String DATABASE_DATABASE = config.getString("Database.Database");
    public final String DATABASE_USERNAME = config.getString("Database.Username");
    public final String DATABASE_PASSWORD = config.getString("Database.Password");
    public final String DATABASE_CONNECTION_URL = config.getString("Database.ConnectionUrl");

}
