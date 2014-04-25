package fr.rushland.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Game 
{
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
	private Plugin plugin;
	private String gameType;
	private List<String> voted = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public Game(String name, String gameType, Plugin plugin, int maxPlayers, String waitMap, String map, List<String> teamNames, 
			List<String> teamPrefixes, List<String> teamColours, List<int[]> waitLocs, List<int[]> locs, Sign sign)
	{
		this.name = name;
		this.plugin = plugin;
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

		mapLoc = Main.MAP_LOCS + map + "_" + gameType + "_" + name;
		waitMapLoc = Main.MAP_LOCS + waitMap + "_" + gameType + "_" + name;
		teams = new ArrayList[teamNames.size()];
		for(int i = 0; i < teamNames.size(); i++)
		{
			teams[i] = new ArrayList<String>();
		}
		setBoard();
		copyMaps();
	}

	private void setBoard()
	{
		board = Main.manager.getNewScoreboard();
		teamsSb = new Team[teamNames.size()];
		for(int i = 0; i < teamNames.size(); i++)
		{
			Team t = board.registerNewTeam(teamNames.get(i));
			t.setPrefix(teamColours.get(i));
			t.setAllowFriendlyFire(false);
			teamsSb[i] = t;
		}
	}

	void copyMaps()
	{
		try 
		{
			FileUtils.copyDirectory(new File(Main.BACKUP_MAP_LOCS + waitMap), new File(waitMapLoc));
			FileUtils.copyDirectory(new File(Main.BACKUP_MAP_LOCS + map), new File(mapLoc));
		} 

		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	String getName()
	{
		return name;
	}

	int getTeamIndex(String teamName)
	{
		return teamNames.indexOf(teamName);
	}

	Boolean isTeam(String teamName)
	{
		for(String team : teamNames)
		{
			if(teamName.equalsIgnoreCase(team))
				return true;
		}

		return false;
	}

	String getTeamNameCleanCase(String teamName)
	{
		for(String team : teamNames)
		{
			if(teamName.equalsIgnoreCase(team))
				return team;
		}

		return null;
	}

	int getTeamSize(int teamIndex)
	{
		return teams[teamIndex].size();
	}

	int getTeamNums()
	{
		return teamNames.size();
	}

	int getMaxPlayers()
	{
		return maxPlayers;
	}

	String getGameType()
	{
		return gameType;
	}

	Sign getSign()
	{
		return sign;
	}

	boolean isStarted()
	{
		return started;
	}

	List<String> getTeams()
	{
		return teamNames;
	}

	void join(Player player)
	{
		boolean randomFill = true;
		for(int i = 0; i < teams.length; i++)
		{
			if(teams[i].size() >= maxPlayers/teams.length)
			{
				randomFill = false;	
				break;
			}
		}

		if(randomFill)
		{
			int teamIndex = Utils.randInt(0, teams.length-1);
			join(player, teamIndex);
		}

		else
		{
			for(int i = 0; i < teams.length; i++)
			{
				if(teams[i].size() < maxPlayers/teams.length)
				{
					join(player, i);
					break;
				}
			}
		}
	}

	void join(Player player, int teamIndex)
	{
		String playerName = player.getName();
		teams[teamIndex].add(playerName);
		sign.setLine(Main.PLAYERS_SIGN_LINE, getPlayersNum() + "/" + maxPlayers);
		sign.update();
		teamsSb[teamIndex].addPlayer(player);
		player.setDisplayName(teamPrefixes.get(teamIndex) + player.getDisplayName());
		Bukkit.getServer().createWorld(new WorldCreator(waitMapLoc));
		World w = Bukkit.getServer().getWorld(waitMapLoc);
		player.teleport(new Location(w, waitLocs.get(teamIndex)[0], waitLocs.get(teamIndex)[1], waitLocs.get(teamIndex)[2]));
		Utils.goNaked(player);
		for(Player p : player.getWorld().getPlayers())
		{
			p.setScoreboard(board);
		}

		msgGame(ChatColor.YELLOW + playerName + " has joined team " + teamNames.get(teamIndex));

		if(getPlayersNum() == maxPlayers)
			start();

	}

	void start()
	{
		Bukkit.getServer().createWorld(new WorldCreator(mapLoc));
		World w = Bukkit.getServer().getWorld(mapLoc);
		for(int i = 0; i < teams.length; i++)
		{
			for(String playerName : teams[i])
			{
				Player player = Bukkit.getServer().getPlayer(playerName);
				player.teleport(new Location(w, locs.get(i)[0], locs.get(i)[1], locs.get(i)[2]));
				Utils.goNaked(player);
				w.setTime(14000);
			}
		}

		for(Player p : Bukkit.getServer().getWorld(waitMapLoc).getPlayers())
		{
			p.teleport(new Location(w, locs.get(Utils.randInt(0, teams.length))[0], locs.get(Utils.randInt(0, teams.length))[1], locs.get(Utils.randInt(0, teams.length))[2]));
		}

		Bukkit.getServer().unloadWorld(waitMapLoc, true);

		started = true;
		sign.setLine(Main.STATUS_SIGN_LINE, Main.STARTED_SIGN_MSG);
		sign.update();

		msgGame(ChatColor.GREEN + "The game is starting! Good luck!");
	}

	void reset()
	{
		for(int i = 0; i < teams.length; i++)
		{   
			for(int x = 0; x < teams[i].size(); x++)
			{
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
		{
			player.teleport(l);
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
		{
			public void run() 
			{
				resetMaps();
			}
		}, 20 * 1L);
	}

	void resetMaps()
	{
		if(Bukkit.getServer().unloadWorld(mapLoc, true))
		{
			try
			{
				FileUtils.deleteDirectory(new File(waitMapLoc));
				FileUtils.deleteDirectory(new File(mapLoc));
			}

			catch (IOException e) 
			{
				e.printStackTrace();
			}

			copyMaps();

			started = false;
			sign.setLine(Main.PLAYERS_SIGN_LINE, "0/" + maxPlayers);
			sign.setLine(Main.STATUS_SIGN_LINE, Main.WAITING_SIGN_MSG);
			sign.getBlock().getChunk().load();
			sign.update();
		}

		else
		{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
			{
				public void run() 
				{
					resetMaps();
				}
			}, 20 * 10L);
		}
	}


	void removeActions(Player player)
	{
		String playerName = player.getName();
		int teamIndex = getPlayerTeam(playerName);
		player.setDisplayName(player.getDisplayName().replace(teamPrefixes.get(teamIndex), ""));
		teams[teamIndex].remove(playerName);
		voted.remove(playerName);
		teamsSb[teamIndex].removePlayer(player);
		player.setScoreboard(Main.manager.getNewScoreboard());
		sign.setLine(Main.PLAYERS_SIGN_LINE, getPlayersNum() + "/" + maxPlayers);
		sign.update();
	}

	void remove(Player player)
	{
		Utils.goNaked(player);
		Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
		player.teleport(l);
		String playerName = player.getName();
		removeActions(player);
		if(started)
		{
			msgGame(ChatColor.RED + playerName + " lost!");
			player.sendMessage(ChatColor.RED + "You lost!");

			int teamsLeft = teamsLeft();
			if(teamsLeft <= 1)
			{
				if(teamsLeft == 1)
				{
					String winPlayerList = "";
					int lastTeamIndex = lastTeamIndex();
					if(lastTeamIndex() != -1)
					{
						for(int i = 0; i < teams[lastTeamIndex].size(); i++)
						{
							String winPlayerName = teams[lastTeamIndex].get(i);
							winPlayerList += winPlayerName;
							if(i != teams[lastTeamIndex].size()-1)
								winPlayerList += ", ";
						}
					}
					Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + winPlayerList + " won the " + gameType + " " + name + "!");
				}

				else
				{
					Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "The " + gameType + " " + name + " ended with no winners!");
				}

				reset();
			}
		}

		else
		{
			msgGame(ChatColor.GREEN + playerName + " left the game");
		}
	}

	int teamsLeft()
	{
		int teamsLeft = 0;
		for(int i = 0; i < teams.length; i++)
		{	
			if(teams[i].size() > 0)
				teamsLeft++;
		}

		return teamsLeft;
	}

	int lastTeamIndex()
	{
		int teamIndex = -1;
		for(int i = 0; i < teams.length; i++)
		{
			if(teams[i].size() > 0)
			{
				teamIndex = i;
			}
		}

		if(teamsLeft() == 1)
			return teamIndex;

		return -1;
	}

	int getPlayerTeam(String playerName)
	{
		for(int i = 0; i < teams.length; i++)
		{
			if(teams[i].contains(playerName))
			{
				return i;
			}
		}

		return -1;
	}

	int getPlayersNum()
	{
		int size = 0;
		for(int i = 0; i < teams.length; i++)
		{
			size += teams[i].size();
		}
		return size;
	}

	void msgTeam(String message, int teamIndex)
	{
		for(String playerName: teams[teamIndex])
		{
			Player player = Bukkit.getServer().getPlayer(playerName);
			player.sendMessage(message);
		}
	}

	void msgGame(String message)
	{
		World world;
		if(started)
			world = Bukkit.getWorld(this.mapLoc);

		else
			world = Bukkit.getWorld(waitMapLoc);

		Utils.msgWorld(world.getName(), message);
	}

	void vote(String playerName)
	{
		msgGame(ChatColor.YELLOW + playerName + " has voted to start the game");
		voted.add(playerName);
		if(voted.size() == getPlayersNum() && teamsLeft() >= teamNames.size()/2 && teamsLeft() > 1)
			start();
	}

	String getTeamColour(int teamIndex)
	{
		return teamColours.get(teamIndex);
	}

	boolean hasVoted(String playerName)
	{
		return voted.contains(playerName);
	}
}
