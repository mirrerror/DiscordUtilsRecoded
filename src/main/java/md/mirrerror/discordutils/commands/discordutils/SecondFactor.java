package md.mirrerror.discordutils.commands.discordutils;

import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SecondFactor implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Message.SENDER_IS_NOT_A_PLAYER.send(sender, true);
            return;
        }

        Player player = (Player) sender;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());
        if(!discordUtilsUser.isLinked()) {
            Message.ACCOUNT_IS_NOT_VERIFIED.send(sender, true);
            return;
        }

        if(discordUtilsUser.hasSecondFactor()) {

            discordUtilsUser.setSecondFactor(false);
            sender.sendMessage(Message.DISCORDUTILS_SECONDFACTOR_SUCCESSFUL.getText(true).getText().replace("%status%", Message.DISABLED.getText().getText()));

        } else {

            discordUtilsUser.setSecondFactor(true);
            sender.sendMessage(Message.DISCORDUTILS_SECONDFACTOR_SUCCESSFUL.getText(true).getText().replace("%status%", Message.ENABLED.getText().getText()));

        }
    }

    @Override
    public String getName() {
        return "secondfactor";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutils.secondfactor";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("2fa");
    }

}
