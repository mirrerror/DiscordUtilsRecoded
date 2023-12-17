package md.mirrerror.discordutils.commands;

import lombok.Getter;
import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> results = new ArrayList<>();

        if(args.length == 1) {

            for(SubCommand subCommand : commands.get(command.getName()))
                if(sender.hasPermission(subCommand.getPermission()))
                    results.add(subCommand.getName());

        } else {

            String lastWord = args[args.length - 1];

            Player senderPlayer = sender instanceof Player ? (Player) sender : null;

            for (Player player : sender.getServer().getOnlinePlayers()) {
                String name = player.getName();
                if ((senderPlayer == null || senderPlayer.canSee(player)) && StringUtil.startsWithIgnoreCase(name, lastWord)) {
                    results.add(name);
                }
            }

            results.sort(String.CASE_INSENSITIVE_ORDER);

        }

        return results;
    }
}
