package md.mirrerror.discordutils.commands.discordutils;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.utils.Validator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Link implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validatePlayerSender(sender)) return;

        Player player = (Player) sender;
        if(DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId()).isLinked()) {
            Message.ACCOUNT_ALREADY_VERIFIED.send(sender, true);
            return;
        }

        if(!Main.getInstance().getBot().getLinkCodes().containsKey(args[0])) {
            Message.INVALID_LINK_CODE.send(sender, true);
            return;
        }

        boolean defaultSecondFactorValue = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("Default2FAValue");
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        discordUtilsUser.setUser(Main.getInstance().getBot().getJda().getUserById(Main.getInstance().getBot().getLinkCodes().get(args[0])));
        discordUtilsUser.setSecondFactor(defaultSecondFactorValue);

        if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("VerifiedRole.Enabled")) {
            Main.getInstance().getBot().getJda().getGuilds().forEach(guild -> {
                Role verifiedRole = Main.getInstance().getBot().getVerifiedRole();
                Member member = guild.getMemberById(Main.getInstance().getBot().getLinkCodes().get(args[0]));
                if(verifiedRole != null && member != null) {
                    try {
                        guild.addRoleToMember(member, verifiedRole).queue();
                    } catch (HierarchyException ignored) {}
                }
            });
        }

        Main.getInstance().getBot().getLinkCodes().remove(args[0]);
        Message.ACCOUNT_SUCCESSFULLY_LINKED.send(sender, true);
        Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration()
                .getStringList("CommandsAfterVerification")
                .forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutils.link";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public int getMinArgsNeeded() {
        return 1;
    }

    @Override
    public Message getIncorrectUsageErrorMessage() {
        return Message.DISCORDUTILS_LINK_USAGE;
    }

}
