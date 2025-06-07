package md.mirrerror.discordutils.utils;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.Main;
import org.bukkit.Bukkit;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
public class UpdateChecker {

    private final Main plugin;

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URI(
                    "https://api.github.com/repos/mirrerror/DiscordUtilsRecoded/releases/latest").toURL().openStream()) {
                JSONObject response = new JSONObject(new JSONTokener(inputStream));

                String currentVersion = plugin.getDescription().getVersion();
                String latestVersion = response.getString("tag_name");

                if(!latestVersion.equalsIgnoreCase(currentVersion)) {
                    plugin.getLogger().info("There is a new plugin version available! Make sure to download the update!");
                    plugin.getLogger().info("Your version: " + currentVersion + "; latest version: " + latestVersion + ".");
                    plugin.getLogger().info("Links:");
                    plugin.getLogger().info("SpigotMC: https://www.spigotmc.org/resources/discordutils-discord-bot-for-your-minecraft-server.97433/");
//                    plugin.getLogger().info("RuBukkit: http://rubukkit.org/threads/misc-discordutils-v1-0-discord-bot-dlja-servera-minecraft-1-7.179479/");
                } else {
                    plugin.getLogger().info("You're up to date.");
                }

            } catch (IOException | URISyntaxException exception) {
                plugin.getLogger().severe("Something went wrong while checking for plugin new version!");
                plugin.getLogger().severe("Cause: " + exception.getCause() + "; message: " + exception.getMessage() + ".");
            }
        });
    }

}
