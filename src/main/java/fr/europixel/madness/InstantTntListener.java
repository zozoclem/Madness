package fr.europixel.madness;

import net.minecraft.server.v1_8_R3.PacketPlayOutExplosion;
import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collections;

public class InstantTntListener implements Listener {

    private final MadnessPlugin plugin;

    public InstantTntListener(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        Block block = event.getBlockPlaced();

        if (item == null || item.getType() != Material.TNT) {
            return;
        }

        if (plugin.getRechargeManager().isTntOnCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        double power = plugin.getConfig().getDouble("tnt.power");
        double minY = plugin.getConfig().getDouble("tnt.min-y");

        Location loc = block.getLocation();

        Vector v = player.getLocation().getDirection().multiply(power);
        if (v.getY() <= 0.0D) {
            v.setY(minY);
        }

        PacketPlayOutExplosion packet = new PacketPlayOutExplosion(
                (float) loc.getX(),
                (float) loc.getY(),
                (float) loc.getZ(),
                1.0F,
                Collections.emptyList(),
                new Vec3D(v.getX(), v.getY(), v.getZ())
        );

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        plugin.getRechargeManager().startTntRecharge(player);
        player.updateInventory();
    }
}