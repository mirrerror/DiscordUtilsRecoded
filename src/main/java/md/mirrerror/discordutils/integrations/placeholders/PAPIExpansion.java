package md.mirrerror.discordutils.integrations.placeholders;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIExpansion extends PlaceholderExpansion {
    @Override
    public String getAuthor() {
        return "mirrerror";
    }

    @Override
    public String getIdentifier() {
        return "discordutils";
    }

    @Override
    public String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(params.equalsIgnoreCase("islinked")){
            if(discordUtilsUser.isLinked()) return Message.YES.getText();
            else return Message.NO.getText();
        }

        if(params.equalsIgnoreCase("discord")) {
            if(!discordUtilsUser.isLinked()) return Message.NOT_AVAILABLE.getText();
            return discordUtilsUser.getUser().getName();
        }

        if(params.equalsIgnoreCase("2fa")) {
            if(discordUtilsUser.isSecondFactorEnabled()) return Message.YES.getText();
            else return Message.NO.getText();
        }

        return null;
    }

}
