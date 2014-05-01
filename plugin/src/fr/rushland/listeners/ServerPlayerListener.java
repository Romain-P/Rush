package fr.rushland.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.inject.Inject;
import fr.rushland.core.*;
import fr.rushland.database.Database;
import fr.rushland.database.data.PlayerManager;
import fr.rushland.enums.Constants;
import fr.rushland.enums.LangValues;
import fr.rushland.server.ServerStuff;
import fr.rushland.server.games.Game;
import fr.rushland.server.objects.ClientPlayer;
import fr.rushland.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class ServerPlayerListener implements Listener {
    @Inject Config config;
    @Inject PlayerManager manager;
    @Inject fr.rushland.server.Server server;
    @Inject Database database;
    @Inject ServerStuff serverStuff;

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if(database.isEnabled()) {
            ClientPlayer client = server.getPlayer(player.getName(), true);
            manager.reloadVars(client);

            if(client.isBanned()) {
                String message = client.getBannedTime() == -1
                        ? "Vous avez ete banni a vie du serveur par "+client.getBannedAuthor()+": "+client.getBannedReason()
                        : "Vous avez ete banni par "+client.getBannedAuthor()+".\nVous etes encore banni "+client.remainingHoursBannedTime()+" heures. Motif: "+client.getBannedReason();
                event.disallow(Result.KICK_BANNED, message);
            } else if(Bukkit.getOnlinePlayers().length >= Bukkit.getServer().getMaxPlayers()) {
                if(client.getGrade() > 0) {
                    event.allow();
                    for(Player p : Bukkit.getServer().getWorlds().get(0).getPlayers()) {
                        if(server.getPlayers().get(p.getName()).getGrade() == 0)  {
                            p.kickPlayer(LangValues.KICKED_VIP.getValue());
                            break;
                        }
                    }
                }  else
                    event.disallow(Result.KICK_FULL, LangValues.SERVER_FULL.getValue());
            }
	    }
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
        //adding items
        serverStuff.giveStartingItems(player);

        server.attachPrefix(player);

		Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
		player.teleport(l);
		event.setJoinMessage("");
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		event.setLeaveMessage("");
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		World w = event.getTo().getWorld();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if(p.getWorld() != w) {
				player.hidePlayer(p);
				p.hidePlayer(player);
			} else {
				player.showPlayer(p);
				p.showPlayer(player);
			}
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Utils.msgWorld(player, "<" + player.getDisplayName() + "> " + event.getMessage());
		event.setCancelled(true);
	}

	@EventHandler
	public void OnPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (player instanceof Player) {
			String name = player.getName();
			Game game = server.getPlayerGame(name);

			if(game != null) {
				Utils.msgWorld(player, event.getDeathMessage());
                if(player != null) {
                    server.getPlayer(player.getName()).addDeath();
                    if(player.getKiller() != null && player.getKiller() != player) {
                        Player killer = player.getKiller();
                        server.getPlayer(killer.getName()).addKill();
                    }
                }
            }
			event.setDeathMessage("");

			if(config.isMainServer()) {
				event.getDrops().clear();
            }
		}
	}

    @EventHandler
    public void OnPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        Game game = server.getPlayerGame(name);

        if (game == null)
            serverStuff.giveStartingItems(player);
    }

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if(config.isMainServer()) {
			event.setCancelled(true);
			player.updateInventory();
		}
	}

	@EventHandler
	public void preCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		String[] args = message.split(" ");

		if(args[0].equalsIgnoreCase("/me") || args[0].equalsIgnoreCase("/bukkit:me")) {
			player.sendMessage(ChatColor.RED + "Tu te crois important?");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {

		event.setQuitMessage("");
	}

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (player.getItemInHand().equals(serverStuff.getLobbyItem()))
                player.chat("/lobby");
            else if (player.getItemInHand().equals(serverStuff.getPvpItem()))
                player.chat("/stuff");
            else if (player.getItemInHand().equals(serverStuff.getPrestigeItem()))
                player.chat("/prestige");
        }
    }

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(config.isMainServer()) {
			Player p = event.getPlayer();
			Location standBlock = p.getWorld().getBlockAt(p.getLocation().add(0.0D, -0.1D, 0.0D)).getLocation();

			if (standBlock.getBlock().getType() == Material.COAL_BLOCK) {
				int xblock = 0;
				double xvel = 0.0D;
				int yblock = -1;
				double yvel = 0.0D;
				int zblock = 0;
				double zvel = 0.0D;
				while (standBlock.getBlock().getLocation().add(xblock, -1.0D, 0.0D).getBlock().getType().equals(Material.COAL_BLOCK)) {
					xblock--;
					xvel += 0.8D;
				}
				while (standBlock.getBlock().getLocation().add(0.0D, yblock, 0.0D).getBlock().getType().equals(Material.COAL_BLOCK)) {
					yblock--;
					yvel += 1.0D;
				}
				while (standBlock.getBlock().getLocation().add(0.0D, -1.0D, zblock).getBlock().getType().equals(Material.COAL_BLOCK)) {
					zblock--;
					zvel += 0.8D;
				}

				xblock = 0;
				zblock = 0;

				while (standBlock.getBlock().getLocation().add(xblock, -1.0D, 0.0D).getBlock().getType().equals(Material.COAL_BLOCK)) {
					xblock++;
					xvel -= 0.8D;
				}
				while (standBlock.getBlock().getLocation().add(0.0D, -1.0D, zblock).getBlock().getType().equals(Material.COAL_BLOCK)) {
					zblock++;
					zvel -= 0.8D;
				}

				if (standBlock.getBlock().getLocation().add(0.0D, -1.0D, 0.0D).getBlock().getType().equals(Material.COAL_BLOCK))
					p.setVelocity(new Vector(xvel, yvel, zvel));

				p.setFallDistance(0);
			}
		}
	}

	@EventHandler
	public void onPlayerDamage(final EntityDamageEvent e) {
		if(e.getEntity() instanceof Player)
			if(e.getCause() == DamageCause.FALL && config.isMainServer()) {
				e.setCancelled(true);
            }
	}

    @EventHandler
    public void onPlayerReceiveDamages(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            if((attacker.getItemInHand() != null && attacker.getItemInHand().getItemMeta() != null &&
                    attacker.getItemInHand().getItemMeta().getDisplayName() != null
                && attacker.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains("spider"))) {
                Player target = (Player) e.getEntity();

                Random generator = new Random();
                int value = generator.nextInt(6);

                if(value == 1)
                    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Utils.convertSecondsToTicks(3), 1));
            }
        }
    }

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(config.isMainServer()) {
			Player player = (Player) event.getWhoClicked();
			String name = player.getName();
			ItemStack clicked = event.getCurrentItem();
			Inventory inventory = event.getInventory();

            Map<Enchantment, Integer> enchantments = new HashMap<>();
			if (inventory.getName().equals(serverStuff.getInventories().get("Kits").getName())) {
				event.setCancelled(true);
				if(clicked != null && clicked.getItemMeta() != null
                        && clicked.getItemMeta().getLore() != null) {

                    String s = clicked.getItemMeta().getLore().size() < 3
                             ? null
                             : clicked.getItemMeta().getLore().get(2);
                    if(s != null) {
                        int grade = Integer.parseInt(s);
                        if(!server.getPlayers().containsKey(name) || server.getPlayers().get(name).getGrade() < grade) {
                            player.closeInventory();
                            player.sendMessage(LangValues.MUST_BE_VIP.getValue() +" (grade "+grade+")");
                            return;
                        }
                    }
					Utils.goNaked(player);
					PlayerInventory playerInv = player.getInventory();

                    Map<String, ItemStack> items = serverStuff.getPvpItems();

					if (clicked.equals(items.get("Guerrier"))) {
						playerInv.setHelmet(new ItemStack(Material.IRON_HELMET));
						playerInv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
						playerInv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
						playerInv.setBoots(new ItemStack(Material.IRON_BOOTS));
						playerInv.addItem(new ItemStack(Material.IRON_SWORD));
						playerInv.addItem(new ItemStack(Material.COOKED_BEEF, 15));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Constants.SECONDS_IN_YEAR.getValue(), 0));

                    } else if(clicked.equals(items.get("Archer"))) {
                        playerInv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
                        playerInv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                        playerInv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                        playerInv.setBoots(new ItemStack(Material.LEATHER_BOOTS));

                        enchantments.clear();
                        enchantments.put(Enchantment.ARROW_DAMAGE, 2);
                        enchantments.put(Enchantment.ARROW_INFINITE, 1);

                        playerInv.addItem(serverStuff.createCustomItem(
                                Material.BOW, -1,
                                "Hunter Bow",
                                new String[] {"A powerful longbow"},
                                enchantments
                        ));

                        playerInv.addItem(new ItemStack(Material.STONE_SWORD));
                        playerInv.addItem(new ItemStack(Material.COOKED_BEEF, 17));
                        playerInv.addItem(new ItemStack(Material.ARROW));

                    } else if(clicked.equals(items.get("Archer puissant"))) {
                        playerInv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                        playerInv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                        playerInv.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                        playerInv.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));


                        enchantments.clear();
                        enchantments.put(Enchantment.ARROW_DAMAGE, 3);
                        enchantments.put(Enchantment.ARROW_INFINITE, 1);

                        playerInv.addItem(serverStuff.createCustomItem(
                                Material.BOW, -1,
                                "Hunter Bow",
                                new String[] {"A powerful longbow"},
                                enchantments
                        ));
                        playerInv.addItem(new ItemStack(Material.ARROW));
                        playerInv.addItem(new ItemStack(Material.STONE_SWORD));
                        playerInv.addItem(new ItemStack(Material.GOLDEN_APPLE));
                        playerInv.addItem(new ItemStack(Material.COOKED_BEEF, 10));

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Constants.SECONDS_IN_YEAR.getValue(), 1));

                    } else if(clicked.equals(items.get("Spider"))) {
                        playerInv.setHelmet(new ItemStack(Material.IRON_HELMET));
                        playerInv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                        playerInv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                        playerInv.setBoots(new ItemStack(Material.IRON_BOOTS));

                        playerInv.addItem(serverStuff.createCustomItem(
                                Material.IRON_SWORD, -1,
                                "Spider Sword",
                                new String[] {"A powerful sword"}
                        ));

                        playerInv.addItem(new ItemStack(Material.COOKED_BEEF, 10));

                    } else if(clicked.equals(items.get("Mastodonte"))) {
                        playerInv.setHelmet(new ItemStack(Material.IRON_HELMET));
                        playerInv.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                        playerInv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                        playerInv.setBoots(new ItemStack(Material.IRON_BOOTS));

                        playerInv.addItem(serverStuff.createCustomItem(
                                Material.IRON_SWORD, -1,
                                "Masto sword",
                                new String[] {"A powerful sword"}
                        ));

                        playerInv.addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
                        playerInv.addItem(new ItemStack(Material.COOKED_BEEF, 10));

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Constants.SECONDS_IN_YEAR.getValue(), 1));

                    } else if(clicked.equals(items.get("God"))) {
                        playerInv.setHelmet(new ItemStack(Material.IRON_HELMET));
                        playerInv.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                        playerInv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                        playerInv.setBoots(new ItemStack(Material.IRON_BOOTS));

                        for(ItemStack item: player.getEquipment().getArmorContents())
                            serverStuff.addEnchantment(item, Enchantment.PROTECTION_ENVIRONMENTAL, 1);

                        enchantments.clear();
                        enchantments.put(Enchantment.DAMAGE_ALL, 2);

                        playerInv.addItem(serverStuff.createCustomItem(
                                Material.DIAMOND_SWORD, -1,
                                "GOD Sword",
                                new String[] {"A powerful sword"},
                                enchantments
                        ));

                        enchantments.clear();
                        enchantments.put(Enchantment.FIRE_ASPECT, 2);

                        playerInv.addItem(serverStuff.createCustomItem(
                                Material.STICK, -1,
                                "GOD Stick",
                                new String[] {"A powerful stick"},
                                enchantments
                        ));

                        playerInv.addItem(new ItemStack(Material.GOLDEN_APPLE, 5));
                        playerInv.addItem(new ItemStack(Material.COOKED_BEEF, 10));

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Constants.SECONDS_IN_YEAR.getValue(), 1));

                    } else if(clicked.equals(items.get("Troll"))) {
						playerInv.setHelmet(new ItemStack(397, 1, (short) 2));
						playerInv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
						playerInv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
						playerInv.setBoots(new ItemStack(Material.LEATHER_BOOTS));

                        enchantments.clear();
                        enchantments.put(Enchantment.DAMAGE_ALL, 2);
                        enchantments.put(Enchantment.KNOCKBACK, 3);

                        playerInv.addItem(serverStuff.createCustomItem(
                                Material.STICK, -1,
                                "Troll Stick",
                                new String[] {"A powerful stick"},
                                enchantments
                        ));

						playerInv.addItem(new ItemStack(Material.STONE_SWORD));
						playerInv.addItem(new ItemStack(Material.ROTTEN_FLESH, 21));

					} else if(clicked.equals(items.get("Ninja"))) {
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

                        enchantments.clear();
                        enchantments.put(Enchantment.DAMAGE_ALL, 5);
                        enchantments.put(Enchantment.KNOCKBACK, 1);

                        playerInv.addItem(serverStuff.createCustomItem(
                                Material.IRON_HOE, -1,
                                "Kama",
                                new String[] {"A powerful weapon"},
                                enchantments
                        ));

						playerInv.addItem(new ItemStack(Material.BOW, 1));
						playerInv.addItem(new ItemStack(Material.ARROW, 25));
						playerInv.addItem(new ItemStack(Material.COOKED_FISH, 23));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Constants.SECONDS_IN_YEAR.getValue(), 0));
					} else if(clicked.equals(items.get("Mage"))) {
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

                        enchantments.clear();
                        enchantments.put(Enchantment.DAMAGE_ALL, 1);
                        enchantments.put(Enchantment.KNOCKBACK, 1);

                        playerInv.addItem(serverStuff.createCustomItem(
                                Material.IRON_SWORD, -1,
                                "Elfen Sword",
                                new String[] {"A powerful sword"},
                                enchantments
                        ));

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
			} else if (inventory.getName().equals(serverStuff.getInventories().get("Bonus").getName())) {
                event.setCancelled(true);
                if(clicked != null && clicked.getItemMeta() != null
                        && clicked.getItemMeta().getLore() != null) {

                    PlayerInventory playerInv = player.getInventory();

                    Map<String, ItemStack> items = serverStuff.getPvpItems();
                    if(clicked.equals(items.get("Bonus1"))) {
                        ItemStack sword = new ItemStack(Material.GOLD_SWORD);
                        enchantments.clear();
                        enchantments.put(Enchantment.DAMAGE_ALL, 1);
                        enchantments.put(Enchantment.KNOCKBACK, 1);

                        serverStuff.addEnchantment(sword, enchantments);
                    } else if (clicked.equals(items.get("Bonus2"))) {
                        playerInv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                        playerInv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                        playerInv.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                        playerInv.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));

                        for(ItemStack item: player.getEquipment().getArmorContents())
                            serverStuff.addEnchantment(item, Enchantment.PROTECTION_ENVIRONMENTAL, 1);

                    }
                    player.updateInventory();
                    player.closeInventory();
                }
            } else if (inventory.getName().equals(serverStuff.getInventories().get("Prestiges").getName())) {
                event.setCancelled(true);
                if(clicked != null && clicked.getItemMeta() != null
                        && clicked.getItemMeta().getLore() != null) {
                    int prestige, price;
                    switch(clicked.getItemMeta().getDisplayName().substring(ChatColor.RED.toString().length()+9)) {
                        case "I": prestige = 1; price = 5500;
                            break;
                        case "II": prestige = 2; price = 1000;
                            break;
                        case "III": prestige = 3; price = 19000;
                            break;
                        case "IV": prestige = 4; price = 30000;
                            break;
                        case "V": prestige = 5; price = 50000;
                            break;
                        default:
                            player.sendMessage("prestige non disponible");
                            return;
                    }
                    ClientPlayer client = server.getPlayer(player.getName());
                    if(client == null) return;

                    if(client.getPrestige() < prestige-1)
                        player.sendMessage("Vous devez au moins avoir le prestige "+(prestige-1)+"!");
                    else if(client.getPrestige() == prestige)
                        player.sendMessage("Vous ne pouvez pas payer 2x le meme prestige!");
                    else if(client.getPoints() < price)
                        player.sendMessage("Vous devez posseder "+price+" tokens! /token pour voir vos tokens.");
                    else {
                        if(client.getPrestige() > 0)
                            player.setDisplayName(player.getDisplayName().replace(client.getPrestigeAsString(), ""));

                        client.setPrestige(prestige);

                        player.setDisplayName(client.getPrestigeAsString() + player.getDisplayName());
                        client.setPoints(client.getPoints() - price);
                        player.sendMessage("Vous venez d'acheter le prestige "+prestige+" a "+price+" tokens." +
                                " Il vous reste "+client.getPoints()+" tokens.");
                        client.save();
                    }
                }
            }
		}
	}
}