package fr.rushland.server.objects;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;

public class Item {
    @Getter private String name;
    @Getter private String description;
    @Getter private Enchantment[] enchantments;
    @Getter private int quantity;
    @Getter private int grade;
    @Getter private String command;

    public Item(String name, String description, Enchantment[] enchantments, int quantity, int grade, String command) {
        this.name = name;
        this.description = description;
        this.enchantments = enchantments;
        this.quantity = quantity;
        this.grade = grade;
        this.command = command;
    }
}
