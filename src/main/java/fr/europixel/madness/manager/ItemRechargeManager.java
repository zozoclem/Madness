package fr.europixel.madness.manager;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.item.CooldownItemFactory;
import fr.europixel.madness.item.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemRechargeManager {

    private final MadnessPlugin plugin;

    private final Map<UUID, Long> tntCooldowns = new HashMap<UUID, Long>();
    private final Map<UUID, Long> jetpackCooldowns = new HashMap<UUID, Long>();

    private final Map<UUID, Integer> tntSlots = new HashMap<UUID, Integer>();
    private final Map<UUID, Integer> jetpackSlots = new HashMap<UUID, Integer>();

    public ItemRechargeManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isTntOnCooldown(Player player) {
        if (player == null) {
            return false;
        }

        Long end = tntCooldowns.get(player.getUniqueId());
        if (end == null) {
            return false;
        }

        if (end <= System.currentTimeMillis()) {
            tntCooldowns.remove(player.getUniqueId());
            restoreTnt(player);
            return false;
        }

        return true;
    }

    public boolean isJetpackOnCooldown(Player player) {
        if (player == null) {
            return false;
        }

        Long end = jetpackCooldowns.get(player.getUniqueId());
        if (end == null) {
            return false;
        }

        if (end <= System.currentTimeMillis()) {
            jetpackCooldowns.remove(player.getUniqueId());
            restoreJetpack(player);
            return false;
        }

        return true;
    }

    public void startTntRecharge(final Player player, final double seconds) {
        if (player == null) {
            return;
        }

        int slot = findTntSlot(player);
        if (slot < 0) {
            slot = player.getInventory().getHeldItemSlot();
        }

        tntSlots.put(player.getUniqueId(), slot);

        final long end = System.currentTimeMillis() + (long) Math.ceil(seconds * 1000.0D);
        tntCooldowns.put(player.getUniqueId(), end);

        player.getInventory().setItem(slot, CooldownItemFactory.createBarrier("cooldowns.tnt", (int) Math.ceil(seconds)));
        player.updateInventory();

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                tntCooldowns.remove(player.getUniqueId());
                restoreTnt(player);
            }
        }, (long) Math.ceil(seconds * 20.0D));
    }

    public void startJetpackRecharge(final Player player, final double seconds) {
        if (player == null) {
            return;
        }

        int slot = findJetpackSlot(player);
        if (slot < 0) {
            slot = player.getInventory().getHeldItemSlot();
        }

        jetpackSlots.put(player.getUniqueId(), slot);

        final long end = System.currentTimeMillis() + (long) Math.ceil(seconds * 1000.0D);
        jetpackCooldowns.put(player.getUniqueId(), end);

        player.getInventory().setItem(slot, CooldownItemFactory.createBarrier("cooldowns.jetpack", (int) Math.ceil(seconds)));
        player.updateInventory();

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                jetpackCooldowns.remove(player.getUniqueId());
                restoreJetpack(player);
            }
        }, (long) Math.ceil(seconds * 20.0D));
    }

    public double getRemainingTntSeconds(Player player) {
        if (player == null) {
            return 0.0D;
        }

        Long end = tntCooldowns.get(player.getUniqueId());
        if (end == null) {
            return 0.0D;
        }

        long remaining = end - System.currentTimeMillis();
        if (remaining <= 0L) {
            return 0.0D;
        }

        return remaining / 1000.0D;
    }

    public double getRemainingJetpackSeconds(Player player) {
        if (player == null) {
            return 0.0D;
        }

        Long end = jetpackCooldowns.get(player.getUniqueId());
        if (end == null) {
            return 0.0D;
        }

        long remaining = end - System.currentTimeMillis();
        if (remaining <= 0L) {
            return 0.0D;
        }

        return remaining / 1000.0D;
    }

    public float getTntProgress(Player player, double maxSeconds) {
        if (player == null) {
            return 0.0F;
        }

        return getProgress(tntCooldowns.get(player.getUniqueId()), maxSeconds);
    }

    public float getJetpackProgress(Player player, double maxSeconds) {
        if (player == null) {
            return 0.0F;
        }

        return getProgress(jetpackCooldowns.get(player.getUniqueId()), maxSeconds);
    }

    private float getProgress(Long end, double maxSeconds) {
        if (end == null || maxSeconds <= 0.0D) {
            return 0.0F;
        }

        long remaining = end - System.currentTimeMillis();
        if (remaining <= 0L) {
            return 0.0F;
        }

        float progress = (float) (remaining / (maxSeconds * 1000.0D));
        return Math.max(0.0F, Math.min(1.0F, progress));
    }

    public Integer getTntSlot(Player player) {
        if (player == null) {
            return null;
        }
        return tntSlots.get(player.getUniqueId());
    }

    public Integer getJetpackSlot(Player player) {
        if (player == null) {
            return null;
        }
        return jetpackSlots.get(player.getUniqueId());
    }

    public void resetTnt(Player player) {
        if (player == null) {
            return;
        }

        tntCooldowns.remove(player.getUniqueId());
        restoreTnt(player);
    }

    public void resetJetpack(Player player) {
        if (player == null) {
            return;
        }

        jetpackCooldowns.remove(player.getUniqueId());
        restoreJetpack(player);
    }

    public void clear(Player player) {
        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();

        tntCooldowns.remove(uuid);
        jetpackCooldowns.remove(uuid);

        tntSlots.remove(uuid);
        jetpackSlots.remove(uuid);
    }

    private void restoreTnt(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Integer slot = tntSlots.get(player.getUniqueId());
        if (slot == null || slot < 0 || slot > 8) {
            return;
        }

        player.getInventory().setItem(slot, buildItemFromConfig("items.tnt"));
        player.updateInventory();
    }

    private void restoreJetpack(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Integer slot = jetpackSlots.get(player.getUniqueId());
        if (slot == null || slot < 0 || slot > 8) {
            return;
        }

        player.getInventory().setItem(slot, buildItemFromConfig("items.jetpack"));
        player.updateInventory();
    }

    private int findTntSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (ItemFactory.isSimilarKeyItem(item, "tnt")) {
                return i;
            }
        }

        Integer saved = tntSlots.get(player.getUniqueId());
        return saved == null ? -1 : saved;
    }

    private int findJetpackSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (ItemFactory.isSimilarKeyItem(item, "jetpack")) {
                return i;
            }
        }

        Integer saved = jetpackSlots.get(player.getUniqueId());
        return saved == null ? -1 : saved;
    }

    private ItemStack buildItemFromConfig(String path) {
        if (plugin == null) {
            return null;
        }

        String materialName = plugin.getConfig().getString(path + ".material", "STONE");
        int amount = plugin.getConfig().getInt(path + ".amount", 1);

        org.bukkit.Material material;
        try {
            material = org.bukkit.Material.valueOf(materialName.toUpperCase());
        } catch (Exception e) {
            material = org.bukkit.Material.STONE;
        }

        ItemStack item = new ItemStack(material, Math.max(1, amount));

        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta != null) {

            String name = plugin.getConfig().getString(path + ".name");
            if (name != null) {
                meta.setDisplayName(fr.europixel.madness.utils.ConfigUtil.color(name));
            }

            java.util.List<String> lore = plugin.getConfig().getStringList(path + ".lore");
            if (lore != null && !lore.isEmpty()) {
                java.util.List<String> colored = new java.util.ArrayList<String>();
                for (String line : lore) {
                    colored.add(fr.europixel.madness.utils.ConfigUtil.color(line));
                }
                meta.setLore(colored);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}