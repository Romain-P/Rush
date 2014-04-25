package fr.rushland.core;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class ServerStuff 
{
	static final String VIP_PREFIX = ChatColor.YELLOW + "VIP";
	static Inventory kitInv = null;
	
	//icons
	//normal
    static ItemStack warriorIcon;
    static ItemStack hunterIcon;
    //vip
    static ItemStack trollIcon;
    static ItemStack ninjaIcon;
    static ItemStack mageIcon;
    
    static void initializeStuff()
    {
    	kitInv = Bukkit.createInventory(null, 9, "Kits");
    	intializeIcons();
    	kitInv.setItem(2, warriorIcon);
    	kitInv.setItem(3, hunterIcon);
    	kitInv.setItem(4, trollIcon);
    	kitInv.setItem(5, ninjaIcon);
    	kitInv.setItem(6, mageIcon);
    }
    
    static void intializeIcons()
    {
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
    
    
    void giveWarriorStuff(Player player)
    {
    	Utils.goNaked(player);
    	player.getInventory();
    }
}
