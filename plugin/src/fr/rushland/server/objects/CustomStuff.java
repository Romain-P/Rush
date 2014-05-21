package fr.rushland.server.objects;
import com.google.inject.Inject;
import fr.rushland.database.data.StuffManager;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class CustomStuff {
    @Getter private Map<String, Item> items;
    @Getter private Map<String, Inventory> inventories;
    @Getter @Setter private Map<Integer, Bonus[]> bonus;

    @Inject StuffManager manager;

    public CustomStuff() {
        this.items = new HashMap<>();
        this.inventories = new HashMap<>();
    }

    public void install() {
        manager.loadData();
    }

    public void addItem(Item item) {
        System.out.println("Added ITEM "+item.getName());
        this.items.put(item.getName(), item);
    }

    public void addInventory(Inventory inventory) {
        this.inventories.put(inventory.getName(), inventory);
    }
}
