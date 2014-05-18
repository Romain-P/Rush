package fr.rushland.server.objects;

import lombok.Getter;
import org.bukkit.potion.PotionEffect;

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
}
