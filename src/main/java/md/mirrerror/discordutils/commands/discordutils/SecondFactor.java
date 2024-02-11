package md.mirrerror.discordutils.commands.discordutils;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.events.custom.UserSecondFactorStateChangeEvent;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.utils.Validator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class SecondFactor implements SubCommand {

    private final DiscordUtilsBot bot;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validatePlayerSender(sender)) return;

        Player player = (Player) sender;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(!Validator.validateLinkedUser(sender, discordUtilsUser)) return;

        if(discordUtilsUser.isSecondFactorEnabled()) {
            if(!Validator.validateSecondFactorDisablingAvailability(sender, discordUtilsUser)) return;

            String playerIp = StringUtils.remove(player.getAddress().getAddress().toString(), '/');

            bot.sendActionChoosingMessage(discordUtilsUser.getUser(), playerIp, Message.SECONDFACTOR_DISABLE_CONFIRMATION.getText()).whenComplete((msg, error) -> {
                if (error == null) {
                    bot.getSecondFactorDisablePlayers().put(player.getUniqueId(), msg);
                    Message.SECONDFACTOR_DISABLE_REQUEST_SENT.send(sender, true);
                    return;
                }
                Message.CAN_NOT_SEND_MESSAGE.send(sender, true);
            });

        } else {

            discordUtilsUser.setSecondFactor(true);
            sender.sendMessage(Message.DISCORDUTILS_SECONDFACTOR_SUCCESSFUL.getText(true).replace("%status%", Message.ENABLED.getText()));

            UserSecondFactorStateChangeEvent userSecondFactorStateChangeEvent = new UserSecondFactorStateChangeEvent(discordUtilsUser, bot, false, true);
            Bukkit.getPluginManager().callEvent(userSecondFactorStateChangeEvent);
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

    @Override
    public int getMinArgsNeeded() {
        return 0;
    }

    @Override
    public String getIncorrectUsageErrorMessage() {
        return null;
    }

}
