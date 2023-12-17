package md.mirrerror.discordutils.commands.discordutils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.utils.Validator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Link implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validatePlayerSender(sender)) return;

        Player player = (Player) sender;

        if(!Validator.validateNotLinkedUser(sender, DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId()))) return;

        if(!Validator.validateLinkCode(sender, args[0])) return;

        boolean defaultSecondFactorValue = BotSettings.DEFAULT_SECOND_FACTOR_VALUE;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        discordUtilsUser.setUser(Main.getInstance().getBot().getJda().getUserById(Main.getInstance().getBot().getLinkCodes().get(args[0])));
        discordUtilsUser.setSecondFactor(defaultSecondFactorValue);

        Main.getInstance().getBot().assignVerifiedRole(Main.getInstance().getBot().getLinkCodes().get(args[0]));

        Main.getInstance().getBot().getLinkCodes().remove(args[0]);
        Message.ACCOUNT_SUCCESSFULLY_LINKED.send(sender, true);
        BotSettings.COMMANDS_AFTER_LINKING
                .forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutils.link";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public int getMinArgsNeeded() {
        return 1;
    }

    @Override
    public Message getIncorrectUsageErrorMessage() {
        return Message.DISCORDUTILS_LINK_USAGE;
    }

}
