package com.hcp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.hcp.daays.Days;
import com.hcp.daays.Difficulty;
import com.hcp.daays.MobSpawn;
import com.hcp.daays.Range;
import com.hcp.utils.Join;
import com.hcp.utils.Score;
import com.hcp.win.Utils;

import br.com.piracraft.api.Main;
import br.com.piracraft.api.PiraCraftAPI;
import br.com.piracraft.api.caixas.ItemAPI;
import br.com.piracraft.api.util.MySQL;

public class Eventos implements Listener {
	
	public static Map<String, Location> startLoc = new HashMap<String, Location>();

	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		e.setFormat(e.getPlayer().getDisplayName() + " �f�l� �7" + e.getMessage());
	}

	public static HashMap<String, Location> spawnLoc = new HashMap<String, Location>();

	@EventHandler
	public void banned(PlayerLoginEvent e) {
		if (e.getResult() == Result.KICK_BANNED) {
			e.setKickMessage(
					"�cSem autorizacao para entrar aqui!\n�cMotivo: �7Ja morreu e ainda nao comprou o reviver.\n�6Solucoes: �7Adquira o reviver indo ate o NPC ou\n�7aguarde mais 5 minutos e tente novamente!\n"
							+ "�awww.piracraft.com.br");
		}else{
			
		}
	}

	public static Map<String, Date> tempo = new HashMap<String, Date>();
	public static Map<String, Date> tempoEntrada = new HashMap<String, Date>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = (Player) e.getPlayer();
		e.setJoinMessage(null);
		Join.giveItems(p);
	}

	@EventHandler
	public void quit(PlayerQuitEvent e) {
		Player p = (Player) e.getPlayer();
		e.setQuitMessage(null);

		if(com.hcp.Main.iniciado){
			MySQL.execute(
					"INSERT INTO 7_DIAS_ENTRADAS_SAIDAS (`UUID`,`ID_SALA`,`TIPO`) VALUES ('" + Main.uuid.get(p) + "','"+PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[0]+"','2');");
			
			Calendar horaSaidaEntrada = Calendar.getInstance();
			horaSaidaEntrada.setTimeInMillis(new Date().getTime() - tempoEntrada.get(Main.uuid.get(p)).getTime()
					+ tempo.get(Main.uuid.get(p)).getTime());
			// horaSaidaEntrada.setTimeInMillis(horaSaidaEntrada.getTimeInMillis() -
			// entrada.getTimeInMillis() + banco.getTimeInMillis());

			MySQL
					.execute("UPDATE 7_DIAS_INSCRICAO_SALA SET TEMPO_CORRIDO = '"
							+ new SimpleDateFormat("HH:mm:ss").format(horaSaidaEntrada.getTime()) + "' WHERE UUID = '"
							+ Main.uuid.get(p) + "' AND ID_NETWORK = '"+Main.network.get(p)+"'");

			tempo.remove(p);
			tempoEntrada.remove(p);

			String as = new SimpleDateFormat("HH:mm").format(new Date());
			for (Player s : Bukkit.getOnlinePlayers()) {
				if (br.com.piracraft.api.Main.network.get(s) == 1) {
					Score score = new Score(Main.uuid.get(s), "�6�lPira�f�lCraft", 
							Arrays.asList("�0 ",
							"Coins: �b" + Eventos.coins.get(s),
							"Cash: �b" + Eventos.cash.get(s),
							"�3 ",
							"Jogadores: �b" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers(),
							"Relogio: �b" + as,
							"TimePlay �b" + new SimpleDateFormat("HH:mm:ss").format(Eventos.tempo.get(Main.uuid.get(s))),
							"Dia: �b" + Days.days,
							"�4 ",
							"�apiracraft.com.br"));
					
					score.create();
					score.set(s);
				} else {
					Score score = new Score(Main.uuid.get(s), "�4�lU�6�lPlay�9�lCraft", 
							Arrays.asList("�0 ",
							"Coins: �b" + Eventos.coins.get(s),
							"Cash: �b" + Eventos.cash.get(s),
							"�3 ",
							"Players: �b" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers(),
							"Clock: �b" + as,
							"TimePlay �b" + new SimpleDateFormat("HH:mm:ss").format(Eventos.tempo.get(Main.uuid.get(s))),
							"Day: �b" + Days.days,
							"�4 ",
							"�auplaycraft.com"));
					
					score.create();
					score.set(s);
				}
			}
		}
	}

	@EventHandler
	public void dano(EntityDamageByEntityEvent e) {
		if(com.hcp.Main.iniciado){
			if (!(e.getDamager() instanceof Player)) {
				e.setDamage(Difficulty.damage);
			} else {
				e.setCancelled(false);
				e.setDamage(e.getDamage());
			}
		}else{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void dano(EntityDamageEvent e) {
		if(com.hcp.Main.iniciado){
			e.setCancelled(false);
		}else{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void spawn(CreatureSpawnEvent e) {
		if (Difficulty.expert) {
			if (e.getEntity() instanceof Zombie) {
				if (new Random().nextInt(100) >= 50) {
					((Zombie) e.getEntity()).getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
					((Zombie) e.getEntity()).getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
				} else {
					((Zombie) e.getEntity()).getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
					((Zombie) e.getEntity()).getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
				}
			}
		}
	}

	public HashMap<Player, Player> getKiller = new HashMap<Player, Player>();
	public static HashMap<Player, Integer> coins = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> cash = new HashMap<Player, Integer>();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Player p = (Player) e.getPlayer();

		if (getKiller.get(p) == p) {
			if (Main.network.get(p) == 1) {
				p.sendMessage(
						"�cVoce acabou de morrer no �6�oHardcore�c.\n�7Voce pode adquirir seu �oreviver �7na loja�7.\n�awww.piracraft.com.br");
			} else {
				p.sendMessage(
						"�cYou died on �6�oHardcore�c.\n�7You can buy your �orespawn �7on the store�7.\n�awww.uplaycraft.com");
			}
			com.hcp.utils.Utils.enviar(p, "lobby");
		} else {
			if (Main.network.get(p) == 1) {
				p.sendMessage("�e" + p.getName() + " �7foi morto por �e" + getKiller.get(p).getName() + "�7.");
			} else {
				p.sendMessage("�e" + p.getName() + " �7was killed by �e" + getKiller.get(p).getName() + "�7.");
			}
			com.hcp.utils.Utils.enviar(p, "lobby");
		}
		p.setBanned(true);
		getKiller.remove(p);
		Utils.makeWin();
		
		MySQL.execute(
				"UPDATE MINIGAMES_SALAS_E_SERVIDORES SET SALA_REFRESH = 1 WHERE ID_MINIGAMESSALAS = "
						+ "'"
						+ PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[0]
						+ "'");
	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		Player p = (Player) e.getEntity();
		Player k = (Player) p.getKiller();

		e.setDeathMessage(null);

		for (Player s : Bukkit.getOnlinePlayers()) {
			s.playSound(s.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
		}

		if (k == null) {
			if(startLoc.containsKey(Main.uuid.get(p))){
				startLoc.put(Main.uuid.get(p), p.getLocation());
				Join.firstJoin.remove(p.getName());
			}
			DamageCause c = e.getEntity().getLastDamageCause().getCause();

			for (Player all : Bukkit.getOnlinePlayers()) {
				if (Main.network.get(all) == 1) {
					if (c == DamageCause.LAVA) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7morreu queimado na lava.");
					}
					if (c == DamageCause.SUICIDE) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7cometeu suic�dio.");
					}
					if (c == DamageCause.FALL) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7morreu de altura.");
					}
					if (c == DamageCause.DROWNING) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7morreu afogado.");
					}
					if (c == DamageCause.POISON) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7morreu envenenado.");
					}
					if (c == DamageCause.ENTITY_ATTACK) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7morreu por alguma entidade.");
					}
					if (c == DamageCause.ENTITY_EXPLOSION) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7morreu por um creeper.");
					}
					if (c == DamageCause.FIRE) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7morreu queimado.");
					}
					if (c == DamageCause.STARVATION) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7morreu de fome.");
					}
					if (c == DamageCause.THORNS) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7morreu nos espinhos.");
					}
					if (c == DamageCause.BLOCK_EXPLOSION) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7explodiu.");
					}
				} else {
					if (c == DamageCause.LAVA) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7dead burning.");
					}
					if (c == DamageCause.SUICIDE) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7commited suicide.");
					}
					if (c == DamageCause.FALL) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7fall to death.");
					}
					if (c == DamageCause.DROWNING) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7drowned.");
					}
					if (c == DamageCause.POISON) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7poisoned.");
					}
					if (c == DamageCause.ENTITY_ATTACK) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7dead by some entity.");
					}
					if (c == DamageCause.ENTITY_EXPLOSION) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7blew up.");
					}
					if (c == DamageCause.FIRE) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7dead burning.");
					}
					if (c == DamageCause.STARVATION) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7starved to death.");
					}
					if (c == DamageCause.THORNS) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7dead by the thorns.");
					}
					if (c == DamageCause.BLOCK_EXPLOSION) {
						all.sendMessage("�e" + e.getEntity().getName() + " �7blew up.");
					}
				}

				if (Main.network.get(p) == 1) {
					p.sendMessage(
							"�cVoce acabou de morrer no �6�oHardcore�c.\n�7Voce pode adquirir seu �oreviver �7.\n�awww.piracraft.com.br");
				} else {
					p.sendMessage(
							"�cYou died on �6�oHardcore�c.\n�7You can buy your �orespawn �7on the store�7.\n�awww.uplaycraft.com");
				}

				getKiller.put(p, p);
			}
		} else {
			if(startLoc.containsKey(Main.uuid.get(p))){
				startLoc.put(Main.uuid.get(p), k.getLocation());
				Join.firstJoin.remove(p.getName());
			}
			
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (Main.network.get(all) == 1) {
					all.sendMessage("�e" + e.getEntity().getName() + " �7foi morto por �e" + k.getName() + "�7.");
				} else {
					all.sendMessage("�e" + e.getEntity().getName() + " �7was killed by �e" + k.getName() + "�7.");
				}
			}

			getKiller.put(p, k);
			if (!Main.isStaff.get(k)) {
				if (Main.isVip.get(k)) {
					MySQL.execute(
							"INSERT INTO MINIGAMES_XP (`ID_MINIGAMES`,`ID_SALA`,`UUID`,`XP`,`CASH`,`COINS`,`ID_NETWORK`) VALUES ('"
									+ PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[2] + "','" + PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[0] + "'" + ",'"
									+ Main.uuid.get(k) + "','100','2','2000','" + Main.network.get(k) + "')");
					if (coins.containsKey(k)) {
						coins.put(k, coins.get(k) + 2000);
					} else {
						coins.put(k, 2000);
					}

					if (cash.containsKey(k)) {
						cash.put(k, cash.get(k) + 2);
					} else {
						cash.put(k, 2);
					}
					
					if(Main.network.get(k) != 1){
						k.sendMessage("�4�lHardcore�f�l� �aYou got +2000 coins, +100 xp and +2 cash.");
					}else{
						k.sendMessage("�4�lHardcore�f�l� �aVoce ganhou +2000 coins e +2 cash.");
					}
				} else {
					MySQL.execute(
							"INSERT INTO MINIGAMES_XP (`ID_MINIGAMES`,`ID_SALA`,`UUID`,`XP`,`CASH`,`COINS`,`ID_NETWORK`) VALUES ('"
									+ PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[2] + "','" + PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[0] + "'" + ",'"
									+ Main.uuid.get(k) + "','50','1','1000','" + Main.network.get(k) + "')");

					if (coins.containsKey(k)) {
						coins.put(k, coins.get(k) + 1000);
					} else {
						coins.put(k, 1000);
					}

					if (cash.containsKey(k)) {
						cash.put(k, cash.get(k) + 1);
					} else {
						cash.put(k, 1);
					}
					
					if(Main.network.get(k) != 1){
						k.sendMessage("�4�lHardcore�f�l� �aYou got +1000 coins, +50 xp and +1 cash.");
					}else{
						k.sendMessage("�4�lHardcore�f�l� �aVoce ganhou +1000 coins, +50 xp e +1 cash.");
					}
				}
			} else {
				MySQL.execute(
						"INSERT INTO MINIGAMES_XP (`ID_MINIGAMES`,`ID_SALA`,`UUID`,`XP`,`CASH`,`COINS`,`ID_NETWORK`) VALUES ('"
								+ PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[2] + "','" + PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[0] + "'" + ",'"
								+ Main.uuid.get(k) + "','300','0','5000','" + Main.network.get(k) + "')");

				if (coins.containsKey(k)) {
					coins.put(k, coins.get(k) + 5000);
				} else {
					coins.put(k, 5000);
				}

				if (cash.containsKey(k)) {
					cash.put(k, cash.get(k) + 0);
				} else {
					cash.put(k, 0);
				}

				k.sendMessage("�4�lHardcore�f�l� �dVoce e um staffer e nao ganhou nenhum cash!");
			}

			k.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 3));
			
			if(Main.network.get(k) == 1){
				k.sendMessage("�4�lHardcore�f�l� �aVoce ganhou �b3 �acarnes!");
			}else{
				k.sendMessage("�4�lHardcore�f�l� �aYou got �b3 �abeefs!");
			}
		}
		
		MySQL.execute("INSERT INTO 7_DIAS_REGISTRO_MORTES (`ID_SALA`,`UUID`) VALUES ('"
					+ PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[0] + "','" + Main.uuid.get(p) + "')");
	}

	@EventHandler
	public void creeper(EntityExplodeEvent e) {
		if (e.getEntity() instanceof Creeper) {
			if(Days.days < 4){
				Bukkit.getWorld("world").createExplosion(e.getEntity().getLocation(), (float) 3.0F);
			}else{
				Bukkit.getWorld("world").createExplosion(e.getEntity().getLocation(), (float) 7.0F);
			}
		}
	}

	@EventHandler
	public void put(PlayerDropItemEvent e) {
		if(com.hcp.Main.iniciado){
			if (e.getItemDrop().getItemStack().hasItemMeta()) {
				if (e.getItemDrop().getItemStack().getType() == Material.ENDER_CHEST) {
					e.setCancelled(true);
				}
			}
		}else{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void bees(BlockBreakEvent e){
		if(com.hcp.Main.iniciado){
			e.setCancelled(false);
		}else{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void put(BlockPlaceEvent e) {

		if(com.hcp.Main.iniciado){
			e.setCancelled(false);
		}else{
			e.setCancelled(true);
		}
	}

	@EventHandler
	void clicar(PlayerInteractEvent e) {
		if (e.getClickedBlock() != null) {
			if (e.getClickedBlock().getType() == Material.ANVIL) {
				if (e.getAction().name().contains("RIGHT")) {
					e.setCancelled(true);
				}
			}
		}
	}
}
