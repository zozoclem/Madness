package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

        if (block.getType() == Material.BARRIER) {
            event.setCancelled(true);
            return;
        }

        if (block.getType() == Material.SANDSTONE) {
            plugin.getBlockDecayManager().track(block);

            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    ItemStack current = player.getInventory().getItem(player.getInventory().getHeldItemSlot());

                    if (current != null && current.getType() == Material.SANDSTONE) {
                        current.setAmount(64);
                        player.setItemInHand(current);
                    } else if (item != null && item.getType() == Material.SANDSTONE) {
                        ItemStack sandstone = item.clone();
                        sandstone.setAmount(64);
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), sandstone);
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