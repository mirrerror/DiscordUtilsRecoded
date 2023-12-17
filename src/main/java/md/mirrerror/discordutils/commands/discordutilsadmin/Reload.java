package md.mirrerror.discordutils.commands.discordutilsadmin;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.config.settings.MainSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Reload implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        Main.getInstance().getConfigManager().reloadConfigFiles();
        Main.getInstance().setMainSettings(new MainSettings());
        Main.getInstance().setBotSettings(new BotSettings());
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
        return Collections.unmodifiableList(Arrays.asList("rl", "rel"));
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
