package md.mirrerror.discordutils.discord;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

@RequiredArgsConstructor
public class EmbedManager {

    private final BotSettings botSettings;

    private final EmbedBuilder embedBuilder = new net.dv8tion.jda.api.EmbedBuilder();
    private final String FOOTER = Message.EMBED_FOOTER.getText();

    public MessageEmbed errorEmbed(String text) {
        embedBuilder.setTitle(Message.ERROR.getText());
        embedBuilder.setColor(botSettings.ERROR_EMBED_COLOR);
        embedBuilder.setDescription(text);
        embedBuilder.setFooter(FOOTER);
        return embedBuilder.build();
    }

    public MessageEmbed successfulEmbed(String text) {
        embedBuilder.setTitle(Message.SUCCESSFULLY.getText());
        embedBuilder.setColor(botSettings.SUCCESSFUL_EMBED_COLOR);
        embedBuilder.setDescription(text);
        embedBuilder.setFooter(FOOTER);
        return embedBuilder.build();
    }

    public MessageEmbed infoEmbed(String text) {
        embedBuilder.setTitle(Message.INFORMATION.getText());
        embedBuilder.setColor(botSettings.INFORMATION_EMBED_COLOR);
        embedBuilder.setDescription(text);
        embedBuilder.setFooter(FOOTER);
        return embedBuilder.build();
    }

    public MessageEmbed embed(String title, String text, Color color) {
        embedBuilder.setTitle(title);
        embedBuilder.setColor(color);
        embedBuilder.setDescription(text);
        embedBuilder.setFooter(FOOTER);
        return embedBuilder.build();
    }

    public MessageEmbed embed(String title, String text, Color color, String footer) {
        embedBuilder.setTitle(title);
        embedBuilder.setColor(color);
        embedBuilder.setDescription(text);
        embedBuilder.setFooter(footer + " / " + FOOTER);
        return embedBuilder.build();
    }

}
