package fr.rushland.server.objects;

import com.google.inject.Inject;
import fr.rushland.server.ServerStuff;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Map;

public class Inventory {
    @Getter private String name;
    @Getter private Item icon;
    @Getter private Map<String, Item> items;
    @Getter private boolean main,lobby,pvp,rush;
    @Getter private org.bukkit.inventory.Inventory object;

    @Inject ServerStuff serverStuff;

    public Inventory(String name, Item icon, Map<String, Item> items, boolean main, boolean lobby, boolean pvp, boolean rush) {
        this.name = name;
        this.icon = icon;
        this.items = items;
        this.main = main;
        this.lobby = lobby;
        this.pvp = pvp;
        this.rush = rush;
    }

    public org.bukkit.inventory.Inventory toObject() {
        org.bukkit.inventory.Inventory object = this.object;
        if(object == null) {
            this.object = Bukkit.createInventory(null, 9 * ((this.items.size()%9)+1), this.name);

            for(Item item: this.items.values())
                this.object.addItem(item.toObject());
        }
        return object;
    }
}
