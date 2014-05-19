package fr.rushland.server.objects;

import com.google.inject.Inject;
import fr.rushland.server.ServerStuff;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Item {
    @Getter private String name;
    @Getter private String description;
    @Getter private String material;
    @Getter private Map<Enchantment, Integer> enchantments;
    @Getter private int quantity;
    @Getter private int grade;
    @Getter private String command;
    @Getter private Item[] givenItems;
    @Getter private ItemStack object;

    @Inject ServerStuff serverStuff;

    public Item(String name, String description, String material, Map<Enchantment, Integer> enchantments, int quantity, int grade, String command, Item[] givenItems) {
        this.name = name;
        this.description = description;
        this.material = material;
        this.enchantments = enchantments;
        this.quantity = quantity;
        this.grade = grade;
        this.command = command;
        this.givenItems = givenItems;
    }

    public ItemStack toObject() {
        ItemStack item = this.object;
        if(item == null) {
            this.object = serverStuff.createCustomItem(
                    Material.getMaterial(this.name),
                    this.quantity,
                    this.name,
                    new String[] {this.description, this.grade == 0 ? "NON VIP":"VIP "+this.grade},
                    enchantments
            );
        }
        return item;
    }
}
