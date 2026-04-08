package fr.europixel.madness.manager;

import fr.europixel.madness.MadnessPlugin;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BlockDecayManager {

    private final MadnessPlugin plugin;
    private final Map<Location, PlacedBlockData> placedBlocks;
    private int animationIdCounter = 0;
    private BukkitTask task;

    public BlockDecayManager(MadnessPlugin plugin) {
        this.plugin = plugin;
        this.placedBlocks = new HashMap<Location, PlacedBlockData>();
    }

    public void track(Block block) {
        if (block == null) {
            return;
        }

        animationIdCounter++;
        placedBlocks.put(block.getLocation(), new PlacedBlockData(System.currentTimeMillis(), animationIdCounter));
    }

    public boolean isTracked(Block block) {
        return block != null && placedBlocks.containsKey(block.getLocation());
    }

    public void remove(Block block) {
        if (block == null) {
            return;
        }

        PlacedBlockData data = placedBlocks.remove(block.getLocation());
        if (data != null) {
            sendBreakAnimation(block, data.getAnimationId(), -1);
        }
    }

    public void start() {
        stop();

        this.task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                tick();
            }
        }, 2L, 2L);
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        for (Map.Entry<Location, PlacedBlockData> entry : placedBlocks.entrySet()) {
            Location location = entry.getKey();
            PlacedBlockData data = entry.getValue();

            if (location.getWorld() != null) {
                sendBreakAnimation(location.getBlock(), data.getAnimationId(), -1);
            }
        }

        placedBlocks.clear();
    }

    private void tick() {
        long now = System.currentTimeMillis();

        int crackStartAfter = plugin.getConfig().getInt("blocks.crack-start-after", 6);
        int removeAfter = plugin.getConfig().getInt("blocks.remove-after", 12);

        Iterator<Map.Entry<Location, PlacedBlockData>> iterator = placedBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Location, PlacedBlockData> entry = iterator.next();

            Location location = entry.getKey();
            PlacedBlockData data = entry.getValue();

            if (location.getWorld() == null) {
                iterator.remove();
                continue;
            }

            Block block = location.getBlock();

            if (block.getType() == Material.AIR) {
                sendBreakAnimation(block, data.getAnimationId(), -1);
                iterator.remove();
                continue;
            }

            long elapsedMillis = now - data.getPlacedAt();
            double elapsedSeconds = elapsedMillis / 1000.0D;

            if (elapsedSeconds >= removeAfter) {
                sendBreakAnimation(block, data.getAnimationId(), -1);
                block.setType(Material.AIR);
                iterator.remove();
                continue;
            }

            if (elapsedSeconds < crackStartAfter) {
                sendBreakAnimation(block, data.getAnimationId(), -1);
                continue;
            }

            double crackDuration = removeAfter - crackStartAfter;
            if (crackDuration <= 0.0D) {
                crackDuration = 1.0D;
            }

            double crackProgress = (elapsedSeconds - crackStartAfter) / crackDuration;
            crackProgress = Math.max(0.0D, Math.min(crackProgress, 1.0D));

            int stage = (int) Math.floor(crackProgress * 10.0D);
            if (stage > 9) {
                stage = 9;
            }
            if (stage < 0) {
                stage = 0;
            }

            sendBreakAnimation(block, data.getAnimationId(), stage);
        }
    }

    private void sendBreakAnimation(Block block, int animationId, int stage) {
        BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(animationId, position, stage);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() != block.getWorld()) {
                continue;
            }

            if (player.getLocation().distanceSquared(block.getLocation()) > 128 * 128) {
                continue;
            }

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private static class PlacedBlockData {
        private final long placedAt;
        private final int animationId;

        public PlacedBlockData(long placedAt, int animationId) {
            this.placedAt = placedAt;
            this.animationId = animationId;
        }

        public long getPlacedAt() {
            return placedAt;
        }

        public int getAnimationId() {
            return animationId;
        }
    }
}