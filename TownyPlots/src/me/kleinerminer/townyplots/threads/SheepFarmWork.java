package me.kleinerminer.townyplots.threads;


import java.util.ConcurrentModificationException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.SheepFarm;

public class SheepFarmWork extends Thread {
	TownyPlots plugin;
	SheepFarm sheepFarm;
	int productionCounter = 0;
	public SheepFarmWork(TownyPlots townyplots, SheepFarm sheepFarm) {
		plugin = townyplots;
		this.sheepFarm = sheepFarm;
	}
	
	public void run() {
		while(true) {
		sheepFarm.refreshLevel();
		plugin.buildinghandler.refreshSign(sheepFarm.getInfoSign(), sheepFarm);
		double radius = sheepFarm.getRadius();
		int sheepCount = 0;
		try {
		List<LivingEntity> near = sheepFarm.getWorld().getLivingEntities();
		for(LivingEntity e : near) {
			if(e.getLocation().distance(sheepFarm.center) <= radius) 
				sheepCount++;
		}
		if(sheepCount > 0) sheepCount -= 1;
		sheepFarm.setSheepCount(sheepCount);
		} catch (ConcurrentModificationException e) {}
		if(sheepFarm.getLevel() != 0) //No producing on lv0
		if(!(productionCounter < plugin.config.getInt("sheepfarm.basicProducingTime") - (sheepFarm.getLevel() * 
				plugin.config.getDouble("sheepfarm.producingTimeLevelStep")))) { //x Sec per wood on lv0 minus x sec per level
		productionCounter = 0;
		for(Location loc : sheepFarm.getOutputChests()) {
			if(loc != null && loc.getBlock().getType().equals(Material.CHEST)) {
				Chest c= (Chest)loc.getWorld().getBlockAt(loc).getState();
				if(c.getBlockInventory().firstEmpty() != -1) { //If the chest is not full
					if(sheepFarm.getBlockCountProduced() != 0)
					c.getBlockInventory().addItem(new ItemStack(Material.WOOL, sheepFarm.getBlockCountProduced()));
					break; //Only ONE chest should get Items ;)
				}
			}
		}
		} else {
			productionCounter += plugin.threadSleepTime / 1000;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		}
	}
}
