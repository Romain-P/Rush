package fr.rushland.core;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class ServerPlayerListener implements Listener
{
	Plugin plugin;

	public ServerPlayerListener(JavaPlugin plugin) 
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) 
	{
		Player player = event.getPlayer();
		String name = player.getName();
		if(DButils.enabled)
		{
			try
			{
				if(!DButils.isMember(name))
				{
					DButils.addMember(name);
				}

				else if(DButils.isBanned(name))
				{
					event.disallow(Result.KICK_BANNED, Main.BAN_PREFIX + DButils.getBanMessage(name));
				}

				if(Bukkit.getOnlinePlayers().length >= Bukkit.getServer().getMaxPlayers())
				{
					if(Main.vips.contains(name))
					{
						event.allow();
						for(Player p : Bukkit.getServer().getWorlds().get(0).getPlayers())
						{
							if(!Main.vips.contains(p))
							{
								p.kickPlayer(Main.KICKED_VIP);
								break;
							}
						}
					}

					else
					{
						event.disallow(Result.KICK_FULL, Main.SERVER_FULL);
					}
				}
			} 

			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		String name = player.getName();
		if(DButils.enabled)
		{
			try
			{
				if(DButils.isVip(name))
					Main.addVips(name);
			}

			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}

		Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
		player.teleport(l);
		event.setJoinMessage("");
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		event.setLeaveMessage("");
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		Player player = event.getPlayer();
		World w = event.getTo().getWorld();
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if(p.getWorld() != w)
			{
				player.hidePlayer(p);
				p.hidePlayer(player);
			}

			else
			{
				player.showPlayer(p);
				p.showPlayer(player);
			}
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		Utils.msgWorld(player, "<" + player.getDisplayName() + "> " + event.getMessage());
		event.setCancelled(true);
	}

	@EventHandler
	public void OnPlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		if (player instanceof Player)
		{
			String name = player.getName();
			Game game = Main.getPlayerGame(name);

			if(game != null)
			{
				Utils.msgWorld(player, event.getDeathMessage());
			}

			event.setDeathMessage("");

			if(Main.mainServer)
				event.getDrops().clear();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) 
	{
		Player player = event.getPlayer();
		if(Main.mainServer)
		{
			event.setCancelled(true);
			player.updateInventory();
		}
	}

	@EventHandler
	public void preCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		String message = event.getMessage();
		String[] args = message.split(" ");
		if(args[0].equalsIgnoreCase("/me") || args[0].equalsIgnoreCase("/bukkit:me"))
		{
			player.sendMessage(ChatColor.RED + "Tu te croie important?");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		String name = player.getName();
		if(DButils.enabled)
		{
			Main.removeVips(name);
		}

		event.setQuitMessage("");
	}

	//kits

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if(Main.mainServer)
		{
			Player p = event.getPlayer();
			Location standBlock = p.getWorld().getBlockAt(p.getLocation().add(0.0D, -0.1D, 0.0D)).getLocation();
			if (standBlock.getBlock().getType() == Material.COAL_BLOCK)
			{
				int xblock = 0;
				double xvel = 0.0D;
				int yblock = -1;
				double yvel = 0.0D;
				int zblock = 0;
				double zvel = 0.0D;
				while (standBlock.getBlock().getLocation().add(xblock, -1.0D, 0.0D).getBlock().getType().equals(Material.COAL_BLOCK))
				{
					xblock--;
					xvel += 0.8D;
				}
				while (standBlock.getBlock().getLocation().add(0.0D, yblock, 0.0D).getBlock().getType().equals(Material.COAL_BLOCK))
				{
					yblock--;
					yvel += 1.0D;
				}
				while (standBlock.getBlock().getLocation().add(0.0D, -1.0D, zblock).getBlock().getType().equals(Material.COAL_BLOCK))
				{
					zblock--;
					zvel += 0.8D;
				}
				xblock = 0;
				zblock = 0;
				while (standBlock.getBlock().getLocation().add(xblock, -1.0D, 0.0D).getBlock().getType().equals(Material.COAL_BLOCK))
				{
					xblock++;
					xvel -= 0.8D;
				}
				while (standBlock.getBlock().getLocation().add(0.0D, -1.0D, zblock).getBlock().getType().equals(Material.COAL_BLOCK))
				{
					zblock++;
					zvel -= 0.8D;
				}
				if (standBlock.getBlock().getLocation().add(0.0D, -1.0D, 0.0D).getBlock().getType().equals(Material.COAL_BLOCK)) {
					p.setVelocity(new Vector(xvel, yvel, zvel));
				}

				p.setFallDistance(0);
			}
		}
	}

	@EventHandler
	public void onPlayerDamage(final EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			if(e.getCause() == DamageCause.FALL && Main.mainServer)
			{
				e.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) 
	{
		if(Main.mainServer)
		{
			Player player = (Player) event.getWhoClicked();
			String name = player.getName();
			ItemStack clicked = event.getCurrentItem();
			Inventory inventory = event.getInventory();
			if (inventory.getName().equals(ServerStuff.kitInv.getName())) 
			{
				event.setCancelled(true);
				if(clicked != null)
				{
					if(clicked.getItemMeta().getLore().contains(ServerStuff.VIP_PREFIX) && !Main.vips.contains(name))
					{
						player.closeInventory();
						player.sendMessage(Main.MUST_BE_VIP);
						return;
					}

					Utils.goNaked(player);
					PlayerInventory playerInv = player.getInventory();
					if (clicked.equals(ServerStuff.warriorIcon))
					{
						playerInv.setHelmet(new ItemStack(Material.IRON_HELMET));
						playerInv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
						playerInv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
						playerInv.setBoots(new ItemStack(Material.IRON_BOOTS));
						playerInv.addItem(new ItemStack(Material.IRON_SWORD));
						playerInv.addItem(new ItemStack(Material.COOKED_BEEF, 15));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Main.ONE_YEAR_SECS, 0));
					}

					else if(clicked.equals(ServerStuff.hunterIcon))
					{
						playerInv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
						playerInv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
						playerInv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
						playerInv.setBoots(new ItemStack(Material.LEATHER_BOOTS));

						ItemStack huntersBow = new ItemStack(Material.BOW);
						ItemMeta meta = huntersBow.getItemMeta();
						meta.setDisplayName(ChatColor.GRAY + "Hunter's Bow");
						ArrayList<String> lore = new ArrayList<String>();
						lore.add("A powerful longbow");
						meta.setLore(lore);
						meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
						meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
						huntersBow.setItemMeta(meta);

						playerInv.addItem(huntersBow);
						playerInv.addItem(new ItemStack(Material.STONE_SWORD));
						playerInv.addItem(new ItemStack(Material.COOKED_BEEF, 17));
						playerInv.addItem(new ItemStack(Material.ARROW));
					}

					else if(clicked.equals(ServerStuff.trollIcon))
					{
						playerInv.setHelmet(new ItemStack(397, 1, (short) 2));
						playerInv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
						playerInv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
						playerInv.setBoots(new ItemStack(Material.LEATHER_BOOTS));

						ItemStack trollsClub = new ItemStack(Material.STICK);
						ItemMeta meta = trollsClub.getItemMeta();
						meta.setDisplayName(ChatColor.GRAY + "Troll's Club");
						ArrayList<String> lore = new ArrayList<String>();
						lore.add("Just a stick");
						meta.setLore(lore);
						meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
						meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
						trollsClub.setItemMeta(meta);

						playerInv.addItem(trollsClub);
						playerInv.addItem(new ItemStack(Material.STONE_SWORD));
						playerInv.addItem(new ItemStack(Material.ROTTEN_FLESH, 21));
					}

					else if(clicked.equals(ServerStuff.ninjaIcon))
					{
						ItemStack ninjaMask = new ItemStack(Material.LEATHER_HELMET);
						LeatherArmorMeta im = (LeatherArmorMeta) ninjaMask.getItemMeta();
						im.setColor(Color.BLACK);
						ninjaMask.setItemMeta(im);

						playerInv.setHelmet(ninjaMask);

						ItemStack ninjaChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
						im = (LeatherArmorMeta) ninjaChestplate.getItemMeta();
						im.setColor(Color.BLACK);
						ninjaChestplate.setItemMeta(im);

						playerInv.setChestplate(ninjaChestplate);

						ItemStack ninjaLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
						im = (LeatherArmorMeta) ninjaLeggings.getItemMeta();
						im.setColor(Color.BLACK);
						ninjaLeggings.setItemMeta(im);

						playerInv.setLeggings(ninjaLeggings);

						ItemStack ninjaBoots = new ItemStack(Material.LEATHER_BOOTS);
						im = (LeatherArmorMeta) ninjaBoots.getItemMeta();
						im.setColor(Color.BLACK);
						ninjaBoots.setItemMeta(im);

						playerInv.setBoots(ninjaBoots);

						ItemStack kama = new ItemStack(Material.IRON_HOE);
						ItemMeta meta = kama.getItemMeta();
						meta.setDisplayName(ChatColor.GRAY + "Kama");
						ArrayList<String> lore = new ArrayList<String>();
						meta.setLore(lore);
						meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
						meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
						kama.setItemMeta(meta);

						playerInv.addItem(kama);
						playerInv.addItem(new ItemStack(Material.BOW, 1));
						playerInv.addItem(new ItemStack(Material.ARROW, 25));
						playerInv.addItem(new ItemStack(Material.COOKED_FISH, 23));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Main.ONE_YEAR_SECS, 0));
					}

					else if(clicked.equals(ServerStuff.mageIcon))
					{
						ItemStack mageHelmet = new ItemStack(Material.LEATHER_HELMET);
						LeatherArmorMeta im = (LeatherArmorMeta) mageHelmet.getItemMeta();
						im.setColor(Color.PURPLE);
						mageHelmet.setItemMeta(im);

						playerInv.setHelmet(mageHelmet);

						playerInv.setChestplate(new ItemStack (Material.CHAINMAIL_CHESTPLATE));

						ItemStack mageLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
						im = (LeatherArmorMeta) mageLeggings.getItemMeta();
						im.setColor(Color.PURPLE);
						mageLeggings.setItemMeta(im);

						playerInv.setLeggings(mageLeggings);

						playerInv.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));

						ItemStack mageSword = new ItemStack(Material.IRON_SWORD);
						ItemMeta meta = mageSword.getItemMeta();
						meta.setDisplayName(ChatColor.GRAY + "Elfen Sword");
						ArrayList<String> lore = new ArrayList<String>();
						meta.setLore(lore);
						meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
						meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
						mageSword.setItemMeta(meta);

						playerInv.addItem(mageSword);
						playerInv.addItem(new ItemStack(Material.APPLE, 47));
						
						Potion potion = new Potion(PotionType.INSTANT_HEAL);
						ItemStack potionItem = potion.toItemStack(1);
						
						playerInv.addItem(potionItem);
						
						potion = new Potion(PotionType.INSTANT_DAMAGE);
						potion.setSplash(true);
						potionItem = potion.toItemStack(1);
						playerInv.addItem(potionItem);
						
						potion = new Potion(PotionType.POISON);
						potion.setSplash(true);
						potionItem = potion.toItemStack(1);
						playerInv.addItem(potionItem);
						
						potion = new Potion(PotionType.WEAKNESS);
						potion.setSplash(true);
						potionItem = potion.toItemStack(1);
						playerInv.addItem(potionItem);
					}

					player.updateInventory();
					player.closeInventory();				
					player.teleport(new Location(Bukkit.getServer().getWorlds().get(0), -220, 106, -539));
				}
			}
		}
	}
}