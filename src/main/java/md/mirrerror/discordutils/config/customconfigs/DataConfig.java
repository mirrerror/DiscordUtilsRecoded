package md.mirrerror.discordutils.config.customconfigs;

import java.util.HashMap;

public class DataConfig extends CustomConfig {
    public DataConfig(String fileName) {
        super(fileName);
    }

    @Override
    public void initializeFields() {
        getFileConfiguration().addDefault("DiscordLink", new HashMap<>());
        getFileConfiguration().options().copyDefaults(true);
        getFileConfiguration().options().copyHeader(true);
        saveConfigFile();
    }
}
