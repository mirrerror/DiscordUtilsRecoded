package md.mirrerror.discordutils.utils;

import md.mirrerror.discordutils.config.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Validator {
    public static boolean validatePlayerSender(CommandSender commandSender) {
        if(!(commandSender instanceof Player)) {
            Message.SENDER_IS_NOT_A_PLAYER.send(commandSender, true);
            return false;
        }
        return true;
    }
}
