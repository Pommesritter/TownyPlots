package me.kleinerminer.townyplots.listeners;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;

import org.bukkit.Location;
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
		//Only if a block was Right clicked
		if(!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
		//If the player wants to register a chest, and is permitted to do so
		if(playerRegisteringChest(event.getPlayer()) == null)
			return;
		//Check if the block is in the players town (must be mayor of course)
		if(plugin.buildinghandler.getBuilding(event.getClickedBlock().getLocation()) == null) {
			event.getPlayer().sendMessage(plugin.lang("buildingNotFound"));
			plugin.playersRegisteringChests.remove(event.getPlayer());
			return;
		}
		try {
			if(!TownyUniverse.getTownBlock(event.getClickedBlock().getLocation()).getTown().getMayor().getName().equals(event.getPlayer().getName())) {
				event.getPlayer().sendMessage(plugin.lang("errorNoMayor"));
				plugin.playersRegisteringChests.remove(event.getPlayer());
				return;
			}
		} catch (NotRegisteredException e) {
			event.getPlayer().sendMessage(plugin.lang("errorNotInTown"));
			plugin.playersRegisteringChests.remove(event.getPlayer());
			return;
		}
		Building b = plugin.buildinghandler.getBuilding(event.getClickedBlock().getLocation());
		if(b == null) {
			return;
		}
		if(!event.getClickedBlock().getType().equals(Material.CHEST)) {
			event.getPlayer().sendMessage(plugin.lang("noChest"));
			plugin.playersRegisteringChests.remove(event.getPlayer());
		    return;
		}
		registerChest(b, event.getClickedBlock().getLocation(), event.getPlayer());
		plugin.playersRegisteringChests.remove(event.getPlayer());
	}
	
	
	private void registerChest(Building b, Location loc, Player player) {
		if(playerRegisteringChest(player).equals("input")) {
			b.addChest("input", loc);
		} else
		if(playerRegisteringChest(player).equals("output")) {
			b.addChest("output", loc);
		} else
		if(playerRegisteringChest(player).equals("level")) {
			b.addChest("level", loc);
		}
		player.sendMessage(plugin.lang("chestRegistered"));
		
	}
	private String playerRegisteringChest(Player player) {
		return plugin.playersRegisteringChests.get(player);
	}
}
