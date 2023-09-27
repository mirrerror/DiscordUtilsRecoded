package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsBot;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CommandListener extends ListenerAdapter {

    private final DiscordUtilsBot bot = Main.getInstance().getBot();
    private final EmbedManager embedManager = new EmbedManager();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.isWebhookMessage()) return;
        if(!event.getMessage().getContentRaw().startsWith(bot.getPrefix())) return;
        if(!event.isFromGuild()) return;

        List<Long> botCommandTextChannels = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLongList("BotCommandTextChannels");
        if(!botCommandTextChannels.isEmpty()) {
            if(!Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLongList("BotCommandTextChannels").contains(event.getChannel().getIdLong())) {
                event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.COMMANDS_ARE_NOT_WORKING_IN_THIS_CHANNEL.getText().getText())).queue();
                return;
            }
        }
        String[] args = event.getMessage().getContentRaw().replaceFirst(bot.getPrefix(), "").split(" ");

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getAuthor().getIdLong());

        switch (args[0]) {
            case "link": {
                if(discordUtilsUser.isLinked()) {
                    event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_ALREADY_VERIFIED.getText().getText())).queue();
                    return;
                }
                if(bot.getLinkCodes().containsValue(event.getAuthor().getIdLong())) {
                    event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.LINK_ALREADY_INITIATED.getText().getText())).queue();
                    return;
                }
                AtomicReference<String> code = new AtomicReference<>("");
                byte[] secureRandomSeed = new SecureRandom().generateSeed(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getInt("CodeLength"));
                for(byte b : secureRandomSeed) code.set(code.get() + b);
                code.set(code.get().replace("-", "").trim());

                event.getAuthor().openPrivateChannel().submit()
                        .thenCompose(channel -> channel.sendMessageEmbeds(embedManager.infoEmbed(Message.VERIFICATION_CODE_MESSAGE.getText().getText().replace("%code%", code.get()))).submit())
                        .whenComplete((msg, error) -> {
                            if(error == null) {
                                event.getChannel().sendMessageEmbeds(embedManager.successfulEmbed(Message.VERIFICATION_MESSAGE.getText().getText())).queue();
                                bot.getLinkCodes().put(code.get(), event.getAuthor().getIdLong());
                                return;
                            }
                            event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.CAN_NOT_SEND_MESSAGE.getText().getText())).queue();
                        });
                break;
            }
            case "online": {
                event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(Message.ONLINE.getText().getText().replace("%online%", "" + Bukkit.getOnlinePlayers().size()))).queue();
                break;
            }
            case "sudo": {
                if(!discordUtilsUser.isLinked()) {
                    event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText().getText())).queue();
                    return;
                }

                if(!discordUtilsUser.isAdmin(event.getGuild())) {
                    event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText().getText())).queue();
                    return;
                }
                if(args.length < 2) {
                    event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(Message.DISCORD_SUDO_USAGE.getText().getText())).queue();
                    return;
                }
                AtomicReference<String> command = new AtomicReference<>("");
                for(int i = 1; i < args.length; i++) command.set(command.get() + args[i] + " ");
                command.set(command.get().trim());

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.get()));
                event.getChannel().sendMessageEmbeds(embedManager.successfulEmbed(Message.COMMAND_EXECUTED.getText().getText())).queue();
                break;
            }
            case "embed": {
                if(!discordUtilsUser.isLinked()) {
                    event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText().getText())).queue();
                    return;
                }

                if(!discordUtilsUser.isAdmin(event.getGuild())) {
                    event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText().getText())).queue();
                    return;
                }
                if(args.length < 4) {
                    event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(Message.DISCORD_EMBED_USAGE.getText().getText())).queue();
                    return;
                }
                String text = "";
                for(int i = 3; i < args.length; i++) text += args[i] + " ";
                text = text.trim();

                Color color;
                try {
                    color = Color.decode(args[2]);
                } catch (Exception e) {
                    color = null;
                }

                if(color == null) {
                    event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.INVALID_COLOR_VALUE.getText().getText())).queue();
                    return;
                }

                event.getChannel().sendMessageEmbeds(embedManager.embed(args[1], text, color, Message.EMBED_SENT_BY.getText().getText().replace("%sender%",
                        event.getAuthor().getAsTag()))).queue();
                break;
            }
            case "stats": {

                if(event.getMessage().getMentions().getMembers().size() > 0) {
                    for(Member member : event.getMessage().getMentions().getMembers()) {
                        DiscordUtilsUser disUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(member.getUser().getIdLong());

                        if(!disUtilsUser.isLinked()) {
                            event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText().getText())).queue();
                            return;
                        }

                        String messageToSend = "";
                        for (TextComponent t : Message.STATS_FORMAT.getTextList()) {
                            messageToSend += t.getText() + "\n";
                        }

                        messageToSend = Main.getInstance().getPapiManager().setPlaceholders(disUtilsUser.getOfflinePlayer(), messageToSend);

                        event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(messageToSend)).queue();
                    }
                } else {
                    OfflinePlayer player;

                    if(args.length > 1) {

                        player = Bukkit.getOfflinePlayer(args[1]);

                    } else {

                        if(!discordUtilsUser.isLinked()) {
                            event.getChannel().sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText().getText())).queue();
                            return;
                        }

                        player = discordUtilsUser.getOfflinePlayer();

                    }

                    String messageToSend = "";
                    for (TextComponent t : Message.STATS_FORMAT.getTextList()) {
                        messageToSend += t.getText() + "\n";
                    }

                    messageToSend = Main.getInstance().getPapiManager().setPlaceholders(player, messageToSend);

                    event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(messageToSend)).queue();
                }

                break;
            }
            case "help": {

                String messageToSend = "";
                for (TextComponent t : Message.DISCORD_HELP.getTextList()) {
                    messageToSend += t.getText() + "\n";
                }

                event.getChannel().sendMessageEmbeds(embedManager.infoEmbed(messageToSend)).queue();

                break;
            }
        }
    }

}
