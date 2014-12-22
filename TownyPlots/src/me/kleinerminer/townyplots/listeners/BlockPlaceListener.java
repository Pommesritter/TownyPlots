package me.kleinerminer.townyplots.listeners;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.handlers.BuildingHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
	private TownyPlots plugin;
	private BuildingHandler bhandler;
	public BlockPlaceListener(TownyPlots townyplots) {
		this.plugin = townyplots;
		bhandler = plugin.buildinghandler;
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Location loc = event.getBlock().getLocation();
		if(player.getLocation() == loc) {
			bhandler.equals(""); //TODO remove all stupid code
		}
	}
	
}
