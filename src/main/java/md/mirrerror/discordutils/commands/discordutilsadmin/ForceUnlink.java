package md.mirrerror.discordutils.commands.discordutilsadmin;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.events.custom.AccountUnlinkEvent;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.utils.Validator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

@RequiredArgsConstructor
public class ForceUnlink implements SubCommand {

    private final DiscordUtilsBot bot;
    private final BotSettings botSettings;
    private final Plugin plugin;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(!Validator.validateLinkedUser(sender, discordUtilsUser)) return;

        bot.unAssignVerifiedRole(discordUtilsUser.getUser().getIdLong());

        if(player.isOnline()) {
            Player onlinePlayer = player.getPlayer();
            bot.getUnlinkPlayers().remove(onlinePlayer.getUniqueId());
            onlinePlayer.sendMessage(Message.DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_TARGET.getText(true).replace("%sender%", sender.getName()).replace("%target%", player.getName()));
        }

        AccountUnlinkEvent accountUnlinkEvent = new AccountUnlinkEvent(discordUtilsUser, bot);
        Bukkit.getPluginManager().callEvent(accountUnlinkEvent);

        Bukkit.getScheduler().runTask(plugin, () -> {
            botSettings.COMMANDS_AFTER_UNLINKING.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));
        });
        discordUtilsUser.unregister();

        sender.sendMessage(Message.DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_SENDER.getText(true).replace("%sender%", sender.getName()).replace("%target%", player.getName()));
    }

    @Override
    public String getName() {
        return "forceunlink";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutilsadmin.forceunlink";
    }

    @Override
    public List<String> getAliases() {
        return List.of("funlink", "fulink", "forceulink");
    }

    @Override
    public int getMinArgsNeeded() {
        return 1;
    }

    @Override
    public Message getIncorrectUsageErrorMessage() {
        return Message.DISCORDUTILSADMIN_FORCEUNLINK_USAGE;
    }

}
