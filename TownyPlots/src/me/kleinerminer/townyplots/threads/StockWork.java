package me.kleinerminer.townyplots.threads;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Stock;

public class StockWork extends Thread {
	TownyPlots plugin;
	Stock stock;
	int productionCounter = 0;
	public StockWork(TownyPlots townyplots, Stock stock) {
		plugin = townyplots;
		this.stock = stock;
	}
	
	public void run() {
		while(true) {
		for(Building b : plugin.buildinghandler.getTownBuildings(stock.getTown())) {
			if(!b.getType().equals("stock")) {
				double dist = b.getLocation().distance(stock.getLocation());
				//Time (in milliseconds) the thread will sleep after the transfer, depending on the distance
				int time = (int) ((dist * plugin.config.getDouble("stock.timePerBlock")) * 1000);
				for(Location loc : b.getOutputChests()) {
					if(loc.getWorld().getBlockAt(loc).getType().equals(Material.CHEST)) {
						Chest c = (Chest) loc.getWorld().getBlockAt(loc).getState();
						stock.itemTransfer(c);
					}
				}
				//Wait on each building
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) { }
			}
		}
		try {
			Thread.sleep(plugin.threadSleepTime);
		} catch (InterruptedException e) {}
		
		}
	}
}
