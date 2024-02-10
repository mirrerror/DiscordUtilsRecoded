package md.mirrerror.discordutils.commands.discordutils;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.events.custom.AccountLinkEvent;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.utils.Validator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class Link implements SubCommand {

    private final BotSettings botSettings;
    private final DiscordUtilsBot bot;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validatePlayerSender(sender)) return;

        Player player = (Player) sender;

        if(!Validator.validateNotLinkedUser(sender, DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId()))) return;

        if(!Validator.validateLinkCode(sender, args[0])) return;

        boolean defaultSecondFactorValue = botSettings.DEFAULT_SECOND_FACTOR_VALUE;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        discordUtilsUser.setUser(bot.getJda().getUserById(bot.getLinkCodes().get(args[0])));
        discordUtilsUser.setSecondFactor(defaultSecondFactorValue);

        bot.assignVerifiedRole(bot.getLinkCodes().get(args[0]));

        bot.getLinkCodes().remove(args[0]);
        Message.ACCOUNT_SUCCESSFULLY_LINKED.send(sender, true);
        botSettings.COMMANDS_AFTER_LINKING
                .forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));

        AccountLinkEvent accountLinkEvent = new AccountLinkEvent(discordUtilsUser, bot, args[0]);
        Bukkit.getPluginManager().callEvent(accountLinkEvent);
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
    public String getIncorrectUsageErrorMessage() {
        return Message.DISCORDUTILS_LINK_USAGE.getText(true);
    }

}
