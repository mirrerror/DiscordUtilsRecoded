package md.mirrerror.discordutils.config.messages;

public class Translation {

    private String key;
    private String author;
    private String version;

    public Translation(String key, String author, String version) {
        this.key = key;
        this.author = author;
        this.version = version;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
