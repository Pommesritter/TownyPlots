package me.kleinerminer.townyplots.listeners;

import java.util.Map.Entry;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Stock;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public class BlockBreakListener implements Listener {
	private TownyPlots plugin;
	public BlockBreakListener(TownyPlots townyplots) {
		this.plugin = townyplots;
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Building b = plugin.buildinghandler.getBuilding(event.getBlock().getLocation());
		if(b == null) return;
		try {
			if(!(plugin.townydatasource.getResident(event.getPlayer().getName()).isMayor()|| event.getPlayer().hasPermission("townyplots.admin"))) {
				event.getPlayer().sendMessage(plugin.lang("noPermission"));
				event.setCancelled(true);
				return;
			}
		} catch (NotRegisteredException e) { }	
		if(b instanceof Stock) {
			Stock stock = (Stock) b;
			if(!event.getBlock().getType().equals(Material.CHEST)) {
				return;
			}
			if(stock.materialChest.containsValue((Chest) event.getBlock().getState())) {
				for(Entry<Material, Chest> entry: stock.materialChest.entrySet()) {
					if(entry.getValue().equals(event.getBlock().getState())) {
						stock.materialChest.remove(entry.getKey());
						event.getPlayer().sendMessage(plugin.lang("stockChestRemoved") + ": " + entry.getKey());
						break;
					}
				}
			}
		}
		if(b.getChests("input") != null)
			if(b.getChests("input").contains(event.getBlock().getLocation())) {
				b.getChests("input").remove(event.getBlock().getLocation());
				event.getPlayer().sendMessage(plugin.lang("inputChestRemoved"));
				return;
			}
		if(b.getChests("output") != null)
			if(b.getChests("output").contains(event.getBlock().getLocation())) {
				b.getChests("output").remove(event.getBlock().getLocation());
				event.getPlayer().sendMessage(plugin.lang("outputChestRemoved"));
				return;
		}
		if(b.getChests("level") != null)
			if(b.getChests("level").contains(event.getBlock().getLocation())) {
				b.getChests("level").remove(event.getBlock().getLocation());
				event.getPlayer().sendMessage(plugin.lang("levelChestRemoved"));
				return;
		}
		return;
			
	}
	
}
