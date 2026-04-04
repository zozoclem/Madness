package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemRechargeManager {

    private final MadnessPlugin plugin;
    private final Map<UUID, RechargeData> tntRecharge;
    private final Map<UUID, RechargeData> jetpackRecharge;
    private final Map<UUID, BukkitTask> tntTasks;
    private final Map<UUID, BukkitTask> jetpackTasks;
    private final Map<UUID, BukkitTask> visualTasks;

    public ItemRechargeManager(MadnessPlugin plugin) {
        this.plugin = plugin;
        this.tntRecharge = new HashMap<UUID, RechargeData>();
        this.jetpackRecharge = new HashMap<UUID, RechargeData>();
        this.tntTasks = new HashMap<UUID, BukkitTask>();
        this.jetpackTasks = new HashMap<UUID, BukkitTask>();
        this.visualTasks = new HashMap<UUID, BukkitTask>();
    }

    public void startTntRecharge(final Player player) {
        cancelTntTask(player);
        cancelVisualTask(player);

        int seconds = plugin.getConfig().getInt("tnt.recharge");
        int slot = 1;

        RechargeData data = new RechargeData(System.currentTimeMillis(), seconds, slot, RechargeType.TNT);
        tntRecharge.put(player.getUniqueId(), data);

        player.getInventory().setItem(slot, CooldownItemFactory.createTntCooldownItem(seconds));
        player.updateInventory();

        startVisualUpdater(player, RechargeType.TNT);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isOnline()) {
                    player.getInventory().setItem(1, ItemFactory.createTntItem());
                    player.updateInventory();
                }
                tntRecharge.remove(player.getUniqueId());
                cancelVisualTask(player);
                tntTasks.remove(player.getUniqueId());
            }
        }, seconds * 20L);

        tntTasks.put(player.getUniqueId(), task);
    }

    public void startJetpackRecharge(final Player player) {
        cancelJetpackTask(player);
        cancelVisualTask(player);

        int seconds = plugin.getConfig().getInt("jetpack.recharge");
        int slot = 2;

        RechargeData data = new RechargeData(System.currentTimeMillis(), seconds, slot, RechargeType.JETPACK);
        jetpackRecharge.put(player.getUniqueId(), data);

        player.getInventory().setItem(slot, CooldownItemFactory.createJetpackCooldownItem(seconds));
        player.updateInventory();

        startVisualUpdater(player, RechargeType.JETPACK);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isOnline()) {
                    player.getInventory().setItem(2, ItemFactory.createJetpackItem());
                    player.updateInventory();
                }
                jetpackRecharge.remove(player.getUniqueId());
                cancelVisualTask(player);
                jetpackTasks.remove(player.getUniqueId());
            }
        }, seconds * 20L);

        jetpackTasks.put(player.getUniqueId(), task);
    }

    public void resetTntCooldown(Player player) {
        cancelTntTask(player);
        cancelVisualTask(player);
        tntRecharge.remove(player.getUniqueId());

        if (player != null && player.isOnline()) {
            player.getInventory().setItem(1, ItemFactory.createTntItem());
            player.updateInventory();
        }
    }

    private void startVisualUpdater(final Player player, final RechargeType type) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (player == null || !player.isOnline()) {
                    return;
                }

                RechargeData data = getRecharge(player, type);
                if (data == null) {
                    return;
                }

                int remaining = data.getRemainingSeconds();

                if (type == RechargeType.TNT) {
                    player.getInventory().setItem(data.getSlot(), CooldownItemFactory.createTntCooldownItem(remaining));
                } else {
                    player.getInventory().setItem(data.getSlot(), CooldownItemFactory.createJetpackCooldownItem(remaining));
                }

                player.updateInventory();

                if (remaining <= 0) {
                    cancelVisualTask(player);
                }
            }
        }, 0L, 20L);

        visualTasks.put(player.getUniqueId(), task);
    }

    private void cancelTntTask(Player player) {
        BukkitTask task = tntTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    private void cancelJetpackTask(Player player) {
        BukkitTask task = jetpackTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    private void cancelVisualTask(Player player) {
        BukkitTask task = visualTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public RechargeData getHeldRecharge(Player player) {
        RechargeData tnt = tntRecharge.get(player.getUniqueId());
        RechargeData jetpack = jetpackRecharge.get(player.getUniqueId());

        int heldSlot = player.getInventory().getHeldItemSlot();

        if (tnt != null && tnt.getSlot() == heldSlot) {
            return tnt;
        }

        if (jetpack != null && jetpack.getSlot() == heldSlot) {
            return jetpack;
        }

        return null;
    }

    public RechargeData getRecharge(Player player, RechargeType type) {
        if (type == RechargeType.TNT) {
            return tntRecharge.get(player.getUniqueId());
        }
        return jetpackRecharge.get(player.getUniqueId());
    }

    public boolean isTntOnCooldown(Player player) {
        return tntRecharge.containsKey(player.getUniqueId());
    }

    public boolean isJetpackOnCooldown(Player player) {
        return jetpackRecharge.containsKey(player.getUniqueId());
    }

    public void clear(Player player) {
        cancelTntTask(player);
        cancelJetpackTask(player);
        cancelVisualTask(player);
        tntRecharge.remove(player.getUniqueId());
        jetpackRecharge.remove(player.getUniqueId());
    }

    public static class RechargeData {
        private final long startMillis;
        private final int durationSeconds;
        private final int slot;
        private final RechargeType type;

        public RechargeData(long startMillis, int durationSeconds, int slot, RechargeType type) {
            this.startMillis = startMillis;
            this.durationSeconds = durationSeconds;
            this.slot = slot;
            this.type = type;
        }

        public int getSlot() {
            return slot;
        }

        public RechargeType getType() {
            return type;
        }

        public int getRemainingSeconds() {
            long elapsed = (System.currentTimeMillis() - startMillis) / 1000L;
            int remaining = durationSeconds - (int) elapsed;
            return Math.max(remaining, 0);
        }

        public float getProgress() {
            long elapsedMillis = System.currentTimeMillis() - startMillis;
            double progress = (double) elapsedMillis / (durationSeconds * 1000.0D);
            progress = Math.max(0.0D, Math.min(progress, 1.0D));
            return (float) progress;
        }
    }

    public enum RechargeType {
        TNT,
        JETPACK
    }
}