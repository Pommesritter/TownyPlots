package me.kleinerminer.townyplots.handlers;

import me.kleinerminer.townyplots.TownyPlots;

public class ConfigHandler {
	private TownyPlots plugin;
	public ConfigHandler(TownyPlots townyplots) {
		this.plugin = townyplots;
	}
	public void refreshConfig() {
		//Version 0.1 config entries
		
		//Config 
		//level 1 settings
		setConfigEntry("lumberhut.levels.1.WOOD", 100);
		setConfigEntry("sheepfarm.levels.1.WOOD", 120);
		setConfigEntry("farm.levels.1.WOOD", 100);
		
		setConfigEntry("lumberhut.basicProducingTime", 40);
		setConfigEntry("lumberhut.producingTimeLevelStep", 3.5);
		setConfigEntry("lumberhut.lowestY", 40);
		setConfigEntry("lumberhut.highestY", 100);
		setConfigEntry("lumberhut.yRelative", 10);
		setConfigEntry("lumberhut.highestYRelativeToFloor", true);
		setConfigEntry("lumberhut.lowestYRelativeToFloor", true);
		setConfigEntry("lumberhut.hourlyUpkeep", 30.0);
		
		setConfigEntry("sheepfarm.basicProducingTime", 40);
		setConfigEntry("sheepfarm.producingTimeLevelStep", 5.0);
		setConfigEntry("sheepfarm.sheepsPerLevel", 5);
		setConfigEntry("sheepfarm.sheepScanRadiusBasic", 11);
		setConfigEntry("sheepfarm.sheepScanRadiusPerLevel", 2);
		setConfigEntry("sheepfarm.lowestY", 40);
		setConfigEntry("sheepfarm.highestY", 100);
		setConfigEntry("sheepfarm.yRelative", 10);
		setConfigEntry("sheepfarm.highestYRelativeToFloor", true);
		setConfigEntry("sheepfarm.lowestYRelativeToFloor", true);
		setConfigEntry("sheepfarm.hourlyUpkeep", 50.0);
		
		setConfigEntry("mine.basicProducingTime", 10);
		setConfigEntry("mine.producingTimePerBlockDepth", 0.1);
		setConfigEntry("mine.depthPerBonusBlock", 7);
		setConfigEntry("mine.efficiencyLossOnBlockGain", 0.1);
		setConfigEntry("mine.useProbabilitySystem", true);
		setConfigEntry("mine.gainableBlocks", "DIRT, GRAVEL, STONE, COAL_ORE, IRON_ORE, GOLD_ORE, DIAMOND_ORE");
		setConfigEntry("mine.hourlyUpkeep", 60.0);

		setConfigEntry("farm.basicFarmInterval", 60);
		setConfigEntry("farm.farmIntervalLevelStep", 5.0);
		setConfigEntry("farm.fieldExpansionBasic", -1 );
		setConfigEntry("farm.fieldExpansionPerLevel", 3);
		setConfigEntry("farm.lowestY", 40);
		setConfigEntry("farm.highestY", 100);
		setConfigEntry("farm.yRelative", 10);
		setConfigEntry("farm.highestYRelativeToFloor", true);
		setConfigEntry("farm.lowestYRelativeToFloor", true);
		setConfigEntry("farm.hourlyUpkeep", 40.0);
		
		setConfigEntry("stock.timePerBlock", 0.25);
		
		setConfigEntry("useDependencySystem", true);
		setConfigEntry("threadSleepTimeMilliseconds", 1000);
		
		//Lang
		setConfigEntry("lang.wrongSyntax", "Syntax:");
		setConfigEntry("lang.blocksNeeded", "Blocks Needed for level");
		setConfigEntry("lang.maxLevel", "This is the maximum level.");
		setConfigEntry("lang.plotTypesAvailable", "Available plot types:");
		setConfigEntry("lang.commandPlayerOnly", "This command is for Players only!");
		setConfigEntry("lang.noPermission", "You are not permitted to do this.");
		setConfigEntry("lang.errorNoMayor", "You must be the town Mayor for this.");
		setConfigEntry("lang.errorNotInTown", "You are not in a town.");
		setConfigEntry("lang.outputChestRemoved", "Output chest was removed successfully.");
		setConfigEntry("lang.inputChestRemoved", "Input chest was removed successfully.");
		setConfigEntry("lang.levelChestRemoved", "Level chest was removed successfully.");
		setConfigEntry("lang.noChest", "That is not a chest.");
		setConfigEntry("lang.buildingNotFound", "No building found.");
		setConfigEntry("lang.buildingFound", "This is already a building.");
		setConfigEntry("lang.plottypeSet", "Plottype set:");
		setConfigEntry("lang.dependentBuilding", "Your town is missing a");
		setConfigEntry("lang.dependentTownLevel", "This building requires town level");
		setConfigEntry("lang.levelInfoMine", "Current Depth");
		setConfigEntry("lang.buildingReset", "You have successfully removed this");
		setConfigEntry("lang.chestNotAppliable", "This kind of building does not support this chest type");
		setConfigEntry("lang.registerChest", "Right click a chest to register it.");
		setConfigEntry("lang.specifyChestType", "Please specify a chest type");
		setConfigEntry("lang.chestRegistered", "Chest registered!");
		setConfigEntry("lang.invalidMaterial", "Please specify a valid Material");
		setConfigEntry("lang.signNotOnChest", "You must attach the sign on a chest. Tip: Sneak");
		setConfigEntry("lang.onlyOneStock", "Only one stock is allowed per town.");
		setConfigEntry("lang.stockChestSet", "Stock chest successfully set for Material");
		setConfigEntry("lang.stockChestRemoved", "Stock chest was removed successfully");
		setConfigEntry("lang.workCeased", "Building work stopped.");
		setConfigEntry("lang.workContinues", "This building will continue work now.");
		setConfigEntry("lang.alreadyCeased", "This building is not working at the moment.");
		setConfigEntry("lang.alreadyWorking", "This building is already working.");
		setConfigEntry("lang.cantWorkFunds", "Your town has not enough money to maintain this building");
		 
		setConfigEntry("lang.depth", "Depth");
		setConfigEntry("lang.blocks", "Block");
		setConfigEntry("lang.seconds", "Seconds");
		setConfigEntry("lang.levelInfo", "Level Info");
		setConfigEntry("lang.efficiency", "Eff.");
		setConfigEntry("lang.radius", "Radius");
		setConfigEntry("lang.sheeps", "Sheeps");
		setConfigEntry("lang.sheepsForProduction", "Sheeps required for producing wool:");
		setConfigEntry("lang.hoeHealth", "Hoe health");
		setConfigEntry("lang.yes", "Yes");
		setConfigEntry("lang.no", "No");
		setConfigEntry("lang.working", "Working");
		setConfigEntry("lang.costsPerHour", "Hourly upkeep");
		
		setConfigEntry("lang.lumberhut", "Lumberjacks hut");
		setConfigEntry("lang.mine", "Mine");
		setConfigEntry("lang.sheepfarm", "Sheep Farm");
		setConfigEntry("lang.stock", "Stock");
		setConfigEntry("lang.farm", "Wheat Farm");
		
	}
	private void setConfigEntry(String entry, int value) {
		if (!plugin.config.contains(entry)) {
			plugin.config.set(entry, value);
		}
	}
	private void setConfigEntry(String entry, double value) {
		if (!plugin.config.contains(entry)) {
			plugin.config.set(entry, value);
		}
	}
	
	private void setConfigEntry(String entry, String value) {
		if (!plugin.config.contains(entry)) {
			plugin.config.set(entry, value);
		}
	}
	private void setConfigEntry(String entry, Boolean value) {
		if (!plugin.config.contains(entry)) {
			plugin.config.set(entry, value);
		}
	}
}
