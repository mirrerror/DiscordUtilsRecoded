package md.mirrerror.discordutils.config.messages;

import lombok.Getter;
import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.customconfigs.LangConfig;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class TranslationsManager {

    private static Set<Translation> translations;
    private static final String TRANSLATION_URL = "https://github.com/mirrerror/DiscordUtilsRecoded/raw/main/DUTranslations/lang_";

    public static void registerTranslations(Set<Translation> translationsToRegister) {
        translations = new HashSet<>();
        translations.addAll(translationsToRegister);
    }

    public static File downloadTranslation(String key) {
        AtomicReference<File> result = new AtomicReference<>(null);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            String fileName = "lang_" + key.toLowerCase() + ".yml";
            File file = Main.getInstance().getDataFolder().toPath().resolve(fileName).toFile();
            if(!file.exists())
                try {
                    Main.getInstance().getLogger().info("Started downloading translation with the key: " + key + ".");
                    URL url = new URL(TRANSLATION_URL + key.toLowerCase() + ".yml");
                    Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Main.getInstance().getLogger().info("Finished downloading translation with the key: " + key + ".");
                    Main.getInstance().getConfigManager().setLang(new LangConfig(fileName));
                    Main.getInstance().getLogger().info("Switched to the new lang file.");
                } catch (IOException e) {
                    Main.getInstance().getLogger().severe("Something went wrong while downloading a translation file (key: \"" + key + "\")!");
                    Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
                    e.printStackTrace();
                }
            else {
                Main.getInstance().getLogger().info("The translation file already exists.");
                Main.getInstance().getConfigManager().setLang(new LangConfig(fileName));
                Main.getInstance().getLogger().info("Switched to the translation lang file.");
            }
        });
        return result.get();
    }

    public static Set<Translation> getTranslations() {
        return translations;
    }
}
