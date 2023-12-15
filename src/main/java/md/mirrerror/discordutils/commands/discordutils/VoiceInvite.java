package md.mirrerror.discordutils.commands.discordutils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.utils.Validator;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VoiceInvite implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validatePlayerSender(sender)) return;

        Player player = (Player) sender;
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());
        if(!discordUtilsUser.isLinked()) {
            Message.ACCOUNT_IS_NOT_VERIFIED.send(sender, true);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Main.getInstance().getBot().getJda().getGuilds().forEach(guild -> {
                Member member = guild.getMemberById(discordUtilsUser.getUser().getIdLong());
                if(member != null) {
                    if(member.getVoiceState().getChannel() == null) {
                        Message.SENDER_IS_NOT_IN_A_VOICE_CHANNEL.send(sender, true);
                        return;
                    }

                    String url = Main.getInstance().getBot().createVoiceInviteUrl(member, 15L, TimeUnit.MINUTES);

                    TextComponent textComponent = new TextComponent(Message.VOICE_INVITE.getText(true).replace("%sender%", player.getName()));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Message.VOICE_INVITE_HOVER.getText(false)).create()));

                    Message.VOICE_INVITE_SENT.send(sender, true);
                    Bukkit.getOnlinePlayers().forEach(online -> online.spigot().sendMessage(textComponent));
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
        return Collections.unmodifiableList(Arrays.asList("vinvite", "vcinvite", "vinv", "vcinv"));
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
