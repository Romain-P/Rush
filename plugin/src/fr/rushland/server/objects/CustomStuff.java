package fr.rushland.server.objects;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class CustomStuff {
    @Getter private Map<String, Item> items;
    @Getter private Map<String, Inventory> inventories;
    @Getter @Setter private Map<Integer, Bonus[]> bonus;

    public CustomStuff() {
        this.items = new HashMap<>();
        this.inventories = new HashMap<>();
    }

    public void install() {

    }

    public void addItem(Item item) {
        this.items.put(item.getName(), item);
    }

    public void addInventory(Inventory inventory) {
        this.inventories.put(inventory.getName(), inventory);
    }
}
