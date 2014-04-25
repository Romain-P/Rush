package fr.rushland.enums;

import org.bukkit.ChatColor;

public enum SignValues {
    STARTED_SIGN_MSG(ChatColor.DARK_GRAY + "Jeu en cours"),
    WAITING_SIGN_MSG(ChatColor.DARK_BLUE + "En Attente"),
    SIGN_TITLE_COLOUR(ChatColor.DARK_RED),

    DEFAULT_GAME_SIZE(8),
    TITLE_SIGN_LINE(0),
    STATUS_SIGN_LINE(1),
    PLAYERS_SIGN_LINE(2),
    KEY_SIGN_LINE(3);

    private int value;
    private String string;
    private ChatColor color;
    private SignValues(int value) {
        this.value = value;
    }
    private SignValues(String string) {
        this.string = string;
    }
    private SignValues(ChatColor color) {
        this.color = color;
    }
    public int getValue() {
        return this.value;
    }

    public String toString() {
        return this.string;
    }
    public ChatColor getColor() {
        return this.color;
    }
}
