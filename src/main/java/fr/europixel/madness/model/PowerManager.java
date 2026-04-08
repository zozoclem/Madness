package fr.europixel.madness.model;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PowerManager {

    private final MadnessPlugin plugin;
    private final Map<UUID, Map<PowerType, Long>> cooldowns;

    public PowerManager(MadnessPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<UUID, Map<PowerType, Long>>();
    }

    public boolean isOnCooldown(Player player, PowerType type) {
        Map<PowerType, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) {
            return false;
        }

        Long end = playerCooldowns.get(type);
        if (end == null) {
            return false;
        }

        return System.currentTimeMillis() < end.longValue();
    }

    public int getRemaining(Player player, PowerType type) {
        Map<PowerType, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) {
            return 0;
        }

        Long end = playerCooldowns.get(type);
        if (end == null) {
            return 0;
        }

        long diff = end.longValue() - System.currentTimeMillis();
        if (diff <= 0L) {
            return 0;
        }

        return (int) Math.ceil(diff / 1000.0D);
    }

    public void startCooldown(Player player, PowerType type, int seconds) {
        if (seconds <= 0) {
            return;
        }

        Map<PowerType, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) {
            playerCooldowns = new HashMap<PowerType, Long>();
            cooldowns.put(player.getUniqueId(), playerCooldowns);
        }

        long end = System.currentTimeMillis() + (seconds * 1000L);
        playerCooldowns.put(type, Long.valueOf(end));
    }

    public boolean use(Player player, PowerType type, int cooldownSeconds) {
        if (isOnCooldown(player, type)) {
            int remaining = getRemaining(player, type);
            player.sendMessage("§cPower en cooldown: §f" + remaining + "s");
            return false;
        }

        startCooldown(player, type, cooldownSeconds);
        return true;
    }
}