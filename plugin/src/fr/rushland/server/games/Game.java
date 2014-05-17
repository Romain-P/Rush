package fr.rushland.server.games;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import fr.rushland.server.Server;
import fr.rushland.server.ServerStuff;
import fr.rushland.utils.Utils;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.rushland.enums.PluginValues;
import fr.rushland.enums.SignValues;

public class Game {
	private String name;
	private List<String> teamNames;
	private List<String> teamPrefixes;
	private List<String> teamColours;
	private List<int[]> waitLocs;
	private List<int[]> locs;
	private List<String>[] teams;
	private Sign sign;
	private Scoreboard board;
	private Team[] teamsSb;
	private String mapLoc;
	private String waitMapLoc;
	private int maxPlayers;
	private boolean started = false;
	private String waitMap;
	private String map;
	private String gameType;
	private List<String> voted = new ArrayList<String>();
    @Getter private boolean vip;

    @Inject JavaPlugin plugin;
    @Inject Server server;
    @Inject ServerStuff serverStuff;

	@SuppressWarnings("unchecked")
	public Game(String name, String gameType, int maxPlayers, String waitMap, String map, List<String> teamNames,
			List<String> teamPrefixes, List<String> teamColours, List<int[]> waitLocs, List<int[]> locs, Sign sign, boolean vip) {
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.teamNames = teamNames;
		this.teamPrefixes = teamPrefixes;
		this.teamColours = teamColours;
		this.waitLocs = waitLocs;
		this.locs = locs;
		this.waitMap = waitMap;
		this.map = map;
		this.sign = sign;
		this.gameType = gameType;
        this.vip = vip;

		this.mapLoc = PluginValues.MAP_LOCS.getValue() + map + "_" + gameType + "_" + name;
		this.waitMapLoc = PluginValues.MAP_LOCS.getValue() + waitMap + "_" + gameType + "_" + name;
		this.teams = new ArrayList[teamNames.size()];

		for(int i = 0; i < teamNames.size(); i++)
			teams[i] = new ArrayList<>();

		setBoard();
		copyMaps();
	}

	private void setBoard() {
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		teamsSb = new Team[teamNames.size()];

		for(int i = 0; i < teamNames.size(); i++) {
			Team t = board.registerNewTeam(teamNames.get(i));
			t.setPrefix(teamColours.get(i));
			t.setAllowFriendlyFire(false);
			teamsSb[i] = t;
		}
	}

	public void copyMaps() {
		try {
			FileUtils.copyDirectory(new File(PluginValues.BACKUP_MAP_LOCS.getValue() + waitMap), new File(waitMapLoc));
			FileUtils.copyDirectory(new File(PluginValues.BACKUP_MAP_LOCS.getValue() + map), new File(mapLoc));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public String getName() {
		return name;
	}

    public int getTeamIndex(String teamName) {
		return teamNames.indexOf(teamName);
	}

    public Boolean isTeam(String teamName) {
		for(String team : teamNames)
			if(teamName.equalsIgnoreCase(team))
				return true;
		return false;
	}

    public String getTeamNameCleanCase(String teamName) {
		for(String team : teamNames)
			if(teamName.equalsIgnoreCase(team))
				return team;
		return null;
	}

	public int getTeamSize(int teamIndex) {
		return teams[teamIndex].size();
	}

	public int getTeamNums() {
		return teamNames.size();
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public String getGameType() {
		return gameType;
	}

	public Sign getSign() {
		return sign;
	}

	public boolean isStarted() {
		return started;
	}

	public List<String> getTeams() {
		return teamNames;
	}

	public void join(Player player) {
		boolean randomFill = true;
		for(int i = 0; i < teams.length; i++) {
			if(teams[i].size() >= maxPlayers/teams.length) {
				randomFill = false;	
				break;
			}
		}

		if(randomFill) {
			int teamIndex = Utils.randInt(0, teams.length - 1);
			join(player, teamIndex);
		} else {
			for(int i = 0; i < teams.length; i++) {
				if(teams[i].size() < maxPlayers/teams.length) {
					join(player, i);
					break;
				}
			}
		}
	}

	public void join(Player player, int teamIndex) {
		String playerName = player.getName();
		teams[teamIndex].add(playerName);
		sign.setLine(SignValues.PLAYERS_SIGN_LINE.getValue(), getPlayersNum() + "/" + maxPlayers);
		sign.update();
		teamsSb[teamIndex].addPlayer(player);
		player.setDisplayName(teamPrefixes.get(teamIndex) + player.getDisplayName());
		Bukkit.getServer().createWorld(new WorldCreator(waitMapLoc));
		World w = Bukkit.getServer().getWorld(waitMapLoc);
		player.teleport(new Location(w, waitLocs.get(teamIndex)[0], waitLocs.get(teamIndex)[1], waitLocs.get(teamIndex)[2]));
		Utils.goNaked(player);

		for(Player p : player.getWorld().getPlayers())
			p.setScoreboard(board);

		msgGame(ChatColor.YELLOW + playerName + " a rejoind la team " + teamNames.get(teamIndex));

		if(getPlayersNum() == maxPlayers)
			start();
	}

	public void start() {
		Bukkit.getServer().createWorld(new WorldCreator(mapLoc));
		World w = Bukkit.getServer().getWorld(mapLoc);
		for(int i = 0; i < teams.length; i++) {
			for(String playerName : teams[i]) {
				Player player = Bukkit.getServer().getPlayer(playerName);
				player.teleport(new Location(w, locs.get(i)[0], locs.get(i)[1], locs.get(i)[2]));
				Utils.goNaked(player);
                serverStuff.giveVipBonus(player);
				w.setTime(14000);
			}
		}

		for(Player p : Bukkit.getServer().getWorld(waitMapLoc).getPlayers())
			p.teleport(new Location(w, locs.get(Utils.randInt(0, teams.length))[0],
                    locs.get(Utils.randInt(0, teams.length))[1], locs.get(Utils.randInt(0, teams.length))[2]));


		Bukkit.getServer().unloadWorld(waitMapLoc, true);

		started = true;
		sign.setLine(SignValues.STATUS_SIGN_LINE.getValue(), SignValues.STARTED_SIGN_MSG.toString());
		sign.update();

		msgGame(ChatColor.GREEN + "Le jeu commence! Bonne chance..!");
	}

	public void reset() {
		for(int i = 0; i < teams.length; i++) {
			for(int x = 0; x < teams[i].size(); x++) {
				String playerName = teams[i].get(x);
				final Player player = Bukkit.getServer().getPlayer(playerName);
				removeActions(player);
				Utils.goNaked(player);
				Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
				player.teleport(l);
				x--;
			}
		}

		Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();

		for(Player player : Bukkit.getServer().getWorld(mapLoc).getPlayers())
			player.teleport(l);


		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                resetMaps();
            }
		}, 20 * 1L);
	}

	public void resetMaps() {
		if(Bukkit.getServer().unloadWorld(mapLoc, true)) {
			try {
				FileUtils.deleteDirectory(new File(waitMapLoc));
				FileUtils.deleteDirectory(new File(mapLoc));
			} catch (IOException e) {
				e.printStackTrace();
			}

			copyMaps();

			started = false;
			sign.setLine(SignValues.PLAYERS_SIGN_LINE.getValue(), "0/" + maxPlayers);
			sign.setLine(SignValues.STATUS_SIGN_LINE.getValue(), SignValues.WAITING_SIGN_MSG.toString());
			sign.getBlock().getChunk().load();
			sign.update();
		} else
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    resetMaps();
                }
            }, 20 * 10L);
	}


	public void removeActions(Player player) {
		String playerName = player.getName();
		int teamIndex = getPlayerTeam(playerName);
		player.setDisplayName(player.getDisplayName().replace(teamPrefixes.get(teamIndex), ""));
		teams[teamIndex].remove(playerName);
		voted.remove(playerName);
		teamsSb[teamIndex].removePlayer(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		sign.setLine(SignValues.PLAYERS_SIGN_LINE.getValue(), getPlayersNum() + "/" + maxPlayers);
		sign.update();
	}

	public void remove(Player player) {
		Utils.goNaked(player);
		Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
		player.teleport(l);
		String playerName = player.getName();
		removeActions(player);

		if(started) {
			msgGame(ChatColor.RED + playerName + " a perdu!");
			player.sendMessage(ChatColor.RED + "Tu as perdu!");

			int teamsLeft = teamsLeft();
			if(teamsLeft <= 1) {
				if(teamsLeft == 1) {
					String winPlayerList = "";
					int lastTeamIndex = lastTeamIndex();

					if(lastTeamIndex() != -1) {
						for(int i = 0; i < teams[lastTeamIndex].size(); i++) {
							String winPlayerName = teams[lastTeamIndex].get(i);
							winPlayerList += winPlayerName;
							if(i != teams[lastTeamIndex].size()-1)
								winPlayerList += ", ";

                            Player w = Bukkit.getServer().getPlayer(winPlayerName);
                            if(w != null) {
                                server.getPlayer(winPlayerName).addWin(10);
                                serverStuff.giveStartingItems(w);
                            }
						}
					}
					Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + winPlayerList + " ont/a gagne le " + gameType + " " + name + "!");
				} else
					Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "Le " + gameType + " " + name + " s'est termine sans aucun vainqueur!");

				reset();
			}
		} else
			msgGame(ChatColor.GREEN + playerName + " a quitte la partie");
        if(player != null) {
            server.getPlayer(player.getName()).addLose(1);
            serverStuff.giveStartingItems(player);
        }
	}

    public int teamsLeft() {
		int teamsLeft = 0;
		for(int i = 0; i < teams.length; i++)
			if(teams[i].size() > 0)
				teamsLeft++;
		return teamsLeft;
	}

    public int lastTeamIndex() {
		int teamIndex = -1;
		for(int i = 0; i < teams.length; i++)
			if(teams[i].size() > 0)
				teamIndex = i;

		if(teamsLeft() == 1)
			return teamIndex;

		return -1;
	}

    public int getPlayerTeam(String playerName) {
		for(int i = 0; i < teams.length; i++)
			if(teams[i].contains(playerName))
				return i;
		return -1;
	}

    public int getPlayersNum() {
		int size = 0;
		for(int i = 0; i < teams.length; i++)
			size += teams[i].size();
		return size;
	}

	public void msgTeam(String message, int teamIndex) {
		for(String playerName: teams[teamIndex]) {
			Player player = Bukkit.getServer().getPlayer(playerName);
			player.sendMessage(message);
		}
	}

	public void msgGame(String message) {
		World world;
		if(started)
			world = Bukkit.getWorld(this.mapLoc);
		else
			world = Bukkit.getWorld(waitMapLoc);

		Utils.msgWorld(world.getName(), message);
	}

	public void vote(String playerName) {
		msgGame(ChatColor.YELLOW + playerName + " a vote pour demarrer le jeu.");
		voted.add(playerName);

		if(voted.size() == getPlayersNum() && teamsLeft() >= teamNames.size()/2 && teamsLeft() > 1)
			start();
	}

    public String getTeamColour(int teamIndex) {
		return teamColours.get(teamIndex);
	}

	public boolean hasVoted(String playerName) {
		return voted.contains(playerName);
	}
}
