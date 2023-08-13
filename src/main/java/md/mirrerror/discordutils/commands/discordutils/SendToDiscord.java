package md.mirrerror.discordutils.commands.discordutils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.Message;
import md.mirrerror.discordutils.discord.EmbedManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

public class SendToDiscord implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("MessagesChannel.Enabled")) {
            Message.COMMAND_DISABLED.send(sender, true);
            return;
        }
        if(args.length < 3) {
            Message.DISCORDUTILS_SENDTODISCORD_USAGE.send(sender, true);
            return;
        }

        Main.getInstance().getBot().getJda().getGuilds().forEach(guild -> {
            String text = "";
            for(int i = 2; i < args.length; i++) text += args[i] + " ";
            text = text.trim();

            Color color;
            try {
                color = Color.decode(args[1]);
            } catch (Exception e) {
                color = null;
            }

            if(color == null) {
                Message.INVALID_COLOR_VALUE.send(sender, true);
                return;
            }

            Main.getInstance().getBot().getMessagesTextChannel().sendMessageEmbeds(new EmbedManager().embed(args[0], text.replace("\\n", "\n"), color, Message.SENDTODISCORD_SENT_BY.getText().getText().replace("%sender%", sender.getName()))).queue();
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

}
