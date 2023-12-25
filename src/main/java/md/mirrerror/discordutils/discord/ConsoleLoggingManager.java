package md.mirrerror.discordutils.discord;

import md.mirrerror.discordutils.models.DiscordUtilsBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.ChatColor;

import java.util.concurrent.RejectedExecutionException;

public class ConsoleLoggingManager extends AbstractAppender {

    private final DiscordUtilsBot bot;

    public ConsoleLoggingManager(DiscordUtilsBot bot) {
        super("ConsoleLoggingManager", null, PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss} %level]: %msg").build(), false, Property.EMPTY_ARRAY);
        this.bot = bot;
    }

    public void initialize() {
        Logger log = (Logger) LogManager.getRootLogger();
        log.addAppender(this);
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public void append(LogEvent e) {
        StringBuilder stringBuilder = new StringBuilder();
        PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss} %level]: %msg").build().serialize(e, stringBuilder);

        String message = ChatColor.stripColor(stringBuilder.toString());
        if(message.length() > 2000) message = message.substring(0, 1990) + "...";

        try {
            bot.getConsoleLoggingTextChannel().sendMessage(message).queue();
        } catch (RejectedExecutionException ignored) {}
    }
}
