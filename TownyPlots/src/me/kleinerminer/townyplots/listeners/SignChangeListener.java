package me.kleinerminer.townyplots.listeners;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Stock;
import me.kleinerminer.townyplots.handlers.BuildingHandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Sign;

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
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign) event.getBlock().getState();
			b.setInfoSign(sign);
		}
		if(b instanceof Stock) {
			Stock stock = (Stock) b;
			if(event.getLine(1).equalsIgnoreCase("[Stock]")) {
				if(event.getBlock().getType().equals(Material.SIGN_POST)) {
					event.getPlayer().sendMessage(plugin.lang("signNotOnChest"));
					return;
				}
				Sign sign = (Sign) event.getBlock().getState().getData();;
				if(!event.getBlock().getRelative(sign.getAttachedFace()).getType().equals(Material.CHEST)) {
					event.getPlayer().sendMessage(plugin.lang("signNotOnChest"));
					return;
				}
				Chest chest = (Chest) event.getBlock().getRelative(sign.getAttachedFace()).getState();
				String mat = (event.getLine(2).toUpperCase());
				if(Material.getMaterial(mat) != null) {
					Material m = Material.getMaterial(mat);
					stock.materialChest.put(m, chest);
					event.getPlayer().sendMessage(plugin.lang("stockChestSet") + " " +event.getLine(2)+".");
				} else {
					event.getPlayer().sendMessage(plugin.lang("invalidMaterial"));
				}
			}
		}
		//TODO: Register all kinds of signs, setting signs, chest signs etc.
	
	}
}

