package fr.europixel.madness.shop;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.item.ConfigItemFactory;
import fr.europixel.madness.utils.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockShopManager {

    private final MadnessPlugin plugin;

    public BlockShopManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void ensurePlayer(Player player) {
        plugin.getDatabaseManager().ensureBlockShopPlayer(
                player.getUniqueId().toString(),
                getDefaultBlockKey()
        );
    }

    public ItemStack[] applySelectedBlock(Player player, ItemStack[] hotbar) {
        if (hotbar == null) {
            return null;
        }

        ensurePlayer(player);

        ItemStack[] copy = new ItemStack[hotbar.length];
        for (int i = 0; i < hotbar.length; i++) {
            ItemStack item = hotbar[i] == null ? null : hotbar[i].clone();
            if (item != null && isPlaceableBlock(item)) {
                item = createSelectedBlockItem(player, item.getAmount());
            }
            copy[i] = item;
        }

        return copy;
    }

    public boolean isPlaceableBlock(ItemStack item) {
        if (item == null) {
            return false;
        }

        return isPlaceableBlock(item.getType(), item.getDurability());
    }

    public boolean isPlaceableBlock(Block block) {
        if (block == null) {
            return false;
        }

        return isPlaceableBlock(block.getType(), block.getData());
    }

    public boolean isPlaceableBlock(Material material, int data) {
        if (material == null || isForbiddenMaterial(material)) {
            return false;
        }

        String token = toToken(material, (short) data);

        if (token.equalsIgnoreCase(getDefaultBlockKey())) {
            return true;
        }

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("block-shop.blocks");
        if (section == null) {
            return false;
        }

        for (String key : section.getKeys(false)) {
            String blockKey = getBlockKey(key);
            if (blockKey != null && token.equalsIgnoreCase(blockKey)) {
                return true;
            }
        }

        return false;
    }

    public int countOwnedBlockAmount(ItemStack[] hotbar) {
        int amount = 0;

        if (hotbar == null) {
            return amount;
        }

        for (ItemStack item : hotbar) {
            if (item == null) {
                continue;
            }

            if (isPlaceableBlock(item)) {
                amount += item.getAmount();
            }
        }

        return amount;
    }

    public int getRequiredBlockAmount() {
        int amount = 0;
        ItemStack[] defaults = plugin.getKitManager().createDefaultHotbar();

        for (ItemStack item : defaults) {
            if (item == null) {
                continue;
            }

            if (isPlaceableBlock(item)) {
                amount += item.getAmount();
            }
        }

        return amount;
    }

    public ItemStack createSelectedBlockItem(Player player, int amount) {
        String token = getSelectedBlockKey(player);
        Material material = parseMaterial(token);
        short data = parseData(token);

        return new ItemStack(material, Math.max(1, amount), data);
    }

    public String getSelectedBlockKey(Player player) {
        ensurePlayer(player);

        String selected = plugin.getDatabaseManager().getSelectedBlock(player.getUniqueId().toString());
        if (selected == null || !isConfiguredToken(selected)) {
            selected = getDefaultBlockKey();
            plugin.getDatabaseManager().setSelectedBlock(player.getUniqueId().toString(), selected);
        }

        return selected;
    }

    public void setSelectedBlockKey(Player player, String token) {
        ensurePlayer(player);
        plugin.getDatabaseManager().setSelectedBlock(player.getUniqueId().toString(), token);
    }

    public boolean owns(Player player, String token) {
        ensurePlayer(player);
        return getOwned(player).contains(token);
    }

    public void unlock(Player player, String token) {
        ensurePlayer(player);
        plugin.getDatabaseManager().addOwnedBlock(player.getUniqueId().toString(), token);
    }

    public Set<String> getOwned(Player player) {
        ensurePlayer(player);
        return plugin.getDatabaseManager().getOwnedBlocks(player.getUniqueId().toString());
    }

    public void fillInventory(Player player, Inventory inventory) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("block-shop.blocks");
        if (section == null) {
            return;
        }

        for (String optionId : section.getKeys(false)) {
            int slot = getSlot(optionId);
            if (slot < 0 || slot >= inventory.getSize()) {
                continue;
            }

            inventory.setItem(slot, buildDisplayItem(player, optionId));
        }
    }

    public String getOptionIdBySlot(int slot) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("block-shop.blocks");
        if (section == null) {
            return null;
        }

        for (String optionId : section.getKeys(false)) {
            if (getSlot(optionId) == slot) {
                return optionId;
            }
        }

        return null;
    }

    public void handleClick(Player player, String optionId) {
        String token = getBlockKey(optionId);
        if (token == null) {
            return;
        }

        if (owns(player, token)) {
            setSelectedBlockKey(player, token);
            player.sendMessage(
                    ConfigUtil.color(plugin.getConfig().getString("messages.shop.selected", "&aYou selected &f%block%&a."))
                            .replace("%block%", getPrettyName(optionId))
            );
            return;
        }

        double price = getPrice(optionId);

        if (plugin.getVaultEconomyHook() == null || !plugin.getVaultEconomyHook().isAvailable()) {
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.shop.no-economy", "&cEconomy is unavailable.")));
            return;
        }

        if (!plugin.getVaultEconomyHook().has(player, price)) {
            player.sendMessage(
                    ConfigUtil.color(plugin.getConfig().getString("messages.shop.not-enough", "&cYou do not have enough coins."))
                            .replace("%price%", format(price))
            );
            return;
        }

        if (!plugin.getVaultEconomyHook().withdraw(player, price)) {
            player.sendMessage(ConfigUtil.color(plugin.getConfig().getString("messages.shop.purchase-failed", "&cPurchase failed.")));
            return;
        }

        unlock(player, token);
        setSelectedBlockKey(player, token);

        player.sendMessage(
                ConfigUtil.color(plugin.getConfig().getString("messages.shop.bought", "&aYou bought &f%block% &afor &6%price% coins&a."))
                        .replace("%block%", getPrettyName(optionId))
                        .replace("%price%", format(price))
        );
    }

    private ItemStack buildDisplayItem(Player player, String optionId) {
        ConfigurationSection option = getOptionSection(optionId);
        ItemStack item = ConfigItemFactory.fromSection(option, Material.SANDSTONE, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();

            String token = getBlockKey(optionId);
            boolean owned = token != null && owns(player, token);
            boolean selected = token != null && token.equalsIgnoreCase(getSelectedBlockKey(player));
            double price = getPrice(optionId);

            lore.add(" ");

            if (selected) {
                lore.add(ConfigUtil.color(plugin.getConfig().getString("block-shop.status.selected", "&aSelected")));
            } else if (owned) {
                lore.add(ConfigUtil.color(plugin.getConfig().getString("block-shop.status.owned", "&eOwned &7(click to select)")));
            } else {
                lore.add(
                        ConfigUtil.color(plugin.getConfig().getString("block-shop.status.buy", "&6Price: &e%price% coins &7(click to buy)"))
                                .replace("%price%", format(price))
                );
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ConfigurationSection getOptionSection(String optionId) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("block-shop.blocks");
        return section == null ? null : section.getConfigurationSection(optionId);
    }

    private String getBlockKey(String optionId) {
        ConfigurationSection section = getOptionSection(optionId);
        if (section == null) {
            return null;
        }

        Material material = ConfigUtil.getMaterial(section.getString("material"), Material.SANDSTONE);
        if (isForbiddenMaterial(material)) {
            return null;
        }

        short data = (short) section.getInt("data", 0);
        return toToken(material, data);
    }

    private int getSlot(String optionId) {
        ConfigurationSection section = getOptionSection(optionId);
        return section == null ? -1 : section.getInt("slot", -1);
    }

    private double getPrice(String optionId) {
        ConfigurationSection section = getOptionSection(optionId);
        return section == null ? 0.0D : section.getDouble("price", 0.0D);
    }

    private String getPrettyName(String optionId) {
        ConfigurationSection section = getOptionSection(optionId);
        if (section == null) {
            return optionId;
        }

        String name = section.getString("name");
        if (name == null || name.trim().isEmpty()) {
            return optionId;
        }

        return ConfigUtil.color(name);
    }

    private boolean isConfiguredToken(String token) {
        if (token == null) {
            return false;
        }

        if (token.equalsIgnoreCase(getDefaultBlockKey())) {
            return true;
        }

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("block-shop.blocks");
        if (section == null) {
            return false;
        }

        for (String optionId : section.getKeys(false)) {
            String blockKey = getBlockKey(optionId);
            if (blockKey != null && token.equalsIgnoreCase(blockKey)) {
                return true;
            }
        }

        return false;
    }

    private String getDefaultBlockKey() {
        String trackedMaterial = plugin.getConfig().getString("blocks.tracked-material", "SANDSTONE");
        Material material = ConfigUtil.getMaterial(trackedMaterial, Material.SANDSTONE);

        if (isForbiddenMaterial(material)) {
            material = Material.SANDSTONE;
        }

        return toToken(material, (short) 0);
    }

    private boolean isForbiddenMaterial(Material material) {
        return material == Material.BARRIER || material == Material.AIR;
    }

    private String toToken(Material material, short data) {
        return material.name() + ":" + data;
    }

    private Material parseMaterial(String token) {
        String[] split = token.split(":");
        Material material = ConfigUtil.getMaterial(split[0], Material.SANDSTONE);

        if (isForbiddenMaterial(material)) {
            return Material.SANDSTONE;
        }

        return material;
    }

    private short parseData(String token) {
        String[] split = token.split(":");
        if (split.length < 2) {
            return 0;
        }

        try {
            return Short.parseShort(split[1]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String format(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}