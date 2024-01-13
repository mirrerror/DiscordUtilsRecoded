package md.mirrerror.discordutils.commands.discordutils;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.utils.Validator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class Unlink implements SubCommand {

    private final DiscordUtilsBot bot;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validatePlayerSender(sender)) return;

        Player player = (Player) sender;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(!Validator.validateLinkedUser(sender, discordUtilsUser)) return;

        if(!Validator.validateUnlinkAvailability(sender, player)) return;

        String playerIp = StringUtils.remove(player.getAddress().getAddress().toString(), '/');

        bot.sendActionChoosingMessage(discordUtilsUser.getUser(), playerIp, Message.ACCOUNT_UNLINK_CONFIRMATION.getText()).whenComplete((msg, error) -> {
            if (error == null) {
                bot.getUnlinkPlayers().put(player.getUniqueId(), msg);
                return;
            }
            Message.CAN_NOT_SEND_MESSAGE.send(sender, true);
        });

        Message.ACCOUNT_UNLINK_REQUEST_SENT.send(sender, true);
    }

    @Override
    public String getName() {
        return "unlink";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutils.unlink";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("ulink");
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
