package md.mirrerror.discordutils.models;

import lombok.Getter;
import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.data.DataManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;

import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
public class DiscordUtilsUser {

    private final DataManager dataManager;
    private final DiscordUtilsBot bot;

    private User user;
    private final OfflinePlayer offlinePlayer;
    private boolean secondFactorEnabled;
    private OffsetDateTime lastBoostingTime;

    public DiscordUtilsUser(DiscordUtilsBot discordUtilsBot, DataManager dataManager, OfflinePlayer offlinePlayer, User user, boolean hasSecondFactor, OffsetDateTime lastBoostingTime) {
        this.bot = discordUtilsBot;
        this.dataManager = dataManager;
        this.offlinePlayer = offlinePlayer;
        this.user = user;
        this.secondFactorEnabled = hasSecondFactor;
        this.lastBoostingTime = lastBoostingTime;
    }

    public void setUser(User user) {
        this.user = user;
        dataManager.setDiscordUserId(offlinePlayer.getUniqueId(), user.getIdLong());
        DiscordUtilsUsersCacheManager.addToCache(this);
    }

    public void setSecondFactor(boolean secondFactor) {
        this.secondFactorEnabled = secondFactor;
        dataManager.setSecondFactor(offlinePlayer.getUniqueId(), secondFactor);
        DiscordUtilsUsersCacheManager.addToCache(this);
    }

    public void setLastBoostingTime(OffsetDateTime lastBoostingTime) {
        this.lastBoostingTime = lastBoostingTime;
        dataManager.setLastBoostingTime(offlinePlayer.getUniqueId(), lastBoostingTime);
        DiscordUtilsUsersCacheManager.addToCache(this);
    }

    public boolean isLinked() {
        return offlinePlayer != null && user != null;
    }

    public boolean isAdmin(Guild guild) {
        return bot.isAdmin(guild, user);
    }

    public void synchronizeRoles(Guild guild) {
        bot.synchronizeRoles(guild, this);
        bot.synchronizeRolesToGroups(guild, this);
    }

    public void synchronizeNickname(Guild guild) {
        bot.synchronizeNickname(guild, this);
    }

    public void unregister() {
        this.user = null;
        this.secondFactorEnabled = false;
        Main.getInstance().getDataManager().unregisterUser(offlinePlayer.getUniqueId());
        DiscordUtilsUsersCacheManager.addToCache(this);
    }

    public boolean isSecondFactorAuthorized() {
        return bot.isSecondFactorAuthorized(offlinePlayer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscordUtilsUser that = (DiscordUtilsUser) o;
        return Objects.equals(user, that.user) && Objects.equals(offlinePlayer, that.offlinePlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, offlinePlayer);
    }
}
