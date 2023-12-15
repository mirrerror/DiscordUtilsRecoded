package md.mirrerror.discordutils.commands;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandsManager implements CommandExecutor, TabCompleter {

    private static final Map<String, List<SubCommand>> commands = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if(commands.get(command.getName()) != null) {

            int newLength = 0;

            if(args.length >= 1) newLength = args.length-1;

            String[] newArgs = new String[newLength];

            for (int i = 1, j = 0; i < args.length && j < args.length; i++, j++)
                newArgs[j] = args[i];

            boolean hasSubCommand = false;

            if(args.length >= 1) for(SubCommand subCommand : commands.get(command.getName())) {

                if(subCommand.getName().equals(args[0]) || subCommand.getAliases().contains(args[0])) {

                    hasSubCommand = true;

                    if(sender.hasPermission(subCommand.getPermission()))
                        if(subCommand.getMinArgsNeeded() > newArgs.length)
                            subCommand.getIncorrectUsageErrorMessage().send(sender, true);
                        else
                            subCommand.onCommand(sender, command, label, newArgs);
                    else
                        Message.INSUFFICIENT_PERMISSIONS.send(sender, true);

                }

            }

            if(!hasSubCommand)
                Message.UNKNOWN_SUBCOMMAND.send(sender, true);

        }

        return true;
    }

    public void registerCommand(String command, List<SubCommand> subCommands) {
        commands.put(command, subCommands);
        Main.getInstance().getCommand(command).setExecutor(this);
    }

    public void registerSubCommand(String command, SubCommand subCommand) {
        if(commands.get(command) == null) return;
        List<SubCommand> subCommands = commands.get(command);
        subCommands.add(subCommand);
        commands.put(command, subCommands);
    }

    public static Map<String, List<SubCommand>> getCommands() {
        return commands;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        List<String> results = new ArrayList<>();

        for(SubCommand subCommand : commands.get(command.getName()))
            if(sender.hasPermission(subCommand.getPermission()))
                results.add(subCommand.getName());

        return results;
    }
}
