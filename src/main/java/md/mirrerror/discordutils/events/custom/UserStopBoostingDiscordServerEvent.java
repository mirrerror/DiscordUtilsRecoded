package md.mirrerror.discordutils.events.custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public class UserStopBoostingDiscordServerEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DiscordUtilsUser discordUtilsUser;
    private final DiscordUtilsBot bot;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
