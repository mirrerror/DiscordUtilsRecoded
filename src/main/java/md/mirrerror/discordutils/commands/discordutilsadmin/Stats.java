package md.mirrerror.discordutils.commands.discordutilsadmin;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class Stats implements SubCommand {

    private final DiscordUtilsBot bot;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        bot.countLinkedUsers().whenComplete((count, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            Message.DISCORDUTILSADMIN_STATS_FORMAT.getTextList().forEach(msg -> sender.sendMessage(msg.replace("%linkedPlayers%",
                    String.valueOf(count))));
        });
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutilsadmin.stats";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("stat");
    }

    @Override
    public int getMinArgsNeeded() {
        return 0;
    }

    @Override
    public String getIncorrectUsageErrorMessage() {
        return null;
    }
}
