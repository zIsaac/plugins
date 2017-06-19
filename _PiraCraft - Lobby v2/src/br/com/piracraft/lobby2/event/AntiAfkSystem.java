package br.com.piracraft.lobby2.event;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.piracraft.api.PiraCraftAPI;
import br.com.piracraft.api.util.MySQL;

public class AntiAfkSystem extends BukkitRunnable implements Listener {

	private static long TIME = 60 * 1000;
	private static final HashMap<Player, Long> TIMES = new HashMap<>();
	private static final String MESSAGE = MySQL.messages.get("KICK_FOR_AFK");
	static {
		try {
			TIME = Long.valueOf(PiraCraftAPI.getIdNetwork()[3]) *60* 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void event(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		TIMES.put(p, System.currentTimeMillis());
	}

	@EventHandler
	public void event(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		TIMES.put(p, System.currentTimeMillis());
	}

	@Override
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (TIMES.containsKey(p)) {
				Long time = TIMES.get(p);
				long now = System.currentTimeMillis();
				if (now > TIME + time) {
					TIMES.remove(p);
					p.kickPlayer(MESSAGE);;
					TIMES.remove(p);
				}
			}
		}
	}

}
