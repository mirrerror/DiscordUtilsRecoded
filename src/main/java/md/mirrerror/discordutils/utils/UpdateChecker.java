package md.mirrerror.discordutils.utils;

import md.mirrerror.discordutils.Main;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {

    public static void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                URL url = new URL("https://raw.githubusercontent.com/mirrerror/DiscordUtilsRecoded/main/version.txt");
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(1200);
                connection.setReadTimeout(1200);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String version = Main.getInstance().getDescription().getVersion();
                String latestVersion = bufferedReader.readLine().trim();
                if(!latestVersion.equalsIgnoreCase(version)) {
                    Main.getInstance().getLogger().info("There is a new plugin version available! Make sure to download the update!");
                    Main.getInstance().getLogger().info("Your version: " + version + "; latest version: " + latestVersion + ".");
                    Main.getInstance().getLogger().info("Links:");
                    Main.getInstance().getLogger().info("SpigotMC: https://www.spigotmc.org/resources/discordutils-discord-bot-for-your-minecraft-server.97433/");
                    Main.getInstance().getLogger().info("RuBukkit: http://rubukkit.org/threads/misc-discordutils-v1-0-discord-bot-dlja-servera-minecraft-1-7.179479/");
                } else {
                    Main.getInstance().getLogger().info("You're up to date.");
                }
            } catch (IOException e) {
                Main.getInstance().getLogger().severe("Something went wrong while checking for plugin new version!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
        });
    }

}
