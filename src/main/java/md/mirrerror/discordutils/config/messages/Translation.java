package md.mirrerror.discordutils.config.messages;

public class Translation {

    private String key;
    private String author;

    public Translation(String key, String author) {
        this.key = key;
        this.author = author;
    }

    public void download() {
        TranslationsManager.downloadTranslation(key);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
