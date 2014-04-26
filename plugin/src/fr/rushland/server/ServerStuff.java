package fr.rushland.server;

import java.util.ArrayList;

import com.google.inject.Inject;
import fr.rushland.core.Config;
import fr.rushland.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class ServerStuff {
    @Getter public final String VIP_PREFIX = ChatColor.YELLOW + "VIP";
    @Getter private Inventory kitInv = null;
	@Getter private ItemStack warriorIcon;
    @Getter private ItemStack hunterIcon;
    @Getter private ItemStack trollIcon;
    @Getter private ItemStack ninjaIcon;
    @Getter private ItemStack mageIcon;
    @Getter private ItemStack lobbyItems;
    @Getter private ItemStack pvpItems;

    @Inject Config config;
    @Inject JavaPlugin plugin;
    
    public void initializeStuff() {
    	kitInv = Bukkit.createInventory(null, 9, "Kits");
    	intializeIcons();

    	kitInv.setItem(2, warriorIcon);
    	kitInv.setItem(3, hunterIcon);
    	kitInv.setItem(4, trollIcon);
    	kitInv.setItem(5, ninjaIcon);
    	kitInv.setItem(6, mageIcon);
    }

    public void intializeItems() {
        lobbyItems = new ItemStack(Material.COMPASS);
        ItemMeta meta = lobbyItems.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Teleportation au Lobby");
        lobbyItems.setItemMeta(meta);

        pvpItems = new ItemStack(Material.WOOD_SWORD);
        meta = pvpItems.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Lancer le PVP");
        pvpItems.setItemMeta(meta);
    }

    public void giveStartingItems(Player player) {
        Utils.goNaked(player);
        PlayerInventory inv = player.getInventory();
        try {
            inv.addItem(lobbyItems);
        } catch(Exception e) {
            plugin.getLogger().warning("error when trying to add lobbyItems");
        }

        if (config.isMainServer())
            try {
                inv.addItem(pvpItems);
            } catch(Exception e) {
                plugin.getLogger().warning("error when trying to add pvpItems");
            }

        player.updateInventory();
    }

    private void intializeIcons() {
    	warriorIcon = new ItemStack(Material.IRON_SWORD);
    	ItemMeta meta = warriorIcon.getItemMeta();
    	meta.setDisplayName(ChatColor.GRAY + "Warrior");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("A barbarian from the north skilled with the sword");
        meta.setLore(lore);
    	warriorIcon.setItemMeta(meta);

    	hunterIcon = new ItemStack(Material.BOW);
    	meta = hunterIcon.getItemMeta();
    	meta.setDisplayName(ChatColor.GRAY + "Hunter");
        lore = new ArrayList<String>();
        lore.add("A solitary individual who poaches in forests");
        meta.setLore(lore);
    	hunterIcon.setItemMeta(meta);

    	//vip now yo

    	trollIcon = new ItemStack(Material.STICK);
    	meta = trollIcon.getItemMeta();
    	meta.setDisplayName(ChatColor.RED + "Troll");
    	meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        lore = new ArrayList<String>();
        lore.add("The troll likes eating humans and picking his nose");
        lore.add(VIP_PREFIX);
        meta.setLore(lore);
    	trollIcon.setItemMeta(meta);
    	
    	ninjaIcon = new ItemStack(Material.IRON_HOE);
    	meta = ninjaIcon.getItemMeta();
    	meta.setDisplayName(ChatColor.RED + "Ninja");
    	meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
        lore = new ArrayList<String>();
        lore.add("A warrior from the east shrouded in mystery");
        lore.add(VIP_PREFIX);
        meta.setLore(lore);
    	ninjaIcon.setItemMeta(meta);
    	
        Potion potion = new Potion(PotionType.INSTANT_DAMAGE);
        ItemStack potionItem = potion.toItemStack(1);
    	mageIcon = potionItem;
    	meta = mageIcon.getItemMeta();
    	meta.setDisplayName(ChatColor.RED + "Mage");
    	meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
        lore = new ArrayList<String>();
        lore.add("Skilled in the art of brewing potions, the mage is a formidable fighter");
        lore.add(VIP_PREFIX);
        meta.setLore(lore);
    	mageIcon.setItemMeta(meta);
    }

    public void giveWarriorStuff(Player player) {
    	Utils.goNaked(player);
    	player.getInventory();
    }
}
