package fr.europixel.madness.listener.global;

import fr.europixel.madness.MadnessPlugin;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldProtectionListener implements Listener {

    private final MadnessPlugin plugin;

    public WorldProtectionListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        applyWorldRules(event.getWorld());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (shouldRemove(entity)) {
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() == null) {
            return;
        }

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }

        if (shouldRemove(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    public void applyAllWorldRules() {
        for (World world : plugin.getServer().getWorlds()) {
            applyWorldRules(world);
            clearLivingMobs(world);
        }
    }

    private void applyWorldRules(World world) {
        if (world == null) {
            return;
        }

        world.setDifficulty(Difficulty.NORMAL);

        try {
            world.setStorm(false);
            world.setThundering(false);
        } catch (Exception ignored) {
        }
    }

    private void clearLivingMobs(World world) {
        if (world == null) {
            return;
        }

        for (Entity entity : world.getEntities()) {
            if (shouldRemove(entity)) {
                entity.remove();
            }
        }
    }

    private boolean shouldRemove(Entity entity) {
        if (entity == null) {
            return false;
        }

        if (!(entity instanceof LivingEntity)) {
            return false;
        }

        if (entity instanceof Player) {
            return false;
        }

        String name = entity.getType().name();

        if (name.equals("ARMOR_STAND")) {
            return false;
        }

        return true;
    }
}