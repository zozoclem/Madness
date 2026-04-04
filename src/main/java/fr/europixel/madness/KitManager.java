package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitManager {

    public void giveKit(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE, 1);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 2);

        ItemStack goldenApples = new ItemStack(Material.GOLDEN_APPLE, 3);

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, ItemFactory.createTntItem());
        player.getInventory().setItem(2, ItemFactory.createJetpackItem());
        player.getInventory().setItem(3, pickaxe);
        player.getInventory().setItem(4, goldenApples);

        for (int slot = 5; slot <= 8; slot++) {
            player.getInventory().setItem(slot, new ItemStack(Material.SANDSTONE, 64));
        }

        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setLevel(0);
        player.setExp(0.0F);
        player.updateInventory();
    }
}