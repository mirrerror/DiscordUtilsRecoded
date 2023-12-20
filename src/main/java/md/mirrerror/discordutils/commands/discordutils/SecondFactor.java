package md.mirrerror.discordutils.commands.discordutils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.utils.Validator;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SecondFactor implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validatePlayerSender(sender)) return;

        Player player = (Player) sender;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(!Validator.validateLinkedUser(sender, discordUtilsUser)) return;

        if(discordUtilsUser.isSecondFactorEnabled()) {

            String playerIp = StringUtils.remove(player.getAddress().getAddress().toString(), '/');

            discordUtilsUser.getUser().openPrivateChannel().submit()
                    .thenCompose(channel ->
                            channel.sendMessageEmbeds(
                                    new EmbedManager().infoEmbed(Message.SECONDFACTOR_DISABLE_CONFIRMATION.getText().replace("%playerIp%", playerIp))
                            ).addActionRow(Button.success("accept", Message.ACCEPT.getText())).addActionRow(Button.danger("decline", Message.DECLINE.getText())).submit())
                    .whenComplete((msg, error) -> {
                        if (error == null) {
                            Main.getInstance().getBot().getSecondFactorDisablePlayers().put(player.getUniqueId(), msg);
                            Message.SECONDFACTOR_DISABLE_REQUEST_SENT.send(sender, true);
                            return;
                        }
                        Message.CAN_NOT_SEND_MESSAGE.send(sender, true);
                    });

        } else {

            discordUtilsUser.setSecondFactor(true);
            sender.sendMessage(Message.DISCORDUTILS_SECONDFACTOR_SUCCESSFUL.getText(true).replace("%status%", Message.ENABLED.getText()));

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
    public Message getIncorrectUsageErrorMessage() {
        return null;
    }

}
