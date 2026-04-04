package fr.europixel.madness;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerModeManager {

    private final Map<UUID, PlayerMode> modes = new HashMap<UUID, PlayerMode>();

    public void setMode(Player player, PlayerMode mode) {
        modes.put(player.getUniqueId(), mode);
    }

    public PlayerMode getMode(Player player) {
        PlayerMode mode = modes.get(player.getUniqueId());
        if (mode == null) {
            return PlayerMode.LOBBY;
        }
        return mode;
    }

    public boolean isInLobby(Player player) {
        return getMode(player) == PlayerMode.LOBBY;
    }

    public boolean isInArena(Player player) {
        return getMode(player) == PlayerMode.ARENA;
    }

    public void remove(Player player) {
        modes.remove(player.getUniqueId());
    }
}