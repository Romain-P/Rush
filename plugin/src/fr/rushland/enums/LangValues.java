package fr.rushland.enums;

import org.bukkit.ChatColor;


public enum LangValues {
    NO_PERM(ChatColor.RED + "Vous n'avez pas la permission pour effectuer cette action!"),
    MUST_BE_IN_GAME(ChatColor.RED + "Vous devez etre dans la partie."),
    PLAYER_ONLY(ChatColor.RED + "Vous devez etre un joueur!"),
    MUST_NOT_BE_STARTED(ChatColor.RED + "La partie a deja commence!"),
    PLAYER_NOT_FOUND(ChatColor.RED + "Joueur non trouve!"),
    VIP_PREFIX(ChatColor.GREEN + "[VIP] " + ChatColor.RESET),
    OP_PREFIX(ChatColor.DARK_RED + "[A] " + ChatColor.RESET),
    M_PREFIX(ChatColor.BLUE + "[M] " + ChatColor.RESET),
    DB_DISABLED(ChatColor.RED + "MySQL doit etre active, contactez l'administrateur!"),
    GAME_TYPE_FAKE(ChatColor.RED + "Ce type de jeu n'existe pas"),
    BAN_PREFIX(ChatColor.RED + "Banni : " + ChatColor.RESET),
    KICKED_VIP(ChatColor.RED + "Vous avez ete expulse par un vip qui a pris votre place"),
    SERVER_FULL(ChatColor.RED + "Le serveur est plein! Deviens VIP pour pouvoir te connecter."),
    MUST_BE_VIP(ChatColor.RED + "Tu dois etre VIP!");

    private String value;
    private LangValues(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }
}
