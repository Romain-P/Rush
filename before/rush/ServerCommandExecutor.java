package fr.rushland.rush;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

public class ServerCommandExecutor
		implements CommandExecutor {
	Plugin plugin;

	public ServerCommandExecutor(Plugin plugin) {
		this.plugin = plugin;
	}

	void ban(String[] args, CommandSender sender, String bannerName) {
		if (args.length >= 2) {
			StringBuilder b = new StringBuilder();
			for (int x = 1; x < args.length; x++) {
				b.append(args[x]).append(" ");
			}
			String message = b.toString();
			String victimName = args[0];
			try {
				if (DButils.isMember(victimName)) {
					DButils.addBanned(args[0], message, bannerName);
					Player p = Bukkit.getPlayer(victimName);
					if (p != null) {
						p.kickPlayer(Rush.BAN_PREFIX + message);
					}
					Bukkit.broadcastMessage(ChatColor.GREEN + victimName + " was banned by " + bannerName);
				} else {
					sender.sendMessage(Rush.PLAYER_NOT_FOUND);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /ban <victim> <message>");
		}
	}

	void pardon(String[] args, CommandSender sender, String bannerName) {
		if (args.length >= 1) {
			String victimName = args[0];
			try {
				if ((DButils.isMember(victimName)) && (DButils.isBanned(victimName))) {
					DButils.deleteBanned(victimName);
					Bukkit.broadcastMessage(ChatColor.GREEN + victimName + " was pardonned by " + bannerName);
				} else {
					sender.sendMessage(ChatColor.RED + "This player is not banned!");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /pardon <criminal>");
		}
	}

	void vip(String[] args, CommandSender sender) {
		if (args.length >= 2) {
			Player vipPlayer = Bukkit.getServer().getPlayer(args[1]);
			if (args[0].equalsIgnoreCase("add")) {
				try {
					if (DButils.isMember(args[1])) {
						if (!DButils.isVip(args[1])) {
							DButils.addVipMonth(args[1]);
							if (vipPlayer != null) {
								Rush.addVips(args[1]);
								vipPlayer.sendMessage(ChatColor.YELLOW + "Vous êtes maintenant un VIP!");
							}
							sender.sendMessage(ChatColor.YELLOW + args[1] + " has become a VIP!");
						} else {
							sender.sendMessage(ChatColor.RED + args[1] + " is already VIP.");
						}
					} else {
						sender.sendMessage(Rush.PLAYER_NOT_FOUND);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (args[0].equalsIgnoreCase("del")) {
				try {
					if (DButils.isMember(args[1])) {
						if (DButils.isVip(args[1])) {
							DButils.deleteVip(args[1]);
							if (vipPlayer != null) {
								Rush.removeVips(args[1]);
								vipPlayer.sendMessage(ChatColor.YELLOW + "Vous êtes plus un VIP!");
							}
							sender.sendMessage(ChatColor.YELLOW + args[1] + " is no longer a VIP!");
						} else {
							sender.sendMessage(ChatColor.RED + args[1] + " is not a VIP.");
						}
					} else {
						sender.sendMessage(Rush.PLAYER_NOT_FOUND);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				sender.sendMessage(ChatColor.RED + "The option '" + args[0] + "' means shit bro :3");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /vip <option> [value]");
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("vip")) {
			if (DButils.enabled) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					if (player.hasPermission("rushy2.vip")) {
						vip(args, sender);
					} else {
						player.sendMessage(Rush.NO_PERM);
					}
				} else if ((sender instanceof ConsoleCommandSender)) {
					vip(args, sender);
				}
			} else {
				sender.sendMessage(Rush.DB_DISABLED);
			}
		} else if (cmd.getName().equalsIgnoreCase("ban")) {
			if (DButils.enabled) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String name = player.getName();
					if (player.hasPermission("rushy2.ban")) {
						ban(args, sender, name);
					} else {
						player.sendMessage(Rush.NO_PERM);
					}
				} else if ((sender instanceof ConsoleCommandSender)) {
					ban(args, sender, "Console");
				}
			} else {
				sender.sendMessage(Rush.DB_DISABLED);
			}
		} else if (cmd.getName().equalsIgnoreCase("pardon")) {
			if (DButils.enabled) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String name = player.getName();
					if (player.hasPermission("rushy2.pardon")) {
						pardon(args, sender, name);
					} else {
						player.sendMessage(Rush.NO_PERM);
					}
				} else if ((sender instanceof ConsoleCommandSender)) {
					pardon(args, sender, "Console");
				}
			} else {
				sender.sendMessage(Rush.DB_DISABLED);
			}
		} else if (cmd.getName().equalsIgnoreCase("connect")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				if (player.hasPermission("rushy2.connect")) {
					if (args.length >= 1) {
						int type = Integer.parseInt(args[0]);
						switch (type) {
							case 0:
								Utils.goServer(player, "main", this.plugin);
								break;
							case 1:
								Utils.goServer(player, "lobby1vs1", this.plugin);
								break;
							case 2:
								Utils.goServer(player, "lobby2vs2", this.plugin);
								break;
							case 3:
								Utils.goServer(player, "lobby3vs3", this.plugin);
								break;
							case 4:
								Utils.goServer(player, "lobby4vs4", this.plugin);
								break;
							case 5:
								Utils.goServer(player, "lobby4t", this.plugin);
						}
					}
				} else {
					player.sendMessage(Rush.NO_PERM);
				}
			} else {
				sender.sendMessage(Rush.PLAYER_ONLY);
			}
		} else if (cmd.getName().equalsIgnoreCase("lobby")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				String name = player.getName();
				if (player.hasPermission("rushy2.lobby")) {
					if (!Rush.mainServer) {
						Utils.goServer(player, "main", this.plugin);
					} else {
						Game game = Rush.getPlayerGame(name);
						if (game != null) {
							Utils.goNaked(player);
							game.remove(player);
						}
						Rush.giveItem(player);
						Location l = (Bukkit.getServer().getWorlds().get(0)).getSpawnLocation();
						player.teleport(l);
					}
				} else {
					player.sendMessage(Rush.NO_PERM);
				}
			} else {
				sender.sendMessage(Rush.PLAYER_ONLY);
			}
		} else if (cmd.getName().equalsIgnoreCase("stuff")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				if (player.hasPermission("rushy2.stuff")) {
					if (Rush.mainServer) {
						player.openInventory(ServerStuff.kitInv);
					}
				} else {
					player.sendMessage(Rush.NO_PERM);
				}
			} else {
				sender.sendMessage(Rush.PLAYER_ONLY);
			}
		}
		return true;
	}
}