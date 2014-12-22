package me.kleinerminer.townyplots.threads;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
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
		if(lumberhut.getInfoSign() != null && lumberhut.getInfoSign().getBlock().getType() == Material.SIGN) {
			plugin.getLogger().info("SCHNITZEL SCHNITZEL"); //TODO remove
			Sign sign = (Sign) lumberhut.getInfoSign().getBlock().getState();
			sign.setLine(0, plugin.config.getString("lang.lumberhut")); //Line 1 is the plot type
			sign.setLine(1, "ID: " + lumberhut.getId()); //Line 2 is the id
			sign.setLine(2, "Level: " + lumberhut.getLevel()); //Line 3 is the level
			sign.setLine(2, "1 : " + plugin.config.getString("lang.blocks")+"/"+ (plugin.config.getInt("lumberhut.basicProducingTime") - (lumberhut.getLevel() * 
					plugin.config.getDouble("lumberhut.producingTimeLevelStep"))) +
					plugin.config.getString("lang.seconds")); //Line 4 is the BlockCount/Seconds
		}
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
