package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.utils.DiscordValidator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class CommandListener extends ListenerAdapter {

    private final DiscordUtilsBot bot = Main.getInstance().getBot();
    private final EmbedManager embedManager = new EmbedManager();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.isWebhookMessage()) return;
        if(!event.getMessage().getContentRaw().startsWith(bot.getPrefix())) return;
        if(!event.isFromGuild()) return;

        if(!DiscordValidator.validateCommandChannel(event.getChannel())) return;

        String[] args = event.getMessage().getContentRaw().replaceFirst(bot.getPrefix(), "").split(" ");

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getAuthor().getIdLong());

        switch (args[0]) {
            case "link": {
                if(!DiscordValidator.validateNotLinkedUser(event.getChannel(), discordUtilsUser)) return;
                if(!DiscordValidator.validateLinkAvailability(event.getChannel(), event.getAuthor())) return;

                bot.startLinkingProcess(event.getAuthor(), event.getChannel());

                break;
            }
            case "online": {
                event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(Message.ONLINE.getText().replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())))).queue();
                break;
            }
            case "sudo": {
                if(!DiscordValidator.validateLinkedUser(event.getChannel(), discordUtilsUser)) return;
                if(!DiscordValidator.validateAdminPermissions(event.getChannel(), event.getGuild(), discordUtilsUser)) return;

                if(args.length < 2) {
                    event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(Message.DISCORD_SUDO_USAGE.getText())).queue();
                    return;
                }

                AtomicReference<String> command = new AtomicReference<>("");
                for(int i = 1; i < args.length; i++) command.set(command.get() + args[i] + " ");
                command.set(command.get().trim());

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.get()));
                event.getChannel().sendMessageEmbeds(embedManager.successfulEmbed(Message.COMMAND_EXECUTED.getText())).queue();
                break;
            }
            case "embed": {
                if(!DiscordValidator.validateLinkedUser(event.getChannel(), discordUtilsUser)) return;
                if(!DiscordValidator.validateAdminPermissions(event.getChannel(), event.getGuild(), discordUtilsUser)) return;

                if(args.length < 4) {
                    event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(Message.DISCORD_EMBED_USAGE.getText())).queue();
                    return;
                }

                StringBuilder text = new StringBuilder();
                for(int i = 3; i < args.length; i++) text.append(args[i]).append(" ");

                Color color;
                try {
                    color = Color.decode(args[2]);
                } catch (Exception e) {
                    color = null;
                }

                if(!DiscordValidator.validateColor(event.getChannel(), color)) return;

                event.getChannel().sendMessageEmbeds(embedManager.embed(args[1], text.toString().trim(), color, Message.EMBED_SENT_BY.getText().replace("%sender%",
                        event.getAuthor().getName()))).queue();
                break;
            }
            case "stats": {

                if(event.getMessage().getMentions().getMembers().size() > 0) {
                    for(Member member : event.getMessage().getMentions().getMembers()) {
                        DiscordUtilsUser disUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(member.getUser().getIdLong());

                        if(!DiscordValidator.validateLinkedUser(event.getChannel(), disUtilsUser)) return;

                        StringBuilder messageToSend = new StringBuilder();
                        for (String s : Message.STATS_FORMAT.getTextList()) {
                            messageToSend.append(s).append("\n");
                        }

                        messageToSend = new StringBuilder(Main.getInstance().getPapiManager().setPlaceholders(disUtilsUser.getOfflinePlayer(), messageToSend.toString()));

                        event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(messageToSend.toString())).queue();
                    }
                } else {
                    OfflinePlayer player;

                    if(args.length > 1) {

                        player = Bukkit.getOfflinePlayer(args[1]);

                    } else {

                        if(!DiscordValidator.validateLinkedUser(event.getChannel(), discordUtilsUser)) return;

                        player = discordUtilsUser.getOfflinePlayer();

                    }

                    StringBuilder messageToSend = new StringBuilder();
                    for (String s : Message.STATS_FORMAT.getTextList()) {
                        messageToSend.append(s).append("\n");
                    }

                    messageToSend = new StringBuilder(Main.getInstance().getPapiManager().setPlaceholders(player, messageToSend.toString()));

                    event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(messageToSend.toString())).queue();
                }

                break;
            }
            case "help": {

                StringBuilder messageToSend = new StringBuilder();
                for (String s : Message.DISCORD_HELP.getTextList()) {
                    messageToSend.append(s).append("\n");
                }

                event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(messageToSend.toString())).queue();

                break;
            }
        }
    }

}
