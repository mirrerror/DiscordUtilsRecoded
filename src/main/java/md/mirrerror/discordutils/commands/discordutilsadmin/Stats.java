package md.mirrerror.discordutils.commands.discordutilsadmin;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class Stats implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        Main.getInstance().getDataManager().countLinkedUsers().whenComplete((count, throwable) -> {
            if(throwable != null) {
                Main.getInstance().getLogger().severe("Something went wrong while counting the linked players!");
                return;
            }
            Message.DISCORDUTILSADMIN_STATS_FORMAT.getTextList().forEach(msg -> sender.sendMessage(msg.getText().replace("%linkedPlayers%", String.valueOf(count))));
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
}
