package md.mirrerror.discordutils.commands;

import md.mirrerror.discordutils.config.messages.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    void onCommand(CommandSender sender, Command command, String label, String[] args);
    String getName();
    String getPermission();
    List<String> getAliases();
    int getMinArgsNeeded();
    Message getIncorrectUsageErrorMessage();

}