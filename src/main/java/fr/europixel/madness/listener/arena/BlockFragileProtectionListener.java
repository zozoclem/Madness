package fr.europixel.madness.listener.arena;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockFragileProtectionListener implements Listener {

    private final MadnessPlugin plugin;

    public BlockFragileProtectionListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getPlayerModeManager().isInArena(player)) {
            return;
        }

        Block placed = event.getBlockPlaced();

        if (wouldBreakFragileBlock(placed)) {
            event.setCancelled(true);
        }
    }

    private boolean wouldBreakFragileBlock(Block placed) {
        Block[] toCheck = new Block[] {
                placed,
                placed.getRelative(BlockFace.UP),
                placed.getRelative(BlockFace.DOWN),
                placed.getRelative(BlockFace.NORTH),
                placed.getRelative(BlockFace.SOUTH),
                placed.getRelative(BlockFace.EAST),
                placed.getRelative(BlockFace.WEST),

                placed.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH),
                placed.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH),
                placed.getRelative(BlockFace.UP).getRelative(BlockFace.EAST),
                placed.getRelative(BlockFace.UP).getRelative(BlockFace.WEST),

                placed.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH),
                placed.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH),
                placed.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST),
                placed.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST)
        };

        for (Block block : toCheck) {
            if (isFragile(block.getType())) {
                return true;
            }
        }

        return false;
    }

    private boolean isFragile(Material material) {
        if (material == null) {
            return false;
        }

        if (material == Material.AIR) {
            return false;
        }

        String name = material.name();

        // Herbes / fleurs / plantes
        if (name.equals("LONG_GRASS")) return true;
        if (name.equals("GRASS")) return true;
        if (name.equals("TALL_GRASS")) return true;
        if (name.equals("DEAD_BUSH")) return true;
        if (name.equals("YELLOW_FLOWER")) return true;
        if (name.equals("RED_ROSE")) return true;
        if (name.endsWith("_FLOWER")) return true;
        if (name.equals("DOUBLE_PLANT")) return true;
        if (name.endsWith("_SAPLING")) return true;
        if (name.equals("SAPLING")) return true;
        if (name.equals("BROWN_MUSHROOM")) return true;
        if (name.equals("RED_MUSHROOM")) return true;
        if (name.equals("CACTUS")) return true;
        if (name.equals("SUGAR_CANE")) return true;
        if (name.equals("SUGAR_CANE_BLOCK")) return true;
        if (name.equals("CROPS")) return true;
        if (name.equals("CARROT")) return true;
        if (name.equals("CARROTS")) return true;
        if (name.equals("POTATO")) return true;
        if (name.equals("POTATOES")) return true;
        if (name.equals("NETHER_WARTS")) return true;
        if (name.equals("NETHER_WART")) return true;
        if (name.equals("MELON_STEM")) return true;
        if (name.equals("PUMPKIN_STEM")) return true;
        if (name.equals("CHORUS_PLANT")) return true;
        if (name.equals("CHORUS_FLOWER")) return true;
        if (name.equals("SWEET_BERRY_BUSH")) return true;

        // Torches / redstone
        if (name.equals("TORCH")) return true;
        if (name.equals("REDSTONE_TORCH_ON")) return true;
        if (name.equals("REDSTONE_TORCH_OFF")) return true;
        if (name.equals("REDSTONE_TORCH")) return true;
        if (name.equals("REDSTONE_WIRE")) return true;
        if (name.equals("REPEATER")) return true;
        if (name.equals("DIODE_BLOCK_ON")) return true;
        if (name.equals("DIODE_BLOCK_OFF")) return true;
        if (name.equals("COMPARATOR")) return true;
        if (name.equals("REDSTONE_COMPARATOR_ON")) return true;
        if (name.equals("REDSTONE_COMPARATOR_OFF")) return true;
        if (name.equals("DAYLIGHT_DETECTOR")) return true;

        // Trapdoors / portes / boutons / leviers / plaques
        if (name.equals("TRAP_DOOR")) return true;
        if (name.endsWith("_TRAPDOOR")) return true;
        if (name.equals("WOODEN_DOOR")) return true;
        if (name.equals("IRON_DOOR_BLOCK")) return true;
        if (name.endsWith("_DOOR")) return true;
        if (name.equals("LEVER")) return true;
        if (name.endsWith("_BUTTON")) return true;
        if (name.equals("STONE_BUTTON")) return true;
        if (name.equals("WOOD_BUTTON")) return true;
        if (name.endsWith("_PRESSURE_PLATE")) return true;
        if (name.equals("STONE_PLATE")) return true;
        if (name.equals("WOOD_PLATE")) return true;
        if (name.equals("GOLD_PLATE")) return true;
        if (name.equals("IRON_PLATE")) return true;

        // Rails
        if (name.equals("RAILS")) return true;
        if (name.equals("RAIL")) return true;
        if (name.equals("POWERED_RAIL")) return true;
        if (name.equals("DETECTOR_RAIL")) return true;
        if (name.equals("ACTIVATOR_RAIL")) return true;

        // Panneaux / têtes / cadres / décos fragiles
        if (name.equals("SIGN_POST")) return true;
        if (name.equals("WALL_SIGN")) return true;
        if (name.endsWith("_SIGN")) return true;
        if (name.equals("SKULL")) return true;
        if (name.equals("PLAYER_HEAD")) return true;
        if (name.equals("PLAYER_WALL_HEAD")) return true;
        if (name.endsWith("_HEAD")) return true;
        if (name.endsWith("_WALL_HEAD")) return true;
        if (name.equals("VINE")) return true;
        if (name.equals("LADDER")) return true;
        if (name.equals("CARPET")) return true;
        if (name.endsWith("_CARPET")) return true;
        if (name.equals("SNOW")) return true;
        if (name.equals("SNOW_LAYER")) return true;
        if (name.equals("WATER_LILY")) return true;
        if (name.equals("LILY_PAD")) return true;
        if (name.equals("TRIPWIRE")) return true;
        if (name.equals("TRIPWIRE_HOOK")) return true;
        if (name.equals("STRING")) return true;

        // Blocs qui sautent souvent avec update support
        if (name.equals("FLOWER_POT")) return true;
        if (name.equals("ANVIL")) return true;
        if (name.equals("BED_BLOCK")) return true;
        if (name.endsWith("_BED")) return true;

        return false;
    }
}