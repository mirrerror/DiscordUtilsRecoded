package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsBot;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SlashCommandsListener extends ListenerAdapter {

    private final DiscordUtilsBot bot = Main.getInstance().getBot();
    private final EmbedManager embedManager = new EmbedManager();

    public SlashCommandsListener(List<Guild> guilds) {
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("link", Message.LINK_SLASH_COMMAND_DESCRIPTION.getText()));
        commandData.add(Commands.slash("online", Message.ONLINE_SLASH_COMMAND_DESCRIPTION.getText()));
        commandData.add(Commands.slash("sudo", Message.SUDO_SLASH_COMMAND_DESCRIPTION.getText())
                .addOption(OptionType.STRING, Message.SUDO_SLASH_COMMAND_FIRST_ARGUMENT_NAME.getText(),
                        Message.SUDO_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION.getText(), true));
        commandData.add(Commands.slash("embed", Message.EMBED_SLASH_COMMAND_DESCRIPTION.getText())
                .addOption(OptionType.STRING, Message.EMBED_SLASH_COMMAND_FIRST_ARGUMENT_NAME.getText(),
                        Message.EMBED_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION.getText(), true)
                .addOption(OptionType.STRING, Message.EMBED_SLASH_COMMAND_SECOND_ARGUMENT_NAME.getText(),
                        Message.EMBED_SLASH_COMMAND_SECOND_ARGUMENT_DESCRIPTION.getText(), true)
                .addOption(OptionType.STRING, Message.EMBED_SLASH_COMMAND_THIRD_ARGUMENT_NAME.getText(),
                        Message.EMBED_SLASH_COMMAND_THIRD_ARGUMENT_DESCRIPTION.getText(), true));
        commandData.add(Commands.slash("stats", Message.STATS_SLASH_COMMAND_DESCRIPTION.getText())
                .addOption(OptionType.STRING, Message.STATS_SLASH_COMMAND_FIRST_ARGUMENT_NAME.getText(),
                        Message.STATS_SLASH_COMMAND_FIRST_ARGUMENT_DESCRIPTION.getText(), false));
        commandData.add(Commands.slash("help", Message.HELP_SLASH_COMMAND_DESCRIPTION.getText()));

        for(Guild guild : guilds) guild.updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getUser().isBot()) return;
        if(!event.isFromGuild()) return;

        List<Long> botCommandTextChannels = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLongList("BotCommandTextChannels");
        if(!botCommandTextChannels.isEmpty()) {
            if(!Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLongList("BotCommandTextChannels").contains(event.getChannel().getIdLong())) {
                event.replyEmbeds(embedManager.errorEmbed(Message.COMMANDS_ARE_NOT_WORKING_IN_THIS_CHANNEL.getText())).queue();
                return;
            }
        }

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUser().getIdLong());

        switch(event.getName()) {
            case "link": {
                event.deferReply().queue();
                if(discordUtilsUser.isLinked()) {
                    event.getHook().sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_ALREADY_VERIFIED.getText())).queue();
                    return;
                }
                if(bot.getLinkCodes().containsValue(event.getUser().getIdLong())) {
                    event.getHook().sendMessageEmbeds(embedManager.errorEmbed(Message.LINK_ALREADY_INITIATED.getText())).queue();
                    return;
                }
                AtomicReference<String> code = new AtomicReference<>("");
                byte[] secureRandomSeed = new SecureRandom().generateSeed(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getInt("CodeLength"));
                for(byte b : secureRandomSeed) code.set(code.get() + b);
                code.set(code.get().replace("-", "").trim());

                event.getUser().openPrivateChannel().submit()
                        .thenCompose(channel -> channel.sendMessageEmbeds(embedManager.infoEmbed(Message.VERIFICATION_CODE_MESSAGE.getText().replace("%code%", code.get()))).submit())
                        .whenComplete((msg, error) -> {
                            if(error == null) {
                                event.getHook().sendMessageEmbeds(embedManager.successfulEmbed(Message.VERIFICATION_MESSAGE.getText())).queue();
                                bot.getLinkCodes().put(code.get(), event.getUser().getIdLong());
                                return;
                            }
                            event.getHook().sendMessageEmbeds(embedManager.errorEmbed(Message.CAN_NOT_SEND_MESSAGE.getText())).queue();
                        });
                break;
            }
            case "online": {
                event.replyEmbeds(embedManager.infoEmbed(Message.ONLINE.getText().replace("%online%", "" + Bukkit.getOnlinePlayers().size()))).queue();
                break;
            }
            case "sudo": {
                event.deferReply().queue();

                if(!discordUtilsUser.isLinked()) {
                    event.getHook().sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText())).queue();
                    return;
                }

                if(!discordUtilsUser.isAdmin(event.getGuild())) {
                    event.getHook().sendMessageEmbeds(embedManager.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText())).queue();
                    return;
                }

                String command = event.getOption(Message.SUDO_SLASH_COMMAND_FIRST_ARGUMENT_NAME.getText()).getAsString();

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                event.getHook().sendMessageEmbeds(embedManager.successfulEmbed(Message.COMMAND_EXECUTED.getText())).queue();
                break;
            }
            case "embed": {
                event.deferReply().queue();

                if(!discordUtilsUser.isLinked()) {
                    event.getHook().sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText())).queue();
                    return;
                }

                if(!discordUtilsUser.isAdmin(event.getGuild())) {
                    event.getHook().sendMessageEmbeds(embedManager.errorEmbed(Message.INSUFFICIENT_PERMISSIONS.getText())).queue();
                    return;
                }

                String title = event.getOption(Message.EMBED_SLASH_COMMAND_FIRST_ARGUMENT_NAME.getText()).getAsString();
                String text = event.getOption(Message.EMBED_SLASH_COMMAND_THIRD_ARGUMENT_NAME.getText()).getAsString();

                Color color;
                try {
                    color = Color.decode(event.getOption(Message.EMBED_SLASH_COMMAND_SECOND_ARGUMENT_NAME.getText()).getAsString());
                } catch (Exception e) {
                    color = null;
                }

                if(color == null) {
                    event.getHook().sendMessageEmbeds(embedManager.errorEmbed(Message.INVALID_COLOR_VALUE.getText())).queue();
                    return;
                }

                event.getHook().sendMessageEmbeds(embedManager.embed(title, text, color, Message.EMBED_SENT_BY.getText().replace("%sender%",
                        event.getUser().getAsTag()))).queue();
                break;
            }
            case "stats": {
                event.deferReply().queue();

                OfflinePlayer player;
                OptionMapping firstArg = event.getOption(Message.STATS_SLASH_COMMAND_FIRST_ARGUMENT_NAME.getText());
                if(firstArg == null) {
                    if(!discordUtilsUser.isLinked()) {
                        event.getHook().sendMessageEmbeds(embedManager.errorEmbed(Message.ACCOUNT_IS_NOT_VERIFIED.getText())).queue();
                        return;
                    }
                    player = discordUtilsUser.getOfflinePlayer();
                } else {
                    player = Bukkit.getOfflinePlayer(firstArg.getAsString());
                }

                String messageToSend = "";
                for (String s : Message.STATS_FORMAT.getTextList()) {
                    messageToSend += s + "\n";
                }

                messageToSend = Main.getInstance().getPapiManager().setPlaceholders(player, messageToSend);

                event.getHook().sendMessageEmbeds(embedManager.infoEmbed(messageToSend)).queue();

                break;
            }
            case "help": {
                event.deferReply().queue();
                String messageToSend = "";
                for (String s : Message.DISCORD_HELP.getTextList()) {
                    messageToSend += s + "\n";
                }

                event.getHook().sendMessageEmbeds(embedManager.infoEmbed(messageToSend)).queue();

                break;
            }
        }
    }

}
