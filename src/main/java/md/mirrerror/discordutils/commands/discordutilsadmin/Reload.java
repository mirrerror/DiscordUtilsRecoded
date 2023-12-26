package md.mirrerror.discordutils.commands.discordutilsadmin;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.ConfigManager;
import md.mirrerror.discordutils.config.messages.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

@RequiredArgsConstructor
public class Reload implements SubCommand {

    private final ConfigManager configManager;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        configManager.reloadConfigFiles();
        Message.CONFIG_FILES_RELOADED.send(sender, true);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutilsadmin.reload";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rl", "rel");
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
