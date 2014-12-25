package me.kleinerminer.townyplots.threads;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Farm;

public class FarmWork extends Thread {
	TownyPlots plugin;
	Farm farm;
	int productionCounter = 0;
	public FarmWork(TownyPlots townyplots, Farm farm) {
		plugin = townyplots;
		this.farm = farm;
	}
	
	public void run() {
		while(true) {
		farm.refreshLevel();
		plugin.buildinghandler.refreshSign(farm.getInfoSign(), farm);
		if(farm.getHoeHealth() == 0)
		refreshHoeHealth: for(Location loc: farm.getInputChests()) {
			if(loc.getWorld().getBlockAt(loc).getType().equals(Material.CHEST)) {
				Chest c = (Chest) loc.getWorld().getBlockAt(loc).getState();
				if(c.getInventory().getContents() != null)
				for(ItemStack stack : c.getInventory().getContents()) {
					if(stack != null) {
					if(stack.getType().equals(Material.WOOD_HOE)) {
						farm.setHoeHealth(20);
						break refreshHoeHealth;
					}
					if(stack.getType().equals(Material.STONE_HOE)) {
						farm.setHoeHealth(90);
						c.getInventory().remove(stack);
						break refreshHoeHealth;
					}
					if(stack.getType().equals(Material.IRON_HOE)) {
						farm.setHoeHealth(150);
						c.getInventory().remove(stack);
						break refreshHoeHealth;
					}
					if(stack.getType().equals(Material.GOLD_HOE)) {
						farm.setHoeHealth(300);
						c.getInventory().remove(stack);
						break refreshHoeHealth;
					}
					if(stack.getType().equals(Material.DIAMOND_HOE)) {
						farm.setHoeHealth(1000);
						c.getInventory().remove(stack);
						break refreshHoeHealth;
					}
					}
				}
			}
		}
		if(farm.getLevel() != 0) //No producing on lv0
		if(!(productionCounter < plugin.config.getInt("farm.basicProducingTime") - (farm.getLevel() * 
				plugin.config.getDouble("farm.producingTimeLevelStep")))) { //x Sec per wood on lv0 minus x sec per level
			productionCounter = 0;
			farm.farm();
		} else {
			productionCounter += plugin.threadSleepTime / 1000;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		}
	}
}
