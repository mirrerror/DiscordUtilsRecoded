package md.mirrerror.discordutils.config.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.config.ConfigManager;
import md.mirrerror.discordutils.config.customconfigs.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Getter
@RequiredArgsConstructor
public class TranslationsManager {

    private final Plugin plugin;
    private final ConfigManager configManager;

    private static final String TRANSLATION_URL = "https://github.com/mirrerror/DiscordUtilsRecoded/raw/main/DUTranslations/";

    public void downloadTranslation(String key) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String fileName = "lang_" + key.toLowerCase() + ".yml";
            File file = plugin.getDataFolder().toPath().resolve(fileName).toFile();
            if(!file.exists())
                try {
                    plugin.getLogger().info("Started downloading translation with the key: " + key + ".");
                    URL url = new URL(TRANSLATION_URL + fileName);
                    Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.getLogger().info("Finished downloading translation with the key: " + key + ".");
                    configManager.setLang(new LangConfig(plugin, fileName));
                    plugin.getLogger().info("Switched to the new lang file.");
                } catch (IOException e) {
                    plugin.getLogger().severe("Something went wrong while downloading a translation file (key: \"" + key + "\")!");
                    plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
                }
            else {
                plugin.getLogger().info("The translation file already exists.");
                configManager.setLang(new LangConfig(plugin, fileName));
                plugin.getLogger().info("Switched to the translation lang file.");
            }
        });
    }

}
