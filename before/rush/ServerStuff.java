package fr.rushland.rush;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;

public class ServerStuff {
	static final String VIP_PREFIX = ChatColor.YELLOW + "VIP";
	static Inventory kitInv = null;
	static ItemStack warriorIcon;
	static ItemStack hunterIcon;
	static ItemStack trollIcon;
	static ItemStack ninjaIcon;
	static ItemStack mageIcon;

	public static void initializeStuff() {
		kitInv = Bukkit.createInventory(null, 9, "Kits");
		intializeIcons();
		kitInv.setItem(2, warriorIcon);
		kitInv.setItem(3, hunterIcon);
		kitInv.setItem(4, trollIcon);
		kitInv.setItem(5, ninjaIcon);
		kitInv.setItem(6, mageIcon);
	}

	public static void intializeIcons() {
		warriorIcon = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = warriorIcon.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Guerrier");
		ArrayList<String> lore = new ArrayList();
		lore.add("");
		meta.setLore(lore);
		warriorIcon.setItemMeta(meta);

		hunterIcon = new ItemStack(Material.BOW);
		meta = hunterIcon.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Hunter");
		lore = new ArrayList();
		lore.add("");
		meta.setLore(lore);
		hunterIcon.setItemMeta(meta);


		trollIcon = new ItemStack(Material.STICK);
		meta = trollIcon.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Troll");
		meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
		lore = new ArrayList();
		lore.add("");
		lore.add(VIP_PREFIX);
		meta.setLore(lore);
		trollIcon.setItemMeta(meta);

		ninjaIcon = new ItemStack(Material.IRON_HOE);
		meta = ninjaIcon.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Ninja");
		meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
		lore = new ArrayList();
		lore.add("");
		lore.add(VIP_PREFIX);
		meta.setLore(lore);
		ninjaIcon.setItemMeta(meta);

		Potion potion = new Potion(PotionType.INSTANT_DAMAGE);
		ItemStack potionItem = potion.toItemStack(1);
		mageIcon = potionItem;
		meta = mageIcon.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Mage");
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		lore = new ArrayList();
		lore.add("");
		lore.add(VIP_PREFIX);
		meta.setLore(lore);
		mageIcon.setItemMeta(meta);
	}
}