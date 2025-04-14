package md.mirrerror.discordutils.discord;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@RequiredArgsConstructor
public class EmbedMessagesBuilder extends EmbedBuilder {

    private final BotSettings botSettings;

    private final String FOOTER = Message.EMBED_FOOTER.getText();

    public EmbedMessagesBuilder errorEmbed(String text) {
        embed(Message.ERROR.getText(), text, botSettings.ERROR_EMBED_COLOR).setImage(botSettings.ERROR_EMBED_IMAGE_URL);
        return this;
    }

    public EmbedMessagesBuilder successfulEmbed(String text) {
        embed(Message.SUCCESSFULLY.getText(), text, botSettings.SUCCESSFUL_EMBED_COLOR).setImage(botSettings.SUCCESSFUL_EMBED_IMAGE_URL);
        return this;
    }

    public EmbedMessagesBuilder infoEmbed(String text) {
        embed(Message.INFORMATION.getText(), text, botSettings.INFORMATION_EMBED_COLOR).setImage(botSettings.INFORMATION_EMBED_IMAGE_URL);
        return this;
    }

    public EmbedMessagesBuilder embed(String title, String text, Color color) {
        embed(title, text, color, null);
        return this;
    }

    public EmbedMessagesBuilder embed(String title, String text, Color color, String footer) {
        setTitle(title).setColor(color).setDescription(text).setFooter(footer == null ? FOOTER : footer + " / " + FOOTER);
        return this;
    }

    @Override
    public @NotNull MessageEmbed build() {
        MessageEmbed messageEmbed = super.build();
        clear();
        return messageEmbed;
    }

}
