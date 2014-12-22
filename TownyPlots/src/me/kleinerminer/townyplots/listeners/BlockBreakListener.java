package me.kleinerminer.townyplots.listeners;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;

import org.bukkit.Location;
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
		for(Building b : plugin.buildings){
			if(b == null) break;
			int counter = 0;
			if(b.getOutputChests() != null)
			for(Location loc : b.getOutputChests()){
				if(loc == null) break;
				if(loc == event.getBlock().getLocation()) {
				try {
				if(plugin.townydatasource.getResident(event.getPlayer().getName()).isMayor() 
						|| event.getPlayer().hasPermission("townyplots.admin")) {
					b.getOutputChests()[counter] = null; //Unregister Chest by null
					//Fill gaps in Array
					 do {
						 b.setOutputChests(counter, b.getOutputChests()[counter+1]);
						counter++;
					} while(b.getOutputChests()[counter] != null);
					 b.setOutputChests(counter + 1, null);
					 event.getPlayer().sendMessage(plugin.lang("outputChestRemoved"));
					return;
				} else {
					event.setCancelled(true);
				}
				} catch (NotRegisteredException e) {}
				}
				counter++;
			}
		
		}
	}
	
}
