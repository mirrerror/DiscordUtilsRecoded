package md.mirrerror.discordutils.utils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.data.ConfigDataManager;
import md.mirrerror.discordutils.data.DataManager;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;

public class Validator {
    public static boolean validatePlayerSender(CommandSender sender) {
        if(!(sender instanceof Player)) {
            Message.SENDER_IS_NOT_A_PLAYER.send(sender, true);
            return false;
        }
        return true;
    }

    public static boolean validateLinkedUser(CommandSender sender, DiscordUtilsUser discordUtilsUser) {
        if(!discordUtilsUser.isLinked()) {
            Message.INVALID_PLAYER_NAME_OR_UNVERIFIED.send(sender, true);
            return false;
        }
        return true;
    }

    public static boolean validateNotLinkedUser(CommandSender sender, DiscordUtilsUser discordUtilsUser) {
        if(discordUtilsUser.isLinked()) {
            Message.ACCOUNT_ALREADY_VERIFIED.send(sender, true);
            return false;
        }
        return true;
    }

    public static boolean validateLinkCode(CommandSender sender, String code) {
        if(!Main.getInstance().getBot().getLinkCodes().containsKey(code)) {
            Message.INVALID_LINK_CODE.send(sender, true);
            return false;
        }
        return true;
    }

    public static boolean validateCommandToggleness(CommandSender sender, boolean isEnabled) {
        if(!isEnabled) {
            Message.COMMAND_DISABLED.send(sender, true);
            return false;
        }
        return true;
    }

    public static boolean validateColor(CommandSender sender, Color color) {
        if(color == null) {
            Message.INVALID_COLOR_VALUE.send(sender, true);
            return false;
        }
        return true;
    }

    public static boolean validateUnlinkAvailability(CommandSender sender, Player player) {
        if(Main.getInstance().getBot().getUnlinkPlayers().containsKey(player.getUniqueId())) {
            Message.UNLINK_ALREADY_INITIATED.send(sender, true);
            return false;
        }
        return true;
    }

    public static boolean validateVoiceChannelPresence(CommandSender sender, Member member) {
        if(member.getVoiceState().getChannel() == null) {
            Message.SENDER_IS_NOT_IN_A_VOICE_CHANNEL.send(sender, true);
            return false;
        }
        return true;
    }

    public static boolean validateTextChannel(CommandSender sender, TextChannel textChannel) {
        if(textChannel == null) {
            Message.CHANNEL_DOES_NOT_EXIST.send(sender, true);
            return false;
        }
        return true;
    }

    public static boolean validateOnlinePlayer(CommandSender sender, String playerName) {
        if(Bukkit.getPlayer(playerName) == null) {
            Message.TARGET_IS_OFFLINE.send(sender, true);
            return false;
        }
        return true;
    }
}
