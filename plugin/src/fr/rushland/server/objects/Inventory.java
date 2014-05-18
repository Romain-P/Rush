package fr.rushland.server.objects;

import lombok.Getter;

public class Inventory {
    @Getter private String name;
    @Getter private Item icon;
    @Getter private Item[] items;
    @Getter private boolean main,lobby,pvp,rush;

    public Inventory(String name, Item icon, Item[] items, boolean main, boolean lobby, boolean pvp, boolean rush) {
        this.name = name;
        this.icon = icon;
        this.items = items;
        this.main = main;
        this.lobby = lobby;
        this.pvp = pvp;
        this.rush = rush;
    }
}
