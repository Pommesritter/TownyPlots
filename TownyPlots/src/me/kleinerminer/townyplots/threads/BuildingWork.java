package me.kleinerminer.townyplots.threads;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Farm;
import me.kleinerminer.townyplots.building.Lumberhut;
import me.kleinerminer.townyplots.building.Mine;
import me.kleinerminer.townyplots.building.SheepFarm;
import me.kleinerminer.townyplots.building.Stock;

public class BuildingWork extends Thread {
	TownyPlots plugin;
	Building b;
	int productionCounter = 0;
	double upkeepPerSecond;
	double upkeepUnpaid = 0;
	World world;
	public BuildingWork(TownyPlots townyplots, Building b) {
		plugin = townyplots;
		world = b.getWorld();
		this.b = b;
		upkeepPerSecond = plugin.config.getDouble(b.getType()+".hourlyUpkeep") / 3600;
	}
	
	public void run() {
		while(true) {
		plugin.buildinghandler.refreshSign(b.getInfoSign(), b);
		if(!b.isWorkCeased()) {
			upkeepUnpaid += upkeepPerSecond * (plugin.threadSleepTime / 1000);
			// TODO: originally: double econ = plugin.economy.getBalance(b.getTown().getEconomyName());
			plugin.debug("Economy name: " + b.getTown().getEconomyName());
			double econ = plugin.economy.getBalance(b.getTown().getEconomyName());
			if(econ < 1) {
				b.setIsWorkCeased(true);
			} else
			if(b instanceof Farm) {
				farmWork((Farm) b);
			} else
			if(b instanceof Lumberhut) {
				lumberhutWork((Lumberhut) b);
			} else 
			if(b instanceof Mine) {
				mineWork((Mine) b);
			} else 
			if(b instanceof SheepFarm) {
				sheepFarmWork((SheepFarm) b);
			} else
			if(b instanceof Stock) {
				stockWork((Stock) b);
			} else {
				if(upkeepUnpaid >= 0.5)
					
					//TODO: debug
					plugin.economy.bankWithdraw(b.getTown().getEconomyName(), upkeepUnpaid);
			}
		}
		try {
			Thread.sleep(plugin.threadSleepTime);
		} catch (InterruptedException e) {}}
	}
	private void farmWork(Farm farm) {
		farm.refreshLevel();
		if(b.getLevel() <= 0)
			b.setIsWorkCeased(true);
		if(farm.getHoeHealth() == 0)
		refreshHoeHealth: for(Location loc: farm.getChests("input")) {
			if(loc.getWorld().getBlockAt(loc).getType().equals(Material.CHEST)) {
				Chest c = (Chest) loc.getWorld().getBlockAt(loc).getState();
				if(c.getInventory().getContents() != null)
				for(ItemStack stack : c.getInventory().getContents()) {
					if(stack != null) {
					if(stack.getType().equals(Material.WOODEN_HOE)) {
						farm.setHoeHealth(20);
						break refreshHoeHealth;
					}
					if(stack.getType().equals(Material.STONE_HOE)) {
						farm.setHoeHealth(stack.getDurability());
						c.getInventory().remove(stack);
						break refreshHoeHealth;
					}
					if(stack.getType().equals(Material.IRON_HOE)) {
						farm.setHoeHealth(stack.getDurability());
						c.getInventory().remove(stack);
						break refreshHoeHealth;
					}
					if(stack.getType().equals(Material.GOLDEN_HOE)) {
						farm.setHoeHealth(stack.getDurability());
						c.getInventory().remove(stack);
						break refreshHoeHealth;
					}
					if(stack.getType().equals(Material.DIAMOND_HOE)) {
						farm.setHoeHealth(stack.getDurability());
						c.getInventory().remove(stack);
						break refreshHoeHealth;
					}
					}
				}
			}
		}
		//x Sec per wood on lv0 minus x sec per level
		int time = (int) (plugin.config.getInt("farm.basicFarmInterval") - 
				(farm.getLevel() * plugin.config.getDouble("farm.farmIntervalLevelStep")));
		if(productionCounter >= time && !farm.isWorkCeased()) {
			productionCounter = 0;
			farm.farm();
		} else {
			productionCounter += plugin.threadSleepTime / 1000;
		}
	}
	private void lumberhutWork(Lumberhut lumberhut) {
		lumberhut.refreshLevel();
		if(b.getLevel() <= 0)
			b.setIsWorkCeased(true);
		//x Sec per wood on lv0 minus x sec per level
		int time = (int) (plugin.config.getInt("lumberhut.basicProducingTime") - (lumberhut.getLevel() * 
				plugin.config.getDouble("lumberhut.producingTimeLevelStep")));
		//No producing on lv0
		if(productionCounter >= time && !lumberhut.isWorkCeased()) { 
		productionCounter = 0;
		for(Location loc : lumberhut.getChests("output")) {
			if(loc != null && loc.getBlock().getType().equals(Material.CHEST)) {
				Chest c= (Chest)loc.getWorld().getBlockAt(loc).getState();
				if(c.getBlockInventory().firstEmpty() != -1) { //If the chest is not full
					c.getBlockInventory().addItem(new ItemStack(Material.OAK_LOG, 1));
					break; //Only ONE chest should get Items ;)
				}
			}
		}
		} else {
			productionCounter += plugin.threadSleepTime / 1000;
		}
	}
	private void mineWork(Mine mine) {
		mine.refreshDepth();
		int depth = mine.getDepth();
		int time = plugin.config.getInt("mine.basicProducingTime") + 
				plugin.config.getInt("mine.producingTimePerBlockDepth")*mine.getDepth();
		//x Sec per Material on lv0 plus x sec per block of depth
		if(productionCounter >= time && !mine.isWorkCeased()) {
		productionCounter = 0;
		for(Location loc : mine.getChests("output")) {
			if(loc != null && loc.getBlock().getType().equals(Material.CHEST)) {
				Chest c= (Chest)loc.getWorld().getBlockAt(loc).getState();
				if(c.getBlockInventory().firstEmpty() != -1) { //If the chest is not full
					
					//When using the standart system for mining (mine existing blocks)
					if(plugin.config.getBoolean("mine.useProbabilitySystem") == false) {
					Material[] gainedBlocks = new Material[30];
					for(int i = 0; i < (mine.getDepth() / plugin.config.getInt("mine.depthPerBonusBlock"));i++) {
						gainedBlocks[i] = getRandomMaterial(mine);
						while(!plugin.config.getString("mine.gainableBlocks").contains(gainedBlocks[i].toString())) {
							gainedBlocks[i] = getRandomMaterial(mine);
						}
						if(gainedBlocks[i].equals(Material.COAL_ORE))
							gainedBlocks[i] = Material.COAL;
						if(gainedBlocks[i].equals(Material.DIAMOND_ORE))
							gainedBlocks[i] = Material.DIAMOND;
						if(gainedBlocks[i].equals(Material.STONE))
							gainedBlocks[i] = Material.COBBLESTONE;
					}
					if(mine.getEfficiency() == 0) break;
					int count = 1;
					if(new Random().nextInt(3)==0) count = 2;
					if(new Random().nextInt(7)==0) count = 3;
					count = (int) (mine.getEfficiency() / 50.0);
					for(Material m : gainedBlocks) {
						if(m == null) break;
						c.getBlockInventory().addItem(new ItemStack(m, count));
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
						if(mine.getEfficiency() > 0) {
						int count = 1;
						if(new Random().nextInt(3)==0) count = 2;
						if(new Random().nextInt(7)==0) count = 3;
						count = (int) (mine.getEfficiency() / 50.0);
						
						if(m != null)
							c.getBlockInventory().addItem(new ItemStack(m, count));
						}
					}
				}
				//Lower the efficiency
				Double eff = mine.getEfficiency();
				Double effLoss = plugin.config.getDouble("mine.efficiencyLossOnBlockGain");
				mine.setEfficiency((long) ((eff - effLoss) * 1e5) / 1e5);
				
			}
			}
		}
		} else {
			productionCounter += plugin.threadSleepTime / 1000;
		}
	}
	private void sheepFarmWork(SheepFarm sheepFarm) {
		sheepFarm.refreshLevel();
		if(b.getLevel() <= 0)
			b.setIsWorkCeased(true);
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
		int time = (int) (plugin.config.getInt("sheepfarm.basicProducingTime") - (sheepFarm.getLevel() * 
				plugin.config.getDouble("sheepfarm.producingTimeLevelStep")));
		if(productionCounter >= time && !sheepFarm.isWorkCeased()) { //x Sec per wood on lv0 minus x sec per level
		productionCounter = 0;
		for(Location loc : sheepFarm.getChests("output")) {
			if(loc != null && loc.getBlock().getType().equals(Material.CHEST)) {
				Chest c= (Chest)loc.getWorld().getBlockAt(loc).getState();
				if(c.getBlockInventory().firstEmpty() != -1) { //If the chest is not full
					if(sheepFarm.getBlockCountProduced() != 0)
					c.getBlockInventory().addItem(new ItemStack(Material.WHITE_WOOL, sheepFarm.getBlockCountProduced()));
					break; //Only ONE chest should get Items ;)
				}
			}
		}
		} else {
			productionCounter += plugin.threadSleepTime / 1000;
		}
	}
	private void stockWork(Stock stock) {
		for(Building b : plugin.buildinghandler.getTownBuildings(stock.getTown())) {
			if(!b.getType().equals("stock")) {
				double dist = b.getLocation().distance(stock.getLocation());
				//Time (in milliseconds) the thread will sleep after the transfer, depending on distance and road level
				int time = (int) ((dist * plugin.config.getDouble("stock.timePerBlock")) * 1000) / plugin.buildinghandler.getRoadConnectionLevel(stock, b);
				for(Location loc : b.getChests("output")) {
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
	}
	private Material getRandomMaterial(Mine mine) {
		Random random = new Random();
		int x = mine.getX() + random.nextInt(mine.getSize());
		int y = mine.getY() - random.nextInt(mine.getDepth());
		int z = mine.getZ() + random.nextInt(mine.getSize());
		World world = mine.getWorld();
		Location loc = new Location(world, x, y, z);
		return world.getBlockAt(loc).getType();


	}
	//Returns false if the material can not be taken from the town stock
	boolean getFromStock(Material mat) {
		Stock stock = null;
		townHasStock: {
			for(Building townB : plugin.buildinghandler.getTownBuildings(b.getTown())) {
				if(b.getType().equals("stock")) {
					stock = (Stock) townB;
					break townHasStock;
				}
			}
			return false;
		}
		if(!stock.materialChest.containsKey(mat.toString())) {
			return false;
		}
		if(!stock.materialChest.get(mat.toString()).getInventory().contains(new ItemStack(mat))) {
			return false;
		}
		return true;
	}
}
