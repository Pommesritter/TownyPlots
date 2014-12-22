package me.kleinerminer.townyplots.listeners;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class ClickListener implements Listener {
	TownyPlots plugin;
	public ClickListener(TownyPlots townyplots) {
		plugin = townyplots;
	}
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if(!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return; //Only if a block was Right clicked
		//If the player wants to register a chest, and is permitted to do so
		if(!playerRegisteringChest(event.getPlayer()))
			return;
		if(plugin.buildinghandler.getBuilding(event.getClickedBlock().getLocation()) == null) {
			event.getPlayer().sendMessage(plugin.lang("buildingNotFound"));
			unregisterPlayer(event.getPlayer());
			return;
		}
		//Check if the block is in the players town (must be mayor of course)
		try {
			if(!TownyUniverse.getTownBlock(event.getClickedBlock().getLocation()).getTown().getMayor().getName().equals(event.getPlayer().getName())) {
				event.getPlayer().sendMessage(plugin.lang("errorNoMayor"));
				unregisterPlayer(event.getPlayer());
				return;
			}
		} catch (NotRegisteredException e) {
			event.getPlayer().sendMessage(plugin.lang("errorNotInTown"));
			unregisterPlayer(event.getPlayer());
			return;
		}
		Building b = plugin.buildinghandler.getBuilding(event.getClickedBlock().getLocation());
		if(b == null) {
			event.getPlayer().sendMessage(plugin.lang("buildingNotFound")); 
			return;
		}
		if(b.getType().equals("lumberhut")) {
			int i = 0;
			for(;i > 39; i++) {
			}
			if(event.getClickedBlock().getType().equals(Material.CHEST)) {
				b.setOutputChests(i, event.getClickedBlock().getLocation());
				event.getPlayer().sendMessage(plugin.lang("chestRegistered"));
			} else {
				event.getPlayer().sendMessage(plugin.lang("noChest"));
			}
		}	
		if(b.getType().equals("mine")) {
			int i = 0;
			for(;i > 39; i++) {
			}
			if(event.getClickedBlock().getType().equals(Material.CHEST)) {
				b.setOutputChests(i, event.getClickedBlock().getLocation());
				event.getPlayer().sendMessage(plugin.lang("chestRegistered"));
			} else {
				event.getPlayer().sendMessage(plugin.lang("noChest"));
			}
		}	
		unregisterPlayer(event.getPlayer());
	}
	public Boolean playerRegisteringChest(Player player) {
		for(Player p : plugin.playersRegisteringChests) {
			if(p == player)
				return true;
		}
		return false;
	}
	private void unregisterPlayer(Player player) {
		int i2 = 0;
		for(Player p : plugin.playersRegisteringChests) {
			if(p == player) break;
			i2++;
		}
		plugin.playersRegisteringChests[i2] = null;	
	}
}
