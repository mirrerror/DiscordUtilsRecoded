package md.mirrerror.discordutils.commands.discordutils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetDiscord implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length < 1) {
            Message.DISCORDUTILS_GETDISCORD_USAGE.send(sender, true);
            return;
        }

        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);

        if(player == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if(offlinePlayer != null) {
                Main.getInstance().getDataManager().getDiscordUserId(offlinePlayer.getUniqueId()).whenComplete((userId, throwable) -> {
                    if(throwable != null) {
                        Message.UNKNOWN_ERROR.send(sender, true);
                        Main.getInstance().getLogger().severe("Something went wrong while getting Discord user ID for the player: " + offlinePlayer.getUniqueId() + "!");
                        return;
                    }

                    try {
                        sender.sendMessage(Message.GETDISCORD_SUCCESSFUL.getText(true).replace("%discord%", Main.getInstance().getBot().getJda().getUserById(userId).getAsTag()));
                    } catch (NullPointerException ignored) {
                        Message.INVALID_PLAYER_NAME_OR_UNVERIFIED.send(sender, true);
                    }
                });
            }
        } else {
            DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());
            if(!discordUtilsUser.isLinked()) {
                Message.INVALID_PLAYER_NAME_OR_UNVERIFIED.send(sender, true);
                return;
            }

            sender.sendMessage(Message.GETDISCORD_SUCCESSFUL.getText(true).replace("%discord%", discordUtilsUser.getUser().getAsTag()));
        }

    }

    @Override
    public String getName() {
        return "getdiscord";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutils.getdiscord";
    }

    @Override
    public List<String> getAliases() {
        return Collections.unmodifiableList(Arrays.asList("gdis", "gdiscord", "gd"));
    }

}
