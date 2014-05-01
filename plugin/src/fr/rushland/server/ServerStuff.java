package fr.rushland.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import fr.rushland.core.Config;
import fr.rushland.enums.Constants;
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
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ServerStuff {
    @Getter public final String VIP_PREFIX = ChatColor.YELLOW + "VIP";
    @Getter private Map<String, Inventory> inventories;
    @Getter private Map<String, ItemStack> pvpItems;
    @Getter private Map<String, ItemStack> bonusItems;
    @Getter private Map<String, ItemStack> prestigeItems;

    //special pvpItems
    @Getter private ItemStack lobbyItem;
    @Getter private ItemStack pvpItem;
    @Getter private ItemStack prestigeItem;

    @Inject Config config;
    @Inject Server server;

    public ServerStuff() {
        this.inventories = new HashMap<>();
        this.pvpItems = new HashMap<>();
        this.bonusItems = new HashMap<>();
        this.prestigeItems = new HashMap<>();
    }
    
    public void initializeStuffs() {
    	Inventory kits = Bukkit.createInventory(null, 9, "Kits");
        //on running rush & player vip 3
        Inventory bonus = Bukkit.createInventory(null, 9, "Bonus");
        //prestiges
        Inventory prestiges = Bukkit.createInventory(null, 9, "Prestiges");

        this.inventories.put("Kits", kits);
        this.inventories.put("Bonus", bonus);
        this.inventories.put("Prestiges", prestiges);

    	intializeIcons();
    	for(ItemStack item: this.pvpItems.values())
            kits.addItem(item);

        initializeBonusIcons();
        for(ItemStack item: this.bonusItems.values())
            bonus.addItem(item);

        initializePrestigeIcons();
        for(ItemStack item: this.prestigeItems.values())
            prestiges.addItem(item);
    }

    public void initializeSpecialItems() {
        this.lobbyItem = createCustomItem(
                Material.COMPASS, -1,
                ChatColor.RED+"Teleportation au spawn",
                new String[] {"Cliquez pour vous teleporter!", VIP_PREFIX}
        );

        this.pvpItem = createCustomItem(
                Material.WOOD_SWORD, -1,
                ChatColor.RED+"Lancer le PVP",
                new String[] {"Cliquez pour voir les classes!", VIP_PREFIX}
        );

        this.prestigeItem = createCustomItem(
                Material.EMERALD, -1,
                ChatColor.RED+"Monter de prestige",
                new String[] {"Cliquez pour voir les prestiges disponibles", VIP_PREFIX}
        );
    }

    private void intializeIcons() {
        this.pvpItems.put("Guerrier", createCustomItem(
                Material.IRON_SWORD, -1,
                ChatColor.GRAY+"Guerrier",
                new String[] {"Incarnez un barbard qui assome tout ce qui bouge!"}
        ));

        this.pvpItems.put("Archer", createCustomItem(
                Material.BOW, -1,
                ChatColor.GRAY + "Archer",
                new String[]{"Incarnez l'ame d'un archer puissant!"}
        ));

        //vip now yo
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(Enchantment.KNOCKBACK, 2);

        this.pvpItems.put("Archer puissant", createCustomItem(
                Material.BOW, -1,
                ChatColor.RED+"Archer puissant",
                new String[] {"Incarnez l'ame d'un archer puissant!", VIP_PREFIX, "2"},
                map
        ));

        this.pvpItems.put("Troll", createCustomItem(
                Material.STICK, -1,
                ChatColor.RED+"Troll",
                new String[] {"Incarnez un devoreur puissant!", VIP_PREFIX, "1"},
                map
        ));

        this.pvpItems.put("Ninja", createCustomItem(
                Material.IRON_HOE, -1,
                ChatColor.RED+"Ninja",
                new String[] {"Incarnez la maitrise des techniques ninjas!", VIP_PREFIX, "1"},
                map
        ));

        this.pvpItems.put("Mage", createCustomItem(
                new Potion(PotionType.INSTANT_DAMAGE), 1,
                ChatColor.RED + "Mage",
                new String[]{"Incarnez un puissant personnage magique!", VIP_PREFIX, "1"},
                map
        ));

        this.pvpItems.put("Spider", createCustomItem(
                Material.WEB, -1,
                ChatColor.RED + "Spider",
                new String[]{"Incarnez la puissance d'un arreignee malefique!", VIP_PREFIX, "2"},
                map
        ));

        this.pvpItems.put("Mastodonte", createCustomItem(
                Material.IRON_SWORD, -1,
                ChatColor.RED + "Mastodonte",
                new String[]{"Incarnez une brute intombable!", VIP_PREFIX, "3"},
                map
        ));

        this.pvpItems.put("God", createCustomItem(
                Material.GOLDEN_APPLE, -1,
                ChatColor.RED + "God",
                new String[]{"Incarnez dieu!", VIP_PREFIX, "5"},
                map
        ));
    }

    public void initializeBonusIcons() {
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(Enchantment.KNOCKBACK, 2);

        this.bonusItems.put("Bonus1", createCustomItem(
                Material.GOLD_SWORD, -1,
                ChatColor.RED+"Bonus arme en or !",
                new String[] {"Cliquez sur ce bonus pour commencer votre partie!", VIP_PREFIX},
                map
        ));

        this.bonusItems.put("Bonus2", createCustomItem(
                Material.CHAINMAIL_CHESTPLATE, -1,
                ChatColor.RED + "Bonus armure en mail!",
                new String[]{"Cliquez sur ce bonus pour commencer votre partie!", VIP_PREFIX},
                map
        ));
    }

    public void initializePrestigeIcons() {
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(Enchantment.FIRE_ASPECT, 1);

        this.prestigeItems.put("1", createCustomItem(
                Material.EMERALD, 1,
                ChatColor.RED+"Prestige I",
                new String[] {"Prestige I contre 1200 tokens"}
        ));

        this.prestigeItems.put("2", createCustomItem(
                Material.EMERALD, 2,
                ChatColor.RED+"Prestige II",
                new String[] {"Prestige II contre 3000 tokens"}
        ));

        this.prestigeItems.put("3", createCustomItem(
                Material.EMERALD, 3,
                ChatColor.RED+"Prestige III",
                new String[] {"Prestige III contre 5000 tokens"}
        ));

        this.prestigeItems.put("4", createCustomItem(
                Material.EMERALD, 4,
                ChatColor.RED+"Prestige IV",
                new String[] {"Prestige IV contre 7000 tokens"}
        ));

        this.prestigeItems.put("5", createCustomItem(
                Material.EMERALD, 5,
                ChatColor.RED+"Prestige V",
                new String[] {"Prestige V contre 9000 tokens"}
        ));
    }

    public void giveStartingItems(Player player) {
        Utils.goNaked(player);
        PlayerInventory inventory = player.getInventory();

        inventory.addItem(lobbyItem);
        if (config.isMainServer()) {
            inventory.addItem(pvpItem);
            inventory.addItem(prestigeItem);
        }

        player.updateInventory();
    }

    public void giveVipBonus(Player player) {
        if(server.getPlayers().containsKey(player.getName())) {
            int grade = server.getPlayers().get(player.getName()).getGrade();
            Map<Enchantment, Integer> enchantments = new HashMap<>();

            switch(grade) {
                case 5:
                    player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                    addEnchantment(player.getEquipment().getChestplate(), Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    player.getInventory().addItem(new ItemStack(Material.SANDSTONE, 44));

                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Constants.SECONDS_IN_YEAR.getValue(), 0));
                case 4:
                    ItemStack weapon = new ItemStack(Material.DIAMOND_PICKAXE);
                    addEnchantment(weapon, Enchantment.DIG_SPEED, 2);

                    player.getInventory().addItem(weapon);
                    player.getInventory().addItem(new ItemStack(Material.ARROW, 3));

                    ItemStack sword = new ItemStack(Material.IRON_SWORD);
                    enchantments.clear();
                    enchantments.put(Enchantment.DAMAGE_ALL, 1);
                    enchantments.put(Enchantment.KNOCKBACK, 1);
                    addEnchantment(sword, enchantments);

                    player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                    if(grade != 5)
                        player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                    player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                    player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));

                    for(ItemStack item: player.getEquipment().getArmorContents())
                        addEnchantment(item, Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                case 3:
                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
                    player.getInventory().addItem(new ItemStack(Material.SANDSTONE, 20));
                    if(grade == 3)
                        player.openInventory(inventories.get("Bonus"));
                    break;
            }
        }
    }

    public ItemStack createCustomItem(Material material, int quantity, String name,
                                      String[] lores, Map<Enchantment, Integer> enchantments) {
        ItemStack item = quantity > 0 ? new ItemStack(material, quantity): new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        List<String> list = new ArrayList<>();
        for(String s: lores)
            list.add(s);
        meta.setLore(list);

        if(enchantments != null)
            for(Map.Entry<Enchantment, Integer> entry: enchantments.entrySet())
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createCustomItem(Potion material, int quantity, String name,
                                      String[] lores, Map<Enchantment, Integer> enchantments) {
        ItemStack item = material.toItemStack(quantity);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        List<String> list = new ArrayList<>();
        for(String s: lores)
            list.add(s);
        meta.setLore(list);

        if(enchantments != null)
            for(Map.Entry<Enchantment, Integer> entry: enchantments.entrySet())
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createCustomItem(Material material, int quantity, String name, String[] lores) {
        return createCustomItem(material, quantity, name, lores, null);
    }

    public void addEnchantment(ItemStack item, Enchantment enchantment, int level) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("Powerful");
        List<String> list = new ArrayList<>();
        list.add("BUFFED");

        meta.setLore(list);

        meta.addEnchant(enchantment, level, true);
        item.setItemMeta(meta);
    }

    public void addEnchantment(ItemStack item, Map<Enchantment, Integer> enchantments) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("Powerful");
        List<String> list = new ArrayList<>();
        list.add("BUFFED");

        meta.setLore(list);

        if(enchantments != null)
            for(Map.Entry<Enchantment, Integer> entry: enchantments.entrySet())
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
        item.setItemMeta(meta);
    }
}
