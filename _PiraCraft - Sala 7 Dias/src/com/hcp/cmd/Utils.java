package com.hcp.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hcp.Eventos;

import br.com.piracraft.api.Main;

public class Utils implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String arg2, String[] args) {
		Player p = (Player) s;
		if(cmd.getName().equalsIgnoreCase("hardcore")){
			com.hcp.utils.Utils.enviar(p, "lobby");
		}
		if(cmd.getName().equalsIgnoreCase("cc")){
			if(Main.isStaff.get(p)){
				for(int x = 0; x < 50; x++){
					Bukkit.broadcastMessage(" ");
				}
			}
		}
		return false;
	}

}
