package fr.rushland.server.commands;

import java.sql.SQLException;

import com.google.inject.Inject;
import fr.rushland.core.*;
import fr.rushland.database.Database;
import fr.rushland.enums.LangValues;
import fr.rushland.server.Server;
import fr.rushland.server.ServerStuff;
import fr.rushland.server.games.Game;
import fr.rushland.utils.DatabaseUtils;
import fr.rushland.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerCommandExecutor implements CommandExecutor {
    @Inject JavaPlugin plugin;
    @Inject Server server;
    @Inject ServerStuff serverStuff;
    @Inject Database database;
    @Inject DatabaseUtils databaseUtils;
    @Inject Config config;

	public void ban(String[] args, CommandSender sender, String bannerName) {
		if(args.length >= 2) {
			StringBuilder b = new StringBuilder();

			for (int x = 1; x < args.length; x++)
				b.append(args[x]).append(" ");

			String message = b.toString();
			String victimName = args[0];

            if(databaseUtils.isMember(victimName)) {
                databaseUtils.addBanned(args[0], message, bannerName);
                Player p = Bukkit.getPlayer(victimName);
                if(p != null)
                {
                    p.kickPlayer(LangValues.BAN_PREFIX.getValue() + message);
                }

                Bukkit.broadcastMessage(ChatColor.GREEN + victimName + " was banned by " + bannerName);
            } else
               sender.sendMessage(LangValues.PLAYER_NOT_FOUND.getValue());
        } else
			sender.sendMessage(ChatColor.RED + "Usage: /ban <victim> <message>");

	}

	public void pardon(String[] args, CommandSender sender, String bannerName) {
		if(args.length >= 1) {
			String victimName = args[0];
            if(databaseUtils.isMember(victimName) && databaseUtils.isBanned(victimName)){
                databaseUtils.deleteBanned(victimName);
                Bukkit.broadcastMessage(ChatColor.GREEN + victimName + " was pardonned by " + bannerName);
            } else
                sender.sendMessage(ChatColor.RED + "This player is not banned!");
		} else
			sender.sendMessage(ChatColor.RED + "Usage: /pardon <criminal>");
	}

	void vip(String[] args, CommandSender sender) {
		if(args.length >= 2) {
			Player vipPlayer = Bukkit.getServer().getPlayer(args[1]);

			if(args[0].equalsIgnoreCase("add")) {
                if(databaseUtils.isMember(args[1])) {
                    if(!databaseUtils.isVip(args[1])) {
                        databaseUtils.addVipMonth(args[1]);

                        if(vipPlayer != null) {
                            server.addVips(args[1]);
                            vipPlayer.sendMessage(ChatColor.YELLOW + "Vous �tes maintenant un VIP!");
                        }
                        sender.sendMessage(ChatColor.YELLOW + args[1] + " has become a VIP!");
                    } else
                        sender.sendMessage(ChatColor.RED + args[1] + " is already VIP.");
                } else
                    sender.sendMessage(LangValues.PLAYER_NOT_FOUND.getValue());
			} else if(args[0].equalsIgnoreCase("del")) {
                if(databaseUtils.isMember(args[1]))  {
                    if(databaseUtils.isVip(args[1])) {
                        databaseUtils.deleteVip(args[1]);
                        if(vipPlayer != null) {
                            server.removeVips(args[1]);
                            vipPlayer.sendMessage(ChatColor.YELLOW + "Vous �tes plus un VIP!");
                        }
                        sender.sendMessage(ChatColor.YELLOW + args[1] + " is no longer a VIP!");
                    }else
                        sender.sendMessage(ChatColor.RED + args[1] + " is not a VIP.");
                } else
                    sender.sendMessage(LangValues.PLAYER_NOT_FOUND.getValue());
			} else
				sender.sendMessage(ChatColor.RED + "The option '" + args[0] + "' means shit bro :3");
		} else
			sender.sendMessage(ChatColor.RED + "Usage: /vip <option> [value]");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if(cmd.getName().equalsIgnoreCase("vip"))
		{
			if(database.isEnabled())
			{
				if(sender instanceof Player)
				{
					Player player = (Player) sender;
					if(player.hasPermission("rushy2.vip"))
					{
						vip(args, sender);
					}

					else
					{
						player.sendMessage(LangValues.NO_PERM.getValue());
					}
				}

				else if(sender instanceof ConsoleCommandSender)
				{
					vip(args, sender);
				}
			}

			else
			{
				sender.sendMessage(LangValues.DB_DISABLED.getValue());
			}
		}

		else if(cmd.getName().equalsIgnoreCase("ban"))
		{
			if(database.isEnabled())
			{
				if(sender instanceof Player)
				{
					Player player = (Player) sender;
					String name = player.getName();

					if(player.hasPermission("rushy2.ban"))
					{
						ban(args, sender, name);
					}

					else
					{
						player.sendMessage(LangValues.NO_PERM.getValue());
					}
				}

				else if(sender instanceof ConsoleCommandSender)
				{
					ban(args, sender, "Console");
				}
			}

			else
			{
				sender.sendMessage(LangValues.DB_DISABLED.getValue());
			}
		}

		else if(cmd.getName().equalsIgnoreCase("pardon"))
		{
			if(database.isEnabled())
			{
				if(sender instanceof Player)
				{
					Player player = (Player) sender;
					String name = player.getName();

					if(player.hasPermission("rushy2.pardon"))
					{
						pardon(args, sender, name);
					}

					else
					{
						player.sendMessage(LangValues.NO_PERM.getValue());
					}
				}

				else if(sender instanceof ConsoleCommandSender)
				{
					pardon(args, sender, "Console");
				}
			}

			else
			{
				sender.sendMessage(LangValues.DB_DISABLED.getValue());
			}
		}

		else if(cmd.getName().equalsIgnoreCase("connect"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;

				if(player.hasPermission("rushy2.connect"))
				{
					if(args.length >= 1)
					{
						int type = Integer.parseInt(args[0]);
						switch(type)
						{
						case 0:
							Utils.goServer(player, "main", plugin);
							break;
						case 1:
							Utils.goServer(player, "lobby1vs1", plugin);
							break;
						case 2:
							Utils.goServer(player, "lobby2vs2", plugin);
							break;
						case 3:
							Utils.goServer(player, "lobby3vs3", plugin);
							break;
						case 4:
							Utils.goServer(player, "lobby4vs4", plugin);
							break;
						case 5:
							Utils.goServer(player, "lobby4t", plugin);
							break;
						}
					}
				}

				else
				{
					player.sendMessage(LangValues.NO_PERM.getValue());
				}
			}

			else
			{
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());
			}
		}

		else if(cmd.getName().equalsIgnoreCase("lobby"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.lobby"))
				{
					if(!config.isMainServer())
					{
						Utils.goServer(player, "main", plugin);
					}

					else
					{
						final Game game = server.getPlayerGame(name);

						if(game != null)
						{
							Utils.goNaked(player);
							game.remove(player);
						}

						Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
						player.teleport(l);
					}
				}

				else
				{
					player.sendMessage(LangValues.NO_PERM.getValue());
				}
			}

			else
			{
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());
			}
		}

		else if(cmd.getName().equalsIgnoreCase("stuff"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;

				if(player.hasPermission("rushy2.stuff"))
				{
					if(config.isMainServer())
					{
						player.openInventory(serverStuff.getKitInv());
					}
				}

				else
				{
					player.sendMessage(LangValues.NO_PERM.getValue());
				}
			}

			else
			{
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());
			}
		}
		
		else if(cmd.getName().equalsIgnoreCase("disg"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;

				if(player.hasPermission("rushy2.disg"))
				{
					if(args.length >= 1)
					{
				        player.setDisplayName(args[0]);
				        player.setPlayerListName(args[0]);
					}
				}

				else
				{
					player.sendMessage(LangValues.NO_PERM.getValue());
				}
			}

			else
			{
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());
			}
		}
		
		return true;
	}
}
