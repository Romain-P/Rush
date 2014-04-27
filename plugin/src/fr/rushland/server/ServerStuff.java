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
    @Getter private Inventory kitInv;
    @Getter private Inventory vipInventory;
	@Getter private ItemStack warriorIcon;
    @Getter private ItemStack hunterIcon;
    @Getter private ItemStack trollIcon;
    @Getter private ItemStack ninjaIcon;
    @Getter private ItemStack mageIcon;
    @Getter private ItemStack hunterVipIcon;
    @Getter private ItemStack spiderIcon;
    @Getter private ItemStack mastodonteIcon;

    @Getter private ItemStack lobbyItems;
    @Getter private ItemStack pvpItems;

    //bonus
    @Getter private ItemStack swordBonus;
    @Getter private ItemStack chestplate;

    @Inject Config config;
    @Inject JavaPlugin plugin;
    
    public void initializeStuff() {
    	kitInv = Bukkit.createInventory(null, 8, "Kits");
    	intializeIcons();

    	kitInv.setItem(0, warriorIcon);
    	kitInv.setItem(1, hunterIcon);
        kitInv.setItem(2, hunterVipIcon);
    	kitInv.setItem(3, trollIcon);
    	kitInv.setItem(4, ninjaIcon);
    	kitInv.setItem(5, mageIcon);
        kitInv.setItem(6, spiderIcon);
        kitInv.setItem(7, mastodonteIcon);

        //on running rush & player vip 3
        vipInventory = Bukkit.createInventory(null, 2, "Bonus");
        initializeBonusIcons();

        vipInventory.setItem(0, swordBonus);
        vipInventory.setItem(1, chestplate);
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
    	meta.setDisplayName(ChatColor.GRAY + "Guerrier");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Incarnez un barbard qui assome tout ce qui bouge!");
        meta.setLore(lore);
    	warriorIcon.setItemMeta(meta);

        hunterIcon = new ItemStack(Material.BOW);
        meta = hunterIcon.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Archer");
        lore.clear();
        lore.add("Incarnez l'ame d'un archer puissant et insolite");
        meta.setLore(lore);
        hunterIcon.setItemMeta(meta);

    	//vip now yo

        hunterVipIcon = new ItemStack(Material.BOW);
        meta = hunterVipIcon.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Archer Puissant");
        meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        lore.clear();
        lore.add("Incarnez l'ame d'un archer surpuissant !!");
        lore.add(VIP_PREFIX);
        lore.add("2");
        meta.setLore(lore);
        hunterVipIcon.setItemMeta(meta);

    	trollIcon = new ItemStack(Material.STICK);
    	meta = trollIcon.getItemMeta();
    	meta.setDisplayName(ChatColor.RED + "Troll");
    	meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        lore.clear();
        lore.add("Incarnez un devoreur surpuissant");
        lore.add(VIP_PREFIX);
        lore.add("1");
        meta.setLore(lore);
    	trollIcon.setItemMeta(meta);
    	
    	ninjaIcon = new ItemStack(Material.IRON_HOE);
    	meta = ninjaIcon.getItemMeta();
    	meta.setDisplayName(ChatColor.RED + "Ninja");
    	meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
        lore.clear();
        lore.add("Incarnez la maitrise des techniques ninja");
        lore.add(VIP_PREFIX);
        lore.add("1");
        meta.setLore(lore);
    	ninjaIcon.setItemMeta(meta);
    	
        Potion potion = new Potion(PotionType.INSTANT_DAMAGE);
        ItemStack potionItem = potion.toItemStack(1);
    	mageIcon = potionItem;
    	meta = mageIcon.getItemMeta();
    	meta.setDisplayName(ChatColor.RED + "Mage");
    	meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
        lore.clear();
        lore.add("Incarnez un puissant personnage magique");
        lore.add(VIP_PREFIX);
        lore.add("1");
        meta.setLore(lore);
    	mageIcon.setItemMeta(meta);

        spiderIcon = new ItemStack(Material.WEB);
        meta = spiderIcon.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Spider");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
        lore.clear();
        lore.add("Incarnez la puissance d'une arreignee malefique");
        lore.add(VIP_PREFIX);
        lore.add("2");
        meta.setLore(lore);
        spiderIcon.setItemMeta(meta);

        mastodonteIcon = new ItemStack(Material.IRON_SWORD);
        meta = mastodonteIcon.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Mastodonte");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
        lore = new ArrayList<String>();
        lore.add("Incarnez une brute intombable!");
        lore.add(VIP_PREFIX);
        lore.add("3");
        meta.setLore(lore);
        mastodonteIcon.setItemMeta(meta);
    }

    public void initializeBonusIcons() {
        swordBonus = new ItemStack(Material.GOLD_SWORD);
        ItemMeta meta = swordBonus.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Bonus arme en or !");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Cliquez sur ce bonus pour commencer votre partie!");
        lore.add(VIP_PREFIX);
        meta.setLore(lore);
        swordBonus.setItemMeta(meta);

        mastodonteIcon = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        meta = mastodonteIcon.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Bonus armure de mail");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        lore = new ArrayList<>();
        lore.add("Cliquez sur ce bonus pour commencer votre partie!");
        lore.add(VIP_PREFIX);
        meta.setLore(lore);
        mastodonteIcon.setItemMeta(meta);
    }
}
