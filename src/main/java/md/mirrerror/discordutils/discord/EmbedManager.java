package md.mirrerror.discordutils.discord;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class EmbedManager {

    private final EmbedBuilder embedBuilder = new net.dv8tion.jda.api.EmbedBuilder();
    private final String FOOTER = Message.EMBED_FOOTER.getText();

    public MessageEmbed errorEmbed(String text) {
        embedBuilder.setTitle(Message.ERROR.getText());
        embedBuilder.setColor(Main.getInstance().getBotSettings().ERROR_EMBED_COLOR);
        embedBuilder.setDescription(text);
        embedBuilder.setFooter(FOOTER);
        return embedBuilder.build();
    }

    public MessageEmbed successfulEmbed(String text) {
        embedBuilder.setTitle(Message.SUCCESSFULLY.getText());
        embedBuilder.setColor(Main.getInstance().getBotSettings().SUCCESSFUL_EMBED_COLOR);
        embedBuilder.setDescription(text);
        embedBuilder.setFooter(FOOTER);
        return embedBuilder.build();
    }

    public MessageEmbed infoEmbed(String text) {
        embedBuilder.setTitle(Message.INFORMATION.getText());
        embedBuilder.setColor(Main.getInstance().getBotSettings().INFORMATION_EMBED_COLOR);
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
