package md.mirrerror.discordutils.commands.discordutilsadmin;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class Stats implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        Message.DISCORDUTILSADMIN_STATS_FORMAT.getTextList().forEach(msg -> sender.sendMessage(msg.replace("%linkedPlayers%",
                String.valueOf(Main.getInstance().getBot().countLinkedUsers()))));
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
    public Message getIncorrectUsageErrorMessage() {
        return null;
    }
}
