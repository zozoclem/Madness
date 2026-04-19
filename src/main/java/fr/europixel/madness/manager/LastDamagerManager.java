package fr.europixel.madness.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LastDamagerManager {

    private static class DamageRecord {
        private final UUID damagerUuid;
        private final long timestamp;

        private DamageRecord(UUID damagerUuid, long timestamp) {
            this.damagerUuid = damagerUuid;
            this.timestamp = timestamp;
        }
    }

    private final Map<UUID, DamageRecord> lastDamagers = new HashMap<UUID, DamageRecord>();
    private final long validityMillis;

    public LastDamagerManager(long validityMillis) {
        this.validityMillis = validityMillis;
    }

    public void setLastDamager(Player victim, Player damager) {
        if (victim == null || damager == null) {
            return;
        }

        if (victim.getUniqueId().equals(damager.getUniqueId())) {
            return;
        }

        lastDamagers.put(victim.getUniqueId(), new DamageRecord(
                damager.getUniqueId(),
                System.currentTimeMillis()
        ));
    }

    public Player getLastDamager(Player victim) {
        if (victim == null) {
            return null;
        }

        DamageRecord record = lastDamagers.get(victim.getUniqueId());
        if (record == null) {
            return null;
        }

        long now = System.currentTimeMillis();
        if ((now - record.timestamp) > validityMillis) {
            lastDamagers.remove(victim.getUniqueId());
            return null;
        }

        Player damager = victim.getServer().getPlayer(record.damagerUuid);
        if (damager == null || !damager.isOnline()) {
            return null;
        }

        return damager;
    }

    public boolean hasValidLastDamager(Player victim) {
        return getLastDamager(victim) != null;
    }

    public void clear(Player victim) {
        if (victim == null) {
            return;
        }

        lastDamagers.remove(victim.getUniqueId());
    }

    public void clearAll() {
        lastDamagers.clear();
    }
}