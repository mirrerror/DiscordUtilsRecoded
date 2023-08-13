package md.mirrerror.discordutils.commands.discordutilsadmin;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ForceUnlink implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            Message.DISCORDUTILSADMIN_FORCEUNLINK_USAGE.send(sender, true);
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(!discordUtilsUser.isLinked()) {
            Message.ACCOUNT_IS_NOT_VERIFIED.send(sender, true);
            return;
        }

        Main.getInstance().getBot().getJda().getGuilds().forEach(guild -> {
            Role verifiedRole = Main.getInstance().getBot().getVerifiedRole();
            Member member = guild.getMemberById(discordUtilsUser.getUser().getIdLong());
            if(verifiedRole != null && member != null) {
                try {
                    guild.removeRoleFromMember(member, verifiedRole).queue();
                } catch (HierarchyException ignored) {}
            }
        });

        if(player.isOnline()) {
            Player onlinePlayer = player.getPlayer();
            Main.getInstance().getBot().getUnlinkPlayers().remove(onlinePlayer.getUniqueId());
            onlinePlayer.sendMessage(Message.DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_TARGET.getText(true).getText().replace("%sender%", sender.getName()).replace("%target%", player.getName()));
        }

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            Main.getInstance().getConfigManager().getConfig().getFileConfiguration().getStringList("Discord.CommandsAfterUnlink").forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));
        });
        discordUtilsUser.unregister();

        sender.sendMessage(Message.DISCORDUTILSADMIN_FORCEUNLINK_SUCCESSFUL_TO_SENDER.getText(true).getText().replace("%sender%", sender.getName()).replace("%target%", player.getName()));
    }

    @Override
    public String getName() {
        return "forceunlink";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutilsadmin.forceunlink";
    }

    @Override
    public List<String> getAliases() {
        return Collections.unmodifiableList(Arrays.asList("funlink", "fulink", "forceulink"));
    }

}
