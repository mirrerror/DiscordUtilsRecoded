package md.mirrerror.discordutils.commands.discordutils;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.utils.Validator;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.awt.*;

@RequiredArgsConstructor
public class SendToDiscord implements SubCommand {

    private final DiscordUtilsBot bot;
    private final BotSettings botSettings;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        TextChannel textChannel = bot.getJda().getTextChannelById(args[0]);

        if(!Validator.validateTextChannel(sender, textChannel)) return;

        StringBuilder text = new StringBuilder();
        for(int i = 3; i < args.length; i++) text.append(args[i]).append(" ");

        Color color;
        try {
            color = Color.decode(args[2]);
        } catch (Exception e) {
            color = null;
        }

        if(!Validator.validateColor(sender, color)) return;

        bot.sendMessageEmbed(textChannel,
                new EmbedManager(botSettings).embed(args[1], text.toString().trim().replace("\\n", "\n"), color, Message.SENDTODISCORD_SENT_BY.getText().replace("%sender%", sender.getName())));
        Message.DISCORDUTILS_SENDTODISCORD_SUCCESSFUL.send(sender, true);
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
        return java.util.List.of("sendtodis", "std", "stodis", "stdis");
    }

    @Override
    public int getMinArgsNeeded() {
        return 4;
    }

    @Override
    public Message getIncorrectUsageErrorMessage() {
        return Message.DISCORDUTILS_SENDTODISCORD_USAGE;
    }

}
