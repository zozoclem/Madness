package fr.europixel.madness.listener.global;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionBlockerListener implements Listener {

    private final MadnessPlugin plugin;

    public InteractionBlockerListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!plugin.getPlayerModeManager().isInArena(player)
                && !plugin.getPlayerModeManager().isInLobby(player)) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        if (!shouldBlock(block)) {
            return;
        }

        event.setCancelled(true);
        event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
        event.setUseItemInHand(org.bukkit.event.Event.Result.ALLOW);
    }

    private boolean shouldBlock(Block block) {
        Material type = block.getType();
        String name = type.name();

        // Doors
        if (name.endsWith("_DOOR")) return true;

        // Trapdoors
        if (name.endsWith("_TRAPDOOR")) return true;

        // Fence gates
        if (name.endsWith("_FENCE_GATE")) return true;

        // Buttons
        if (name.endsWith("_BUTTON")) return true;

        // Pressure plates
        if (name.endsWith("_PRESSURE_PLATE")) return true;

        // Lever
        if (name.equals("LEVER")) return true;

        // Coffres & stockage
        if (name.contains("CHEST")) return true;
        if (name.equals("BARREL")) return true;
        if (name.equals("HOPPER")) return true;
        if (name.equals("DISPENSER")) return true;
        if (name.equals("DROPPER")) return true;

        // Tables interactives
        if (name.contains("CRAFTING")) return true;
        if (name.contains("FURNACE")) return true;
        if (name.equals("ENCHANTING_TABLE")) return true;
        if (name.equals("BREWING_STAND")) return true;
        if (name.equals("ANVIL")) return true;

        return false;
    }
}