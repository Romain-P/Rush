package fr.rushland.database.data;

import com.google.inject.Inject;
import com.google.inject.Injector;
import fr.rushland.database.Manager;
import fr.rushland.server.objects.Bonus;
import fr.rushland.server.objects.CustomStuff;
import fr.rushland.server.objects.Inventory;
import fr.rushland.server.objects.Item;
import fr.rushland.utils.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StuffManager extends Manager{
    @Inject JavaPlugin plugin;
    @Inject CustomStuff stuffs;
    @Inject Injector injector;

    public void loadData() {
        loadItems();
        loadInventories();
        loadBonus();
    }

    private void loadItems() {
        try {
            ResultSet result = getData("SELECT * FROM items;");

            while(result.next()) {
                Map<Enchantment, Integer> enchantments = new HashMap<>();
                for(String enchantment: result.getString("enchantments").split(";")) {
                    String[] split = enchantment.split(",");
                    enchantments.put(Enchantment.getByName(split[0]), Integer.parseInt(split[1]));
                }

                ArrayList<Item> items = new ArrayList<>();
                for(String item: result.getString("items").split(";"))
                    items.add(stuffs.getItems().get(item));

                Item item = new Item(
                        result.getString("name"),
                        result.getString("description"),
                        result.getString("material"),
                        enchantments,
                        result.getInt("quantity"),
                        result.getInt("grade"),
                        result.getString("command"),
                        items.toArray(new Item[0])
                );
                injector.injectMembers(item);
                stuffs.addItem(item);
            }
            closeResultSet(result);
        } catch(Exception e) {
            plugin.getLogger().warning("(sql error) : "+e.getMessage());
        }
    }

    private void loadInventories() {
        try {
            ResultSet result = getData("SELECT * FROM inventories;");

            while(result.next()) {
                Map<String, Item> items = new HashMap<>();
                for(String item: result.getString("items").split(";"))
                    items.put(item, stuffs.getItems().get(item));

                Inventory inventory = new Inventory(
                        result.getString("name"),
                        stuffs.getItems().get(result.getString("icon")),
                        items,
                        result.getString("main").equalsIgnoreCase("true") ? true:false,
                        result.getString("lobby").equalsIgnoreCase("true") ? true:false,
                        result.getString("pvp").equalsIgnoreCase("true") ? true:false,
                        result.getString("rush").equalsIgnoreCase("true") ? true:false
                );
                injector.injectMembers(inventory);
                stuffs.addInventory(inventory);
            }
            closeResultSet(result);
        } catch(Exception e) {
            plugin.getLogger().warning("(sql error) : "+e.getMessage());
        }
    }

    private void loadBonus() {
        try {
            ResultSet result = getData("SELECT * FROM bonus;");

            Map<Integer, ArrayList<Bonus>> bonus = new HashMap<>();

            while(result.next()) {
                ArrayList<Item> items = new ArrayList<>();
                for(String item: result.getString("items").split(";"))
                    items.add(stuffs.getItems().get(item));

                ArrayList<PotionEffect> powers = new ArrayList<>();
                for(String item: result.getString("items").split(";")) {
                    String[] split = item.split(",");
                    powers.add(new PotionEffect(
                            PotionEffectType.getByName(split[0]),
                            Utils.convertSecondsToTicks(Integer.parseInt(split[1])),
                            Integer.parseInt(split[2])
                    ));
                }

                Bonus b = new Bonus(
                        items.toArray(new Item[0]),
                        powers.toArray(new PotionEffect[0]),
                        result.getInt("grade"),
                        result.getString("pvp").equalsIgnoreCase("true") ? true:false,
                        result.getString("rush").equalsIgnoreCase("true") ? true:false
                );
                injector.injectMembers(b);

                if(bonus.get(b.getGrade()) != null)
                    bonus.get(b.getGrade()).add(b);
                else
                    bonus.put(b.getGrade(), new ArrayList<Bonus>());
            }

            Map<Integer, Bonus[]> bonusToAdd = new HashMap<>();
            for(Map.Entry<Integer, ArrayList<Bonus>> entry: bonus.entrySet())
                bonusToAdd.put(entry.getKey(), entry.getValue().toArray(new Bonus[0]));

            stuffs.setBonus(bonusToAdd);
            closeResultSet(result);
        } catch(Exception e) {
            plugin.getLogger().warning("(sql error) : "+e.getMessage());
        }
    }
}
