package me.kleinerminer.townyplots.listeners;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.handlers.BuildingHandler;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeListener implements Listener {
	private TownyPlots plugin;
	private BuildingHandler bhandler;
	public SignChangeListener(TownyPlots townyplots) {
		this.plugin = townyplots;
		bhandler = plugin.buildinghandler;
	}
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		Location loc = event.getBlock().getLocation();
		if(bhandler.getBuilding(loc) == null || 
				!bhandler.getBuilding(loc).getTown().getMayor().getName().equals(player.getName())) return;
		Building b = bhandler.getBuilding(loc);
		if(event.getLine(1).equals("[binfo]")) {
			Sign sign = (Sign) event.getBlock().getState();
			b.setInfoSign(sign);
		}
		//TODO: Register all kinds of signs, setting signs, chest signs etc.
	
	}
}

