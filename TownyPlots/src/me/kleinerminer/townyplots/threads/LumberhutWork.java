package me.kleinerminer.townyplots.threads;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Lumberhut;

public class LumberhutWork extends Thread {
	TownyPlots plugin;
	Lumberhut lumberhut;
	int productionCounter = 0;
	public LumberhutWork(TownyPlots townyplots, Lumberhut lumberhut) {
		plugin = townyplots;
		this.lumberhut = lumberhut;
	}
	
	public void run() {
		while(true) {
		lumberhut.refreshLevel();
		plugin.buildinghandler.refreshSign(lumberhut.getInfoSign(), lumberhut);
		if(lumberhut.getLevel() != 0) //No producing on lv0
		if(!(productionCounter < plugin.config.getInt("lumberhut.basicProducingTime") - (lumberhut.getLevel() * 
				plugin.config.getDouble("lumberhut.producingTimeLevelStep")))) { //x Sec per wood on lv0 minus x sec per level
		productionCounter = 0;
		for(Location loc : lumberhut.getOutputChests()) {
			if(loc != null && loc.getBlock().getType().equals(Material.CHEST)) {
				Chest c= (Chest)loc.getWorld().getBlockAt(loc).getState();
				if(c.getBlockInventory().firstEmpty() != -1) { //If the chest is not full
					c.getBlockInventory().addItem(new ItemStack(Material.LOG, 1));
					break; //Only ONE chest should get Items ;)
				}
			}
		}
		} else {
			productionCounter++;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		}
	}
}
