package md.mirrerror.discordutils.commands.discordutils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.BotSettings;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.utils.Validator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

public class SendToDiscord implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validateCommandToggleness(sender, BotSettings.MESSAGES_CHANNEL_ENABLED))
            return;

        Main.getInstance().getBot().getJda().getGuilds().forEach(guild -> {
            StringBuilder text = new StringBuilder();
            for(int i = 2; i < args.length; i++) text.append(args[i]).append(" ");

            Color color;
            try {
                color = Color.decode(args[1]);
            } catch (Exception e) {
                color = null;
            }

            if(!Validator.validateColor(sender, color)) return;

            Main.getInstance().getBot().sendMessageEmbed(Main.getInstance().getBot().getMessagesTextChannel(),
                    new EmbedManager().embed(args[0], text.toString().trim().replace("\\n", "\n"), color, Message.SENDTODISCORD_SENT_BY.getText().replace("%sender%", sender.getName())));
            Message.DISCORDUTILS_SENDTODISCORD_SUCCESSFUL.send(sender, true);
        });
    }

    @Override
    public String getName() {
        return "sendtodiscord";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutils.sendtodiscord";
    }

    @Override
    public java.util.List<String> getAliases() {
        return Collections.unmodifiableList(Arrays.asList("sendtodis", "std", "stodis", "stdis"));
    }

    @Override
    public int getMinArgsNeeded() {
        return 3;
    }

    @Override
    public Message getIncorrectUsageErrorMessage() {
        return Message.DISCORDUTILS_SENDTODISCORD_USAGE;
    }

}
