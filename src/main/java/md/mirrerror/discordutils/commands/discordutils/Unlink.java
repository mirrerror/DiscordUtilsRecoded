package md.mirrerror.discordutils.commands.discordutils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Unlink implements SubCommand {

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

        if(Main.getInstance().getBot().getUnlinkPlayers().containsKey(player.getUniqueId())) {
            Message.UNLINK_ALREADY_INITIATED.send(sender, true);
            return;
        }

        String playerIp = StringUtils.remove(player.getAddress().getAddress().toString(), '/');

        discordUtilsUser.getUser().openPrivateChannel().submit()
                .thenCompose(channel -> channel.sendMessageEmbeds(new EmbedManager().infoEmbed(Message.ACCOUNT_UNLINK_CONFIRMATION.getText().getText().replace("%playerIp%", playerIp))).submit())
                .whenComplete((msg, error) -> {
                    if (error == null) {
                        Main.getInstance().getBot().getUnlinkPlayers().put(player.getUniqueId(), msg);
                        msg.addReaction("✅").queue();
                        msg.addReaction("❎").queue();
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

}
