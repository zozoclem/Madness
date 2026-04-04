package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemRechargeManager {

    private final MadnessPlugin plugin;

    private final Map<UUID, Long> tntCooldowns = new HashMap<UUID, Long>();
    private final Map<UUID, Long> jetpackCooldowns = new HashMap<UUID, Long>();

    private final Map<UUID, BukkitTask> tntTasks = new HashMap<UUID, BukkitTask>();
    private final Map<UUID, BukkitTask> jetpackTasks = new HashMap<UUID, BukkitTask>();

    private final Map<UUID, Integer> tntSlots = new HashMap<UUID, Integer>();
    private final Map<UUID, Integer> jetpackSlots = new HashMap<UUID, Integer>();

    public ItemRechargeManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void startTntRecharge(final Player player, final int seconds) {
        clearTnt(player);

        final UUID uuid = player.getUniqueId();
        final int slot = player.getInventory().getHeldItemSlot();
        final long end = System.currentTimeMillis() + (seconds * 1000L);

        tntCooldowns.put(uuid, end);
        tntSlots.put(uuid, slot);

        player.getInventory().setItem(slot, CooldownItemFactory.createBarrier("cooldowns.tnt", seconds));
        player.updateInventory();

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                tntCooldowns.remove(uuid);
                tntTasks.remove(uuid);

                Integer savedSlot = tntSlots.remove(uuid);

                if (player.isOnline() && savedSlot != null) {
                    player.getInventory().setItem(savedSlot, ItemFactory.createTntItem());
                    player.updateInventory();
                }
            }
        }, seconds * 20L);

        tntTasks.put(uuid, task);
    }

    public void startJetpackRecharge(final Player player, final int seconds) {
        clearJetpack(player);

        final UUID uuid = player.getUniqueId();
        final int slot = player.getInventory().getHeldItemSlot();
        final long end = System.currentTimeMillis() + (seconds * 1000L);

        jetpackCooldowns.put(uuid, end);
        jetpackSlots.put(uuid, slot);

        player.getInventory().setItem(slot, CooldownItemFactory.createBarrier("cooldowns.jetpack", seconds));
        player.updateInventory();

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                jetpackCooldowns.remove(uuid);
                jetpackTasks.remove(uuid);

                Integer savedSlot = jetpackSlots.remove(uuid);

                if (player.isOnline() && savedSlot != null) {
                    player.getInventory().setItem(savedSlot, ItemFactory.createJetpackItem());
                    player.updateInventory();
                }
            }
        }, seconds * 20L);

        jetpackTasks.put(uuid, task);
    }

    public boolean isTntOnCooldown(Player player) {
        Long end = tntCooldowns.get(player.getUniqueId());
        if (end == null) {
            return false;
        }

        if (end <= System.currentTimeMillis()) {
            tntCooldowns.remove(player.getUniqueId());
            tntSlots.remove(player.getUniqueId());
            return false;
        }

        return true;
    }

    public boolean isJetpackOnCooldown(Player player) {
        Long end = jetpackCooldowns.get(player.getUniqueId());
        if (end == null) {
            return false;
        }

        if (end <= System.currentTimeMillis()) {
            jetpackCooldowns.remove(player.getUniqueId());
            jetpackSlots.remove(player.getUniqueId());
            return false;
        }

        return true;
    }

    public int getRemainingTntSeconds(Player player) {
        Long end = tntCooldowns.get(player.getUniqueId());
        if (end == null) {
            return 0;
        }

        long remaining = end - System.currentTimeMillis();
        if (remaining <= 0L) {
            return 0;
        }

        return (int) Math.ceil(remaining / 1000.0D);
    }

    public int getRemainingJetpackSeconds(Player player) {
        Long end = jetpackCooldowns.get(player.getUniqueId());
        if (end == null) {
            return 0;
        }

        long remaining = end - System.currentTimeMillis();
        if (remaining <= 0L) {
            return 0;
        }

        return (int) Math.ceil(remaining / 1000.0D);
    }

    public float getTntProgress(Player player, int maxSeconds) {
        return getProgress(tntCooldowns.get(player.getUniqueId()), maxSeconds);
    }

    public float getJetpackProgress(Player player, int maxSeconds) {
        return getProgress(jetpackCooldowns.get(player.getUniqueId()), maxSeconds);
    }

    private float getProgress(Long end, int maxSeconds) {
        if (end == null || maxSeconds <= 0) {
            return 0.0F;
        }

        long remaining = end - System.currentTimeMillis();
        if (remaining <= 0L) {
            return 0.0F;
        }

        float progress = (float) remaining / (float) (maxSeconds * 1000L);
        return Math.max(0.0F, Math.min(1.0F, progress));
    }

    public void resetTnt(Player player) {
        UUID uuid = player.getUniqueId();
        Integer slot = tntSlots.get(uuid);

        clearTnt(player);

        if (slot == null) {
            slot = findItemSlot(player, "tnt");
        }

        if (slot != null) {
            player.getInventory().setItem(slot, ItemFactory.createTntItem());
            player.updateInventory();
        }
    }

    public void resetJetpack(Player player) {
        UUID uuid = player.getUniqueId();
        Integer slot = jetpackSlots.get(uuid);

        clearJetpack(player);

        if (slot == null) {
            slot = findItemSlot(player, "jetpack");
        }

        if (slot != null) {
            player.getInventory().setItem(slot, ItemFactory.createJetpackItem());
            player.updateInventory();
        }
    }

    public void clear(Player player) {
        clearTnt(player);
        clearJetpack(player);
    }

    private void clearTnt(Player player) {
        UUID uuid = player.getUniqueId();

        tntCooldowns.remove(uuid);
        tntSlots.remove(uuid);

        BukkitTask task = tntTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    private void clearJetpack(Player player) {
        UUID uuid = player.getUniqueId();

        jetpackCooldowns.remove(uuid);
        jetpackSlots.remove(uuid);

        BukkitTask task = jetpackTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    private Integer findItemSlot(Player player, String configKey) {
        for (int i = 0; i < 9; i++) {
            if (ItemFactory.isSimilarKeyItem(player.getInventory().getItem(i), configKey)) {
                return i;
            }
        }
        return null;
    }
}
