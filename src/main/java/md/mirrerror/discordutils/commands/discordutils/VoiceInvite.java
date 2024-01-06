package md.mirrerror.discordutils.commands.discordutils;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.utils.Validator;
import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class VoiceInvite implements SubCommand {

    private final DiscordUtilsBot bot;
    private final Plugin plugin;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validatePlayerSender(sender)) return;

        Player player = (Player) sender;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(!Validator.validateLinkedUser(sender, discordUtilsUser)) return;

        List<Player> players = new LinkedList<>();
        for(String playerName : args)
            if(Validator.validateOnlinePlayer(sender, playerName))
                players.add(Bukkit.getPlayer(playerName));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            bot.getJda().getGuilds().forEach(guild -> {
                Member member = guild.getMemberById(discordUtilsUser.getUser().getIdLong());
                if(member != null) {
                    if(!Validator.validateVoiceChannelPresence(sender, member)) return;

                    String url = bot.createVoiceInviteUrl(member, 15L, TimeUnit.MINUTES);

                    TextComponent textComponent = new TextComponent(Message.VOICE_INVITE.getText(true).replace("%sender%", player.getName()));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Message.VOICE_INVITE_HOVER.getText(false)).create()));

                    if(players.isEmpty() && args.length == 0)
                        Bukkit.getOnlinePlayers().forEach(online -> online.spigot().sendMessage(textComponent));
                    else
                        for(Player entry : players) entry.spigot().sendMessage(textComponent);

                    Message.VOICE_INVITE_SENT.send(sender, true);
                }
            });
        });
    }

    @Override
    public String getName() {
        return "voiceinvite";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutils.voiceinvite";
    }

    @Override
    public List<String> getAliases() {
        return List.of("vinvite", "vcinvite", "vinv", "vcinv");
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
