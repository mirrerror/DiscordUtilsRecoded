package md.mirrerror.discordutils.discord.listeners;

import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.integrations.placeholders.PAPIManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.utils.DiscordValidator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SlashCommandsListener extends ListenerAdapter {

    private final DiscordUtilsBot bot;
    private final BotSettings botSettings;
    private final Plugin plugin;
    private final PAPIManager papiManager;
    private final EmbedManager embedManager;

    public SlashCommandsListener(DiscordUtilsBot bot, Plugin plugin, PAPIManager papiManager, BotSettings botSettings, List<Guild> guilds) {
        this.bot = bot;
        this.botSettings = botSettings;
        this.plugin = plugin;
        this.papiManager = papiManager;
        this.embedManager = new EmbedManager(botSettings);

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
        commandData.add(Commands.slash("unlink", Message.UNLINK_SLASH_COMMAND_DESCRIPTION.getText()));

        for(Guild guild : guilds) guild.updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getUser().isBot()) return;
        if(!event.isFromGuild()) return;

        if(!DiscordValidator.validateCommandChannel(event)) return;

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUserId(event.getUser().getIdLong());

        switch(event.getName()) {
            case "link": {
                event.deferReply().queue();

                if(!DiscordValidator.validateNotLinkedUser(event.getHook(), discordUtilsUser)) return;
                if(!DiscordValidator.validateLinkAvailability(event.getHook(), event.getUser())) return;

                bot.startLinkingProcess(event.getUser(), event.getHook());

                break;
            }
            case "online": {
                event.replyEmbeds(embedManager.infoEmbed(Message.ONLINE.getText().replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())))).queue();
                break;
            }
            case "sudo": {
                event.deferReply().queue();

                if(!DiscordValidator.validateLinkedUser(event.getHook(), discordUtilsUser)) return;
                if(!DiscordValidator.validateAdminPermissions(event.getHook(), event.getGuild(), discordUtilsUser)) return;

                String command = event.getOption(Message.SUDO_SLASH_COMMAND_FIRST_ARGUMENT_NAME.getText()).getAsString();

                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                event.getHook().sendMessageEmbeds(embedManager.successfulEmbed(Message.COMMAND_EXECUTED.getText())).queue();
                break;
            }
            case "embed": {
                event.deferReply().queue();

                if(!DiscordValidator.validateLinkedUser(event.getHook(), discordUtilsUser)) return;
                if(!DiscordValidator.validateAdminPermissions(event.getHook(), event.getGuild(), discordUtilsUser)) return;

                String title = event.getOption(Message.EMBED_SLASH_COMMAND_FIRST_ARGUMENT_NAME.getText()).getAsString();
                String text = event.getOption(Message.EMBED_SLASH_COMMAND_THIRD_ARGUMENT_NAME.getText()).getAsString();

                Color color;
                try {
                    color = Color.decode(event.getOption(Message.EMBED_SLASH_COMMAND_SECOND_ARGUMENT_NAME.getText()).getAsString());
                } catch (Exception e) {
                    color = null;
                }

                if(!DiscordValidator.validateColor(event.getHook(), color)) return;

                event.getHook().sendMessageEmbeds(embedManager.embed(title, text, color, Message.EMBED_SENT_BY.getText().replace("%sender%",
                        event.getUser().getName()))).queue();
                break;
            }
            case "stats": {
                event.deferReply().queue();

                OfflinePlayer player;
                OptionMapping firstArg = event.getOption(Message.STATS_SLASH_COMMAND_FIRST_ARGUMENT_NAME.getText());
                if(firstArg == null) {
                    if(!DiscordValidator.validateLinkedUser(event.getHook(), discordUtilsUser)) return;
                    player = discordUtilsUser.getOfflinePlayer();
                } else {
                    player = Bukkit.getOfflinePlayer(firstArg.getAsString());
                }

                StringBuilder messageToSend = new StringBuilder();
                for (String s : Message.STATS_FORMAT.getTextList()) {
                    messageToSend.append(s).append("\n");
                }

                messageToSend = new StringBuilder(papiManager.setPlaceholders(player, messageToSend.toString()));

                event.getHook().sendMessageEmbeds(embedManager.infoEmbed(messageToSend.toString())).queue();

                break;
            }
            case "help": {
                event.deferReply().queue();

                StringBuilder messageToSend = new StringBuilder();
                for (String s : Message.DISCORD_HELP.getTextList()) {
                    messageToSend.append(s).append("\n");
                }

                event.getHook().sendMessageEmbeds(embedManager.infoEmbed(messageToSend.toString())).queue();

                break;
            }
            case "unlink": {
                event.deferReply().queue();

                if(!DiscordValidator.validateLinkedUser(event.getHook(), discordUtilsUser)) return;

                bot.unAssignVerifiedRole(discordUtilsUser.getUser().getIdLong());

                bot.getUnlinkPlayers().remove(discordUtilsUser.getOfflinePlayer().getUniqueId());
                if(discordUtilsUser.getOfflinePlayer().isOnline()) Message.ACCOUNT_SUCCESSFULLY_UNLINKED.send(discordUtilsUser.getOfflinePlayer().getPlayer(), true);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    botSettings.COMMANDS_AFTER_UNLINKING.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", discordUtilsUser.getOfflinePlayer().getName())));
                });

                discordUtilsUser.unregister();

                event.getHook().sendMessageEmbeds(embedManager.infoEmbed(Message.ACCOUNT_SUCCESSFULLY_UNLINKED.getText())).queue();

                break;
            }
        }
    }

}
