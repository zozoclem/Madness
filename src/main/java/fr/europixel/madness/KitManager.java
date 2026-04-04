package fr.europixel.madness;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitManager {

    private final MadnessPlugin plugin;

    public KitManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void giveKit(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE, 1);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 2);

        ItemStack goldenApples = new ItemStack(Material.GOLDEN_APPLE, 3);

        ItemStack helmet = new ItemStack(Material.IRON_HELMET, 1);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE, 1);
        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS, 1);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);

        player.getInventory().setArmorContents(new ItemStack[] {
                boots, leggings, chestplate, helmet
        });

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, ItemFactory.createTntItem());
        player.getInventory().setItem(2, ItemFactory.createJetpackItem());
        player.getInventory().setItem(3, pickaxe);
        player.getInventory().setItem(4, goldenApples);

        for (int slot = 5; slot <= 8; slot++) {
            player.getInventory().setItem(slot, new ItemStack(Material.SANDSTONE, 64));
        }

        applySavedHotbar(player);

        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setLevel(0);
        player.setExp(0.0F);
        player.getInventory().setHeldItemSlot(0);
        player.updateInventory();
    }

    private void applySavedHotbar(Player player) {
        ItemStack[] saved = plugin.getPlayerStatsManager().getHotbar(player);
        if (saved == null) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, saved[i] == null ? null : saved[i].clone());
        }
    }

    public ItemStack[] createDefaultHotbar() {
        ItemStack[] hotbar = new ItemStack[9];

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE, 1);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 2);

        hotbar[0] = sword;
        hotbar[1] = ItemFactory.createTntItem();
        hotbar[2] = ItemFactory.createJetpackItem();
        hotbar[3] = pickaxe;
        hotbar[4] = new ItemStack(Material.GOLDEN_APPLE, 3);

        for (int i = 5; i <= 8; i++) {
            hotbar[i] = new ItemStack(Material.SANDSTONE, 64);
        }

        return hotbar;
    }
}