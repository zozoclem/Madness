package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceBreakListener implements Listener {

    private final MadnessPlugin plugin;

    public BlockPlaceBreakListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        final Player player = event.getPlayer();
        final ItemStack item = event.getItemInHand();

        Material blockedMaterial = ConfigUtil.getMaterial(plugin.getConfig().getString("blocks.blocked-place-material"), Material.BARRIER);
        Material trackedMaterial = ConfigUtil.getMaterial(plugin.getConfig().getString("blocks.tracked-material"), Material.SANDSTONE);
        int refillAmount = plugin.getConfig().getInt("blocks.refill-amount", 64);

        if (block.getType() == blockedMaterial) {
            event.setCancelled(true);
            return;
        }

        if (block.getType() == trackedMaterial) {
            plugin.getBlockDecayManager().track(block);

            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    ItemStack current = player.getInventory().getItem(player.getInventory().getHeldItemSlot());

                    if (current != null && current.getType() == trackedMaterial) {
                        current.setAmount(refillAmount);
                        player.setItemInHand(current);
                    } else if (item != null && item.getType() == trackedMaterial) {
                        ItemStack cloned = item.clone();
                        cloned.setAmount(refillAmount);
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), cloned);
                    }

                    player.updateInventory();
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!plugin.getBlockDecayManager().isTracked(block)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        plugin.getBlockDecayManager().remove(block);
        block.setType(Material.AIR);
    }
}
