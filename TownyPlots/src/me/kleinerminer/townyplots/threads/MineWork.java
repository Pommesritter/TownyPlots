package me.kleinerminer.townyplots.threads;


import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Mine;

public class MineWork extends Thread {
	TownyPlots plugin;
	Mine mine;
	int productionCounter = 0;
	public MineWork(TownyPlots townyplots, Mine mine) {
		plugin = townyplots;
		this.mine = mine;
	}
	
	public void run() {
		while(true) {
		mine.refreshDepth();
		int depth = mine.getDepth();
		if(!(productionCounter < plugin.config.getInt("mine.basicProducingTime") + 
				//x Sec per Material on lv0 plus x sec per block of depth
				plugin.config.getInt("mine.producingTimePerBlockDepth")*mine.getDepth())) { 
		productionCounter = 0;
		for(Location loc : mine.getOutputChests()) {
			if(loc != null && loc.getBlock().getType().equals(Material.CHEST)) {
				Chest c= (Chest)loc.getWorld().getBlockAt(loc).getState();
				if(c.getBlockInventory().firstEmpty() != -1) { //If the chest is not full
					
					//When using the standart system for mining (mine existing blocks)
					if(plugin.config.getBoolean("mine.useProbabilitySystem") == false) {
					Material[] gainedBlocks = new Material[30];
					for(int i = 0; i < (mine.getDepth() / plugin.config.getInt("mine.depthPerBonusBlock"));i++) {
						gainedBlocks[i] = getRandomMaterial();
						while(!plugin.config.getString("mine.gainableBlocks").contains(gainedBlocks[i].toString())) {
							gainedBlocks[i] = getRandomMaterial();
						}
						if(gainedBlocks[i].equals(Material.COAL_ORE))
							gainedBlocks[i] = Material.COAL;
						if(gainedBlocks[i].equals(Material.DIAMOND_ORE))
							gainedBlocks[i] = Material.DIAMOND;
						if(gainedBlocks[i].equals(Material.STONE))
							gainedBlocks[i] = Material.COBBLESTONE;
					}
					for(Material m : gainedBlocks) {
						if(m == null) break;
						c.getBlockInventory().addItem(new ItemStack(m));
					}
					break; //Only ONE chest should get Items ;) 
					
					//When using the probability mining system
				} else {
					HashMap<Material, Double> blockProbability = new HashMap<Material, Double>();
					Material[] mineables = {Material.DIAMOND,Material.OBSIDIAN, Material.REDSTONE, Material.GOLD_ORE, 
							Material.IRON_ORE, Material.COAL, Material.COBBLESTONE};
					blockProbability.put(Material.DIAMOND, 0.06 * depth);
					blockProbability.put(Material.OBSIDIAN, 0.1 * depth);
					blockProbability.put(Material.REDSTONE, 0.2 * depth);
					blockProbability.put(Material.GOLD_ORE, 0.4 * depth);
					blockProbability.put(Material.IRON_ORE, 0.7 * depth);
					blockProbability.put(Material.COAL, 1.0 * depth);
					blockProbability.put(Material.COBBLESTONE, 1.8 * depth);
					Material[] minedOres = new Material[10];
					int i = 0;
					for(Material m : mineables) {
						double probability = blockProbability.get(m);
						if((100 - probability) <= 0)  {
							minedOres[i] = m;
							i++;
							if(new Random().nextInt((int)(200 - probability))==0){
								minedOres[i] = m;
								i++; 
							}
						} else
						if(new Random().nextInt((int)(100 - probability))==0){
							minedOres[i] = m;
							i++; 
						}
					} 
					for(Material m : minedOres) {
						int count = 1;
						if(new Random().nextInt(3)==0) count = 2;
						if(new Random().nextInt(7)==0) count = 3;
						if(m != null)
							c.getBlockInventory().addItem(new ItemStack(m, count));
					}
				}
				
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
	//Gets the Material of a random Block within the mine's bounds
	private Material getRandomMaterial() {
		Random random = new Random();
		int x = mine.getX() + random.nextInt(mine.getSize());
		int y = mine.getY() - random.nextInt(mine.getDepth());
		int z = mine.getZ() + random.nextInt(mine.getSize());
		World world = mine.getWorld();
		Location loc = new Location(world, x, y, z);
		return world.getBlockAt(loc).getType();


	}
}
