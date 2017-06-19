package br.com.piracraft.lobby2.utils;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.base.Charsets;

import br.com.piracraft.api.PiraCraftAPI;
import br.com.piracraft.lobby2.Main;

public class SetarScoreBoard {

	private Player player;
	private Integer network;

	public SetarScoreBoard(Player player, Integer network) {
		setPlayer(player);
		setNetwork(network);
	}

	@SuppressWarnings("deprecation")
	public void setar() {
		if (getNetwork() == 1) {
			if (getPlayer().getScoreboard() == null
					|| getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) {
				Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
				Objective obj = board.registerNewObjective("a", "b");
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
				Scroller scroller = new Scroller("�6PiraCraft", 16, 5, '�');
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!getPlayer().isOnline()) {
							cancel();
						}
						obj.setDisplayName(scroller.next());
					}
				}.runTaskTimer(Main.getPlugin(), 0, 3);
				obj.getScore("              ").setScore(6);
				obj.getScore("Seus Cashs: ").setScore(5);
				obj.getScore("Seus Coins: ").setScore(4);
				obj.getScore("Suas Caixas: ").setScore(3);
				obj.getScore(" ").setScore(2);
				obj.getScore("�7www.piracraft.com.br").setScore(1);

				board.registerNewTeam("espaco").addPlayer(new FastOfflinePlayer("              "));
				board.registerNewTeam("cashs").addPlayer(new FastOfflinePlayer("Seus Cashs: "));
				board.registerNewTeam("coins").addPlayer(new FastOfflinePlayer("Seus Coins: "));
				board.registerNewTeam("caixas").addPlayer(new FastOfflinePlayer("Suas Caixas: "));
				getPlayer().setScoreboard(board);
			}
			getPlayer().getScoreboard().getTeam("espaco").setSuffix("     ");
			getPlayer().getScoreboard().getTeam("cashs")
					.setSuffix(String.valueOf(new PiraCraftAPI(getPlayer()).getCash()));
			getPlayer().getScoreboard().getTeam("coins")
					.setSuffix(String.valueOf(new PiraCraftAPI(getPlayer()).getCoins()));
			getPlayer().getScoreboard().getTeam("caixas").setSuffix("0");
		} else {

		}
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Integer getNetwork() {
		return network;
	}

	public void setNetwork(Integer network) {
		this.network = network;
	}
	public static class FastOfflinePlayer implements OfflinePlayer {
		private final String playerName;

		public FastOfflinePlayer(String playerName) {
			this.playerName = playerName;
		}

		public boolean isOnline() {
			return false;
		}

		public String getName() {
			return this.playerName;
		}

		public UUID getUniqueId() {
			return UUID.nameUUIDFromBytes(this.playerName.getBytes(Charsets.UTF_8));
		}

		public boolean isBanned() {
			return false;
		}

		public void setBanned(boolean banned) {
			throw new UnsupportedOperationException();
		}

		public boolean isWhitelisted() {
			return false;
		}

		public void setWhitelisted(boolean value) {
			throw new UnsupportedOperationException();
		}

		public Player getPlayer() {
			throw new UnsupportedOperationException();
		}

		public long getFirstPlayed() {
			return System.currentTimeMillis();
		}

		public long getLastPlayed() {
			return System.currentTimeMillis();
		}

		public boolean hasPlayedBefore() {
			return false;
		}

		public Location getBedSpawnLocation() {
			throw new UnsupportedOperationException();
		}

		public boolean isOp() {
			return false;
		}

		public void setOp(boolean value) {
			throw new UnsupportedOperationException();
		}

		public Map<String, Object> serialize() {
			throw new UnsupportedOperationException();
		}
	}


}
