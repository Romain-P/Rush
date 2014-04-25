package fr.rushland.core;

import fr.rushland.enums.LangValues;
import fr.rushland.enums.PluginValues;
import fr.rushland.enums.SignValues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class GamePlayerListener implements Listener
{
	Plugin plugin;

	public GamePlayerListener(JavaPlugin plugin) 
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void OnPlayerDeath(PlayerDeathEvent event)
	{
		final Player player = event.getEntity();
		final String name = player.getName();
		if (player instanceof Player)
		{
			final Game game = Main.getPlayerGame(name);

			if(game != null)
			{
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
				{
					public void run()
					{
						if((game.isStarted() && player.getBedSpawnLocation() == null) || !game.isStarted())
						{
							game.remove(player);
						}
					}
				}, 1L);
			}
		}
	}

	@EventHandler
	public void OnPlayerRespawn(PlayerRespawnEvent event)
	{
		final Player player = event.getPlayer();
		final String name = player.getName();
		final Game game = Main.getPlayerGame(name);

		if(game != null)
		{
			if(player.getBedSpawnLocation() == null || !game.isStarted())
			{
				if((game.isStarted() && player.getBedSpawnLocation() == null) || !game.isStarted())
				{
					game.remove(player);
				}
			}
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) 
	{
		if(event.getLocation().getWorld().getName().contains(PluginValues.MAP_LOCS.getValue()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		String name = player.getName();

		final Game game = Main.getPlayerGame(name);
		if(game != null)
		{
			game.remove(player);
		}

		else if(player.getWorld().getName().contains(PluginValues.MAP_LOCS.getValue()))
		{
			Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
			player.teleport(l);
		}
	}

	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent event)
	{
		if(event.getDamager() instanceof Player)
		{
			Player damager = (Player) event.getDamager();
			String damagerName = damager.getName();
			Game game = Main.getPlayerGame(damagerName);

			if(game != null)
			{
				if(!game.isStarted())
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event) 
	{
		Player player = event.getPlayer();
		String name = player.getName();
		Game game = Main.getPlayerGame(name);

		if(game != null)
		{
			if (player.getBedSpawnLocation() != null && game.isStarted())
			{
				player.sendMessage(ChatColor.RED + "You have already slept!");
				event.setCancelled(true); 
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) 
	{
		Player player = event.getPlayer();
		String name = player.getName();
		Block block = event.getBlock();
		if(!player.isOp())
		{
			Game game = Main.getPlayerGame(name);

			if(game != null)
			{
				if(game.isStarted())
				{
					if(block.getType() == Material.OBSIDIAN)
					{
						event.setCancelled(true); 
					}
				}

				else
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) 
	{
		Player player = event.getPlayer();
		String name = player.getName();
		Block block = event.getBlock();
		if(!player.isOp())
		{
			Game game = Main.getPlayerGame(name);

			if(game != null)
			{
				if (game.isStarted())
				{
					if(block.getType() == Material.BRICK)
					{
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You can't craft in rush.");
					}
				}

				else
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Block block = event.getClickedBlock();
			if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
			{
				Sign sign = (Sign) block.getState();
				String gameKey = sign.getLine(SignValues.KEY_SIGN_LINE.getValue());
				String[] partsPlayerLine = sign.getLine(SignValues.PLAYERS_SIGN_LINE.getValue()).split("/");
				int maxPlayersNum = 0;
				if(partsPlayerLine.length == 2 && !partsPlayerLine[1].equals(""))
					maxPlayersNum = Integer.parseInt(partsPlayerLine[1]);

				if(maxPlayersNum < 2)
					maxPlayersNum = SignValues.DEFAULT_GAME_SIZE.getValue();
				if(sign.getLine(SignValues.TITLE_SIGN_LINE.getValue()).startsWith(SignValues.SIGN_TITLE_COLOUR.getColor() + "[") && sign.getLine(SignValues.TITLE_SIGN_LINE.getValue()).endsWith("]"))
				{
					String gameName = sign.getLine(SignValues.TITLE_SIGN_LINE.getValue()).substring(1 + SignValues.SIGN_TITLE_COLOUR.getColor().toString().length(), sign.getLine(SignValues.TITLE_SIGN_LINE.getValue()).length()-1);
					Game game = Main.getGame(gameKey, gameName);

					if(game == null)
					{
						GameType gameType = Main.getGameType(gameName);

						if(gameType == null)
						{
							player.sendMessage(LangValues.GAME_TYPE_FAKE.getValue());
							return;
						}

						sign.setLine(SignValues.PLAYERS_SIGN_LINE.getValue(), "0/" + maxPlayersNum);
						sign.setLine(SignValues.STATUS_SIGN_LINE.getValue(), SignValues.WAITING_SIGN_MSG.toString());
						sign.update(true);
						Main.games.add(new Game(gameKey, gameType.name, plugin, maxPlayersNum, gameType.waitMap, gameType.map, gameType.teamNames, 
								gameType.teamPrefixes, gameType.teamColours, gameType.waitLocs, gameType.locs, sign));
						game = Main.getGame(gameKey, gameName);
					}

					if(!game.isStarted())
					{
						if(game.getMaxPlayers() != game.getPlayersNum())
							game.join(player);
						else
							player.sendMessage(ChatColor.RED + "The game is full!");	
					}

					else
					{
						player.sendMessage(ChatColor.RED + "The game is already started!");
					}
				}

				else if(sign.getLine(SignValues.TITLE_SIGN_LINE.getValue()).startsWith("[") && sign.getLine(SignValues.TITLE_SIGN_LINE.getValue()).endsWith("]"))
				{
					if(player.isOp())
					{
						String gameName = sign.getLine(SignValues.TITLE_SIGN_LINE.getValue()).substring(1, sign.getLine(SignValues.TITLE_SIGN_LINE.getValue()).length()-1);
						GameType gameType = Main.getGameType(gameName);
						if(gameType == null)
						{
							player.sendMessage(LangValues.GAME_TYPE_FAKE.getValue());
							return;
						}

						sign.setLine(SignValues.TITLE_SIGN_LINE.getValue(), SignValues.SIGN_TITLE_COLOUR.getColor() + "[" + gameType.name + "]");
						sign.setLine(SignValues.PLAYERS_SIGN_LINE.getValue(), "0/" + maxPlayersNum);
						sign.setLine(SignValues.STATUS_SIGN_LINE.getValue(), SignValues.WAITING_SIGN_MSG.toString());
						sign.update(true);
						Main.games.add(new Game(gameKey, gameType.name, plugin, maxPlayersNum, gameType.waitMap, gameType.map, gameType.teamNames, 
								gameType.teamPrefixes, gameType.teamColours, gameType.waitLocs, gameType.locs, sign));
					}
				}
			}
		}
	}
}