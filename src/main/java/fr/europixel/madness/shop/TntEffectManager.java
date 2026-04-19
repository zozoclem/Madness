package fr.europixel.madness.shop;

import fr.europixel.madness.MadnessPlugin;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TntEffectManager {

    private final MadnessPlugin plugin;

    public TntEffectManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void playSelectedEffect(Player player, Location location) {
        if (player == null || location == null) {
            return;
        }

        String effectId = "classic";

        if (plugin.getDatabaseManager() != null) {
            String selected = plugin.getDatabaseManager().getSelectedTntEffect(player.getUniqueId().toString());
            if (selected != null && !selected.trim().isEmpty()) {
                effectId = selected.toLowerCase();
            }
        }

        if (effectId.equals("fire")) {
            playFire(location);
            return;
        }

        if (effectId.equals("magic")) {
            playMagic(location);
            return;
        }

        if (effectId.equals("thunder")) {
            playThunder(location);
            return;
        }

        if (effectId.equals("nuclear")) {
            playNuclear(location);
            return;
        }

        if (effectId.equals("dragon")) {
            playDragonBurst(location);
            return;
        }

        if (effectId.equals("frost")) {
            playFrostNova(location);
            return;
        }

        if (effectId.equals("shadow")) {
            playShadowVoid(location);
            return;
        }

        playClassic(location);
    }

    private void playClassic(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(location, Sound.EXPLODE, 2.2F, 1.0F);
        world.playEffect(location, Effect.EXPLOSION_HUGE, 0);
        world.playEffect(location, Effect.EXPLOSION_LARGE, 0);

        spawnParticle(location, EnumParticle.EXPLOSION_LARGE, 0.0F, 0.0F, 0.0F, 0.0F, 4);
        spawnParticle(location, EnumParticle.SMOKE_LARGE, 0.55F, 0.35F, 0.55F, 0.02F, 30);
        spawnParticle(location.clone().add(0.0D, 0.2D, 0.0D), EnumParticle.CLOUD, 0.45F, 0.15F, 0.45F, 0.02F, 20);
    }

    private void playFire(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(location, Sound.EXPLODE, 2.4F, 1.1F);
        world.playSound(location, Sound.FIRE_IGNITE, 1.5F, 1.7F);
        world.playEffect(location, Effect.EXPLOSION_HUGE, 0);
        world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);

        spawnParticle(location, EnumParticle.EXPLOSION_LARGE, 0.0F, 0.0F, 0.0F, 0.0F, 5);
        spawnParticle(location, EnumParticle.FLAME, 0.65F, 0.45F, 0.65F, 0.03F, 50);
        spawnParticle(location, EnumParticle.LAVA, 0.45F, 0.25F, 0.45F, 0.01F, 18);
        spawnParticle(location, EnumParticle.SMOKE_LARGE, 0.7F, 0.4F, 0.7F, 0.03F, 35);
        spawnParticle(location.clone().add(0.0D, 0.35D, 0.0D), EnumParticle.CRIT, 0.45F, 0.2F, 0.45F, 0.02F, 18);
    }

    private void playMagic(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(location, Sound.EXPLODE, 1.8F, 1.45F);
        world.playSound(location, Sound.LEVEL_UP, 1.4F, 1.9F);
        world.playSound(location, Sound.ENDERMAN_TELEPORT, 1.1F, 1.55F);
        world.playEffect(location, Effect.ENDER_SIGNAL, 0);
        world.playEffect(location, Effect.EXPLOSION_LARGE, 0);

        spawnParticle(location, EnumParticle.PORTAL, 0.75F, 0.5F, 0.75F, 0.45F, 55);
        spawnParticle(location, EnumParticle.SPELL_WITCH, 0.65F, 0.45F, 0.65F, 0.15F, 45);
        spawnParticle(location, EnumParticle.CRIT_MAGIC, 0.65F, 0.35F, 0.65F, 0.04F, 45);
        spawnParticle(location, EnumParticle.ENCHANTMENT_TABLE, 0.85F, 0.6F, 0.85F, 0.9F, 50);
        spawnParticle(location.clone().add(0.0D, 0.3D, 0.0D), EnumParticle.FIREWORKS_SPARK, 0.55F, 0.3F, 0.55F, 0.08F, 25);
    }

    private void playThunder(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(location, Sound.AMBIENCE_THUNDER, 2.2F, 1.1F);
        world.playSound(location, Sound.EXPLODE, 2.3F, 0.85F);
        world.strikeLightningEffect(location);
        world.playEffect(location, Effect.EXPLOSION_HUGE, 0);

        spawnParticle(location, EnumParticle.EXPLOSION_LARGE, 0.0F, 0.0F, 0.0F, 0.0F, 6);
        spawnParticle(location, EnumParticle.FIREWORKS_SPARK, 0.7F, 0.45F, 0.7F, 0.12F, 40);
        spawnParticle(location, EnumParticle.CRIT, 0.8F, 0.5F, 0.8F, 0.08F, 35);
        spawnParticle(location, EnumParticle.SMOKE_LARGE, 0.65F, 0.35F, 0.65F, 0.02F, 22);
        spawnParticle(location.clone().add(0.0D, 0.25D, 0.0D), EnumParticle.CLOUD, 0.55F, 0.15F, 0.55F, 0.02F, 15);
    }

    private void playNuclear(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(location, Sound.EXPLODE, 3.2F, 0.65F);
        world.playSound(location, Sound.WITHER_SPAWN, 1.1F, 1.35F);
        world.playSound(location, Sound.AMBIENCE_THUNDER, 1.6F, 0.85F);

        world.playEffect(location, Effect.EXPLOSION_HUGE, 0);
        world.playEffect(location, Effect.EXPLOSION_HUGE, 0);
        world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
        world.strikeLightningEffect(location);

        spawnParticle(location, EnumParticle.EXPLOSION_HUGE, 0.0F, 0.0F, 0.0F, 0.0F, 3);
        spawnParticle(location, EnumParticle.EXPLOSION_LARGE, 0.2F, 0.1F, 0.2F, 0.0F, 10);
        spawnParticle(location, EnumParticle.SMOKE_LARGE, 1.1F, 0.65F, 1.1F, 0.04F, 70);
        spawnParticle(location, EnumParticle.FLAME, 0.95F, 0.55F, 0.95F, 0.05F, 55);
        spawnParticle(location, EnumParticle.LAVA, 0.75F, 0.35F, 0.75F, 0.02F, 30);
        spawnParticle(location.clone().add(0.0D, 0.35D, 0.0D), EnumParticle.CLOUD, 0.95F, 0.25F, 0.95F, 0.03F, 35);
        spawnParticle(location.clone().add(0.0D, 0.2D, 0.0D), EnumParticle.FIREWORKS_SPARK, 0.9F, 0.45F, 0.9F, 0.12F, 45);
        spawnParticle(location.clone().add(0.0D, 0.25D, 0.0D), EnumParticle.CRIT_MAGIC, 0.9F, 0.45F, 0.9F, 0.06F, 28);
    }

    private void playDragonBurst(final Location location) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(location, Sound.ENDERDRAGON_GROWL, 2.6F, 1.05F);
        world.playSound(location, Sound.EXPLODE, 2.4F, 0.8F);
        world.playSound(location, Sound.ENDERMAN_TELEPORT, 1.3F, 0.7F);

        world.playEffect(location, Effect.EXPLOSION_HUGE, 0);
        world.playEffect(location, Effect.ENDER_SIGNAL, 0);

        spawnParticle(location, EnumParticle.EXPLOSION_LARGE, 0.0F, 0.0F, 0.0F, 0.0F, 7);
        spawnParticle(location, EnumParticle.PORTAL, 1.0F, 0.55F, 1.0F, 0.55F, 80);
        spawnParticle(location, EnumParticle.SMOKE_LARGE, 0.95F, 0.45F, 0.95F, 0.04F, 40);
        spawnParticle(location.clone().add(0.0D, 0.35D, 0.0D), EnumParticle.CRIT_MAGIC, 0.8F, 0.3F, 0.8F, 0.06F, 40);

        createRing(location.clone().add(0.0D, 0.15D, 0.0D), EnumParticle.PORTAL, 1.8D, 30, 0.02F);
        createRing(location.clone().add(0.0D, 0.4D, 0.0D), EnumParticle.SPELL_WITCH, 1.2D, 24, 0.02F);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (location.getWorld() == null) {
                    return;
                }

                spawnParticle(location.clone().add(0.0D, 0.65D, 0.0D), EnumParticle.CLOUD, 0.85F, 0.2F, 0.85F, 0.03F, 25);
                spawnParticle(location.clone().add(0.0D, 0.7D, 0.0D), EnumParticle.PORTAL, 0.75F, 0.25F, 0.75F, 0.38F, 35);
                spawnParticle(location.clone().add(0.0D, 0.7D, 0.0D), EnumParticle.CRIT_MAGIC, 0.7F, 0.3F, 0.7F, 0.04F, 25);
            }
        }, 3L);
    }

    private void playFrostNova(final Location location) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(location, Sound.GLASS, 1.6F, 0.55F);
        world.playSound(location, Sound.EXPLODE, 1.9F, 1.5F);
        world.playSound(location, Sound.BAT_TAKEOFF, 1.1F, 1.8F);

        world.playEffect(location, Effect.EXPLOSION_LARGE, 0);

        spawnParticle(location, EnumParticle.CLOUD, 0.95F, 0.25F, 0.95F, 0.03F, 55);
        spawnParticle(location, EnumParticle.SNOW_SHOVEL, 1.05F, 0.4F, 1.05F, 0.05F, 75);
        spawnParticle(location.clone().add(0.0D, 0.25D, 0.0D), EnumParticle.CRIT, 0.7F, 0.25F, 0.7F, 0.04F, 25);

        createRing(location.clone().add(0.0D, 0.12D, 0.0D), EnumParticle.SNOW_SHOVEL, 1.9D, 34, 0.01F);
        createRing(location.clone().add(0.0D, 0.28D, 0.0D), EnumParticle.CLOUD, 1.25D, 26, 0.01F);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                spawnParticle(location.clone().add(0.0D, 0.35D, 0.0D), EnumParticle.CLOUD, 0.75F, 0.15F, 0.75F, 0.03F, 25);
                spawnParticle(location.clone().add(0.0D, 0.35D, 0.0D), EnumParticle.SNOW_SHOVEL, 0.85F, 0.25F, 0.85F, 0.04F, 32);
            }
        }, 2L);
    }

    private void playShadowVoid(final Location location) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(location, Sound.WITHER_HURT, 1.8F, 0.7F);
        world.playSound(location, Sound.ENDERMAN_TELEPORT, 1.4F, 0.55F);
        world.playSound(location, Sound.EXPLODE, 2.0F, 0.55F);

        world.playEffect(location, Effect.ENDER_SIGNAL, 0);
        world.playEffect(location, Effect.EXPLOSION_LARGE, 0);

        spawnParticle(location, EnumParticle.PORTAL, 1.15F, 0.6F, 1.15F, 0.65F, 95);
        spawnParticle(location, EnumParticle.SMOKE_LARGE, 0.95F, 0.45F, 0.95F, 0.04F, 55);
        spawnParticle(location.clone().add(0.0D, 0.25D, 0.0D), EnumParticle.SPELL_WITCH, 0.75F, 0.35F, 0.75F, 0.12F, 38);

        createRing(location.clone().add(0.0D, 0.1D, 0.0D), EnumParticle.PORTAL, 2.1D, 36, 0.02F);
        createRing(location.clone().add(0.0D, 0.25D, 0.0D), EnumParticle.SMOKE_LARGE, 1.45D, 28, 0.01F);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                spawnParticle(location.clone().add(0.0D, 0.45D, 0.0D), EnumParticle.PORTAL, 0.85F, 0.35F, 0.85F, 0.45F, 35);
                spawnParticle(location.clone().add(0.0D, 0.35D, 0.0D), EnumParticle.CRIT_MAGIC, 0.65F, 0.2F, 0.65F, 0.03F, 18);
            }
        }, 3L);
    }

    private void createRing(Location center, EnumParticle particle, double radius, int points, float speed) {
        if (center == null || center.getWorld() == null || points <= 0) {
            return;
        }

        for (int i = 0; i < points; i++) {
            double angle = (Math.PI * 2.0D * i) / points;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location point = center.clone().add(x, 0.0D, z);
            spawnParticle(point, particle, 0.02F, 0.02F, 0.02F, speed, 2);
        }
    }

    private void spawnParticle(Location location,
                               EnumParticle particle,
                               float offsetX,
                               float offsetY,
                               float offsetZ,
                               float speed,
                               int count) {
        if (location == null || location.getWorld() == null || particle == null || count < 0) {
            return;
        }

        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                particle,
                true,
                (float) location.getX(),
                (float) location.getY(),
                (float) location.getZ(),
                offsetX,
                offsetY,
                offsetZ,
                speed,
                count
        );

        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(location) <= 64 * 64) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }
}