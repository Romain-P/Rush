package fr.rushland.enums;

import org.bukkit.ChatColor;

import java.io.File;

public enum PluginValues {
    DATA_FOLDER("plugins" + File.separator + "Rush" + File.separator),
    BACKUP_MAPS_DIR_NAME("maps"),
    MAPS_DIR_NAME("temp"),
    GAMES_DIR_NAME("games"),
    YML_ERROR(ChatColor.RED + "Error with yml "),
    BACKUP_MAP_LOCS(DATA_FOLDER.getValue() + BACKUP_MAPS_DIR_NAME.getValue() + File.separator),
    MAP_LOCS(DATA_FOLDER.getValue() + MAPS_DIR_NAME.getValue() + File.separator);

    private String value;
    private PluginValues(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }
}
