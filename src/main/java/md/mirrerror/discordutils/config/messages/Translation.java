package md.mirrerror.discordutils.config.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Translation {

    private String key;
    private String author;

    public void download() {
        TranslationsManager.downloadTranslation(key);
    }

}
