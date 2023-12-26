package md.mirrerror.discordutils.integrations.placeholders;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PAPIExpansion extends PlaceholderExpansion {

    private final Plugin plugin;

    @Override
    public @NotNull String getAuthor() {
        return "mirrerror";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "discordutils";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
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
