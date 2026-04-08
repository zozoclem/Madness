package fr.europixel.madness.listener.arena;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceBreakListener implements Listener {

    private final MadnessPlugin plugin;

    public BlockPlaceBreakListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (!plugin.getPlayerModeManager().isInArena(player)) {
            event.setCancelled(true);
            return;
        }

        final ItemStack itemInHand = event.getItemInHand();
        if (itemInHand == null) {
            return;
        }

        if (isForbiddenBlock(itemInHand.getType())) {
            event.setCancelled(true);
            return;
        }

        if (!isTrackedBlock(itemInHand)) {
            return;
        }

        final Block placedBlock = event.getBlockPlaced();

        if (isForbiddenBlock(placedBlock.getType())) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getBlockDecayManager() != null) {
            plugin.getBlockDecayManager().track(placedBlock);
        }

        final int heldSlot = player.getInventory().getHeldItemSlot();
        final Material type = itemInHand.getType();
        final short data = itemInHand.getDurability();
        final int amountBeforePlace = itemInHand.getAmount();

        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                ItemStack current = player.getInventory().getItem(heldSlot);

                if (current == null || current.getType() == Material.AIR) {
                    player.getInventory().setItem(heldSlot, new ItemStack(type, amountBeforePlace, data));
                    player.updateInventory();
                    return;
                }

                if (current.getType() == type && current.getDurability() == data) {
                    current.setAmount(amountBeforePlace);
                    player.updateInventory();
                    return;
                }

                player.getInventory().setItem(heldSlot, new ItemStack(type, amountBeforePlace, data));
                player.updateInventory();
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (!plugin.getPlayerModeManager().isInArena(player)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();

        if (!isTrackedBlock(block)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        block.setType(Material.AIR);
    }

    private boolean isTrackedBlock(ItemStack item) {
        if (item == null) {
            return false;
        }

        if (isForbiddenBlock(item.getType())) {
            return false;
        }

        if (plugin.getBlockShopManager() != null) {
            return plugin.getBlockShopManager().isPlaceableBlock(item);
        }

        Material tracked = Material.SANDSTONE;
        return item.getType() == tracked;
    }

    private boolean isTrackedBlock(Block block) {
        if (block == null) {
            return false;
        }

        if (isForbiddenBlock(block.getType())) {
            return false;
        }

        if (plugin.getBlockShopManager() != null) {
            return plugin.getBlockShopManager().isPlaceableBlock(block);
        }

        Material tracked = Material.SANDSTONE;
        return block.getType() == tracked;
    }

    private boolean isForbiddenBlock(Material material) {
        return material == Material.BARRIER || material == Material.AIR;
    }
}