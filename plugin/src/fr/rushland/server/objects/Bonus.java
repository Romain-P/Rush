package fr.rushland.server.objects;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

public class Bonus {
    @Getter private Item[] items;
    @Getter private PotionEffect[] powers;
    @Getter private int grade;
    @Getter private boolean pvp;
    @Getter private boolean rush;

    public Bonus(Item[] items, PotionEffect[] powers, int grade, boolean pvp, boolean rush) {
        this.items = items;
        this.powers = powers;
        this.grade = grade;
        this.pvp = pvp;
        this.rush = rush;
    }

    public void giveBonus(ClientPlayer player) {
        if(player.getGrade() < this.grade) return;

        Player entity = Bukkit.getServer().getPlayer(player.getName());


        for(Item item: this.items) {
            String type = item.toObject().getType().name().toLowerCase().split("_")[1];

            switch(type) {
                case "chestplate":
                    entity.getInventory().setChestplate(item.toObject());
                    break;
                case "helmet":
                    entity.getInventory().setHelmet(item.toObject());
                    break;
                case "leggings":
                    entity.getInventory().setLeggings(item.toObject());
                    break;
                case "boots":
                    entity.getInventory().setBoots(item.toObject());
                    break;
                default:
                    entity.getInventory().addItem(item.toObject());
                    break;
            }
        }

        entity.addPotionEffects(Arrays.asList(powers));
    }
}
