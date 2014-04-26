package fr.rushland.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import fr.rushland.core.*;
import fr.rushland.database.Database;
import fr.rushland.enums.LangValues;
import fr.rushland.enums.PluginValues;
import fr.rushland.server.commands.GameCommandExecutor;
import fr.rushland.server.commands.ServerCommandExecutor;
import fr.rushland.server.games.Game;
import fr.rushland.server.games.GameType;
import fr.rushland.server.objects.VipPlayer;
import fr.rushland.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    @Getter private List<GameType> gameTypes;
    @Getter private List<Game> games;
    @Getter private Map<String, VipPlayer> vips;

    @Inject JavaPlugin plugin;
    @Inject Set<Listener> listeners;
    @Inject Config config;
    @Inject Injector injector;
    @Inject ServerStuff serverStuff;
    @Inject Database database;

    public Server() {
        this.gameTypes = new ArrayList<>();
        this.games = new ArrayList<>();
        this.vips = new ConcurrentHashMap<>();
    }

    public void initialize() {
        plugin.getLogger().info("registering event listeners..");
        for(Listener listener: listeners)
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);

        if(config.isMainServer())
            serverStuff.initializeStuff();
        serverStuff.intializeItems();

        plugin.getLogger().info("loading games..");
        loadGames();

        plugin.getLogger().info("attributing executors to games/server commands..");
        attributeExecutors();
    }

    public void attributeExecutors() {
        String[] gameCommands = {"start", "join", "leave", "vote", "t"};
        for(String command: gameCommands) {
            GameCommandExecutor executor = new GameCommandExecutor();
            injector.injectMembers(executor);
            plugin.getCommand(command).setExecutor(executor);
        }

        String[] serverCommands = {"vip", "ban", "pardon", "connect", "lobby", "stuff"};
        for(String command: serverCommands) {
            ServerCommandExecutor executor = new ServerCommandExecutor();
            injector.injectMembers(executor);
            plugin.getCommand(command).setExecutor(executor);
        }
    }

    public void loadGames() {
        File dataDir = new File(PluginValues.DATA_FOLDER.getValue());
        if(!dataDir.exists())
            dataDir.mkdir();

        File backupMapsDir = new File(PluginValues.BACKUP_MAP_LOCS.getValue());
        if(!backupMapsDir.exists())
            backupMapsDir.mkdir();

        File mapsDir = new File(PluginValues.MAP_LOCS.getValue());
        if(!mapsDir.exists())
            mapsDir.mkdir();

        File gamesDir = new File(PluginValues.DATA_FOLDER.getValue() + PluginValues.GAMES_DIR_NAME.getValue());
        if(!gamesDir.exists())
            gamesDir.mkdir();

        for (File file : gamesDir.listFiles()) {
            String fileName = file.getName();
            String extension = "";

            int i = fileName.lastIndexOf('.');
            int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

            if (i > p)
                extension = fileName.substring(i+1);

            if(file.isFile() && extension.equalsIgnoreCase("yml")) {
                plugin.getLogger().info("Trying to load " + fileName);
                FileConfiguration gameConfig = YamlConfiguration.loadConfiguration(file);
                String name = gameConfig.getString("name");
                String errorSize = " size is not equal to numTeams";
                int numTeams = gameConfig.getInt("numTeams");
                List<String> teamNames = gameConfig.getStringList("teamNames");
                List<String> teamPrefixes = gameConfig.getStringList("teamPrefixes");
                List<String> teamColours = gameConfig.getStringList("teamColours");

                List<String> teamColoursColour = new ArrayList<>();
                for(String teamPrefix : teamColours)
                    teamColoursColour.add(ChatColor.translateAlternateColorCodes('&', teamPrefix));


                teamColours.clear();
                teamColours.addAll(teamColoursColour);

                List<String> teamPrefixesColour = new ArrayList<>();
                for(String teamChatPrefixe : teamPrefixes)
                    teamPrefixesColour.add(ChatColor.translateAlternateColorCodes('&', teamChatPrefixe));


                teamPrefixes.clear();
                teamPrefixes.addAll(teamPrefixesColour);

                List<String> waitLocs = gameConfig.getStringList("coordinates.waitingArea");
                List<String> locs = gameConfig.getStringList("coordinates.map");
                String waitMap = gameConfig.getString("waitingAreaMap");
                String map = gameConfig.getString("map");
                String errorFileNotExists = " does not exist";

                boolean errorYml = false;
                if(name.length() > 14 || name.length() == 0) {
                    plugin.getLogger().info(PluginValues.YML_ERROR.getValue() + fileName + ": name must have a length of > 0 && < 15");
                    errorYml = true;
                }

                if(teamNames.size() != numTeams) {
                    plugin.getLogger().info(PluginValues.YML_ERROR.getValue() + fileName + ": teamNames" + errorSize);
                    errorYml = true;
                }

                if(teamPrefixes.size() != numTeams) {
                    plugin.getLogger().info(PluginValues.YML_ERROR.getValue() + fileName + ": teamPrefixes" + errorSize);
                    errorYml = true;
                }


                if(teamColours.size() != numTeams) {
                    plugin.getLogger().info(PluginValues.YML_ERROR.getValue() + fileName + ": teamColours" + errorSize);
                    errorYml = true;
                }

                if(waitLocs.size() != numTeams) {
                    plugin.getLogger().info(PluginValues.YML_ERROR.getValue() + fileName + ": coordinates.waitingArea" + errorSize);
                    errorYml = true;
                }

                if(locs.size() != numTeams) {
                    plugin.getLogger().info(PluginValues.YML_ERROR.getValue() + fileName + ": coordinates.map" + errorSize);
                    errorYml = true;
                }

                if(!new File(PluginValues.BACKUP_MAP_LOCS.getValue() + map).exists()) {
                    plugin.getLogger().info(PluginValues.YML_ERROR.getValue() + fileName + ": " + map + errorFileNotExists);
                    errorYml = true;
                }

                if(!new File(PluginValues.BACKUP_MAP_LOCS.getValue() + waitMap).exists()) {
                    plugin.getLogger().info(PluginValues.YML_ERROR.getValue() + fileName + ": " + waitMap + errorFileNotExists);
                    errorYml = true;
                }

                for(GameType gameType: gameTypes) {
                    if(gameType.name.equals(name)) {
                        plugin.getLogger().info(PluginValues.YML_ERROR.getValue() + fileName + ": There is already a games with this name " + name + "!");
                        errorYml = true;
                        break;
                    }
                }

                if(!errorYml) {
                    plugin.getLogger().info("No error detected with " + fileName);
                    List<int[]> waitLocsArrInt = new ArrayList<>();
                    List<int[]> locsArrInt = new ArrayList<>();

                    for(String loc : waitLocs)
                        waitLocsArrInt.add(Utils.strArrayToInt(loc.split(" ")));


                    for(String loc : locs)
                        locsArrInt.add(Utils.strArrayToInt(loc.split(" ")));

                    gameTypes.add(new GameType(name, waitMap, map, teamNames,
                            teamPrefixes, teamColours, waitLocsArrInt, locsArrInt));
                }
            }
        }
    }

    public void attachPrefix(Player player) {
        if (player != null) {
            if (player.isOp())
                player.setDisplayName(LangValues.OP_PREFIX.getValue() + player.getDisplayName());
            else if (player.hasPermission("rushy2.mPrefix"))
                player.setDisplayName(LangValues.M_PREFIX.getValue() + player.getDisplayName());
            else if (player.getDisplayName().contains("javadevelopper")) //moi aussi jveux mon title :D
                player.setDisplayName(LangValues.DEV_PREFIX.getValue() + player.getDisplayName());
            else if (database.isEnabled())
                if (vips.containsKey(player.getName()))
                    player.setDisplayName(LangValues.VIP_PREFIX.getValue() + player.getDisplayName());
        }
    }

    public void addVips(String name, int grade) {
        Player player = Bukkit.getPlayer(name);
        if(player != null) {
            vips.put(name, new VipPlayer(name, grade));
        }
    }

    public void removeVips(String name) {
        vips.remove(name);
        Player player = Bukkit.getPlayer(name);
        if(player != null) {
            player.setDisplayName(player.getDisplayName().replace(LangValues.VIP_PREFIX.getValue(), ""));
        }
    }

    public GameType getGameType(String name) {
        for(GameType gameType : gameTypes)
            if(name.equalsIgnoreCase(gameType.name))
                return gameType;
        return null;
    }

    public Game getGame(String name, String gameType) {
        for(Game game : games)
            if(name.equalsIgnoreCase(game.getName()) && gameType.equalsIgnoreCase(game.getGameType()))
                return game;
        return null;
    }

    public Game getPlayerGame(String name) {
        for(Game game : games)
            if(game.getPlayerTeam(name) != -1)
                return game;
        return null;
    }
}
