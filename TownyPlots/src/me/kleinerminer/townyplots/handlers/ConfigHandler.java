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
		
		setConfigEntry("lumberhut.basicProducingTime", 40);
		setConfigEntry("lumberhut.producingTimeLevelStep", 3.5);
		setConfigEntry("lumberhut.lowestY", 40);
		setConfigEntry("lumberhut.highestY", 100);
		setConfigEntry("lumberhut.yRelative", 100);
		setConfigEntry("lumberhut.highestYRelativeToFloor", true);
		setConfigEntry("lumberhut.lowestYRelativeToFloor", true);
		
		setConfigEntry("mine.basicProducingTime", 10);
		setConfigEntry("mine.producingTimePerBlockDepth", 0.1);
		setConfigEntry("mine.depthPerBonusBlock", 7);
		setConfigEntry("mine.useProbabilitySystem", true);
		setConfigEntry("mine.gainableBlocks", "DIRT, GRAVEL, STONE, COAL_ORE, IRON_ORE, GOLD_ORE, DIAMOND_ORE");
		
		setConfigEntry("useTownyPlotSize", true);
		setConfigEntry("plotSize", 16);
		
		//Lang
		setConfigEntry("lang.wrongSyntax", "Syntax:");
		setConfigEntry("lang.levelInfo", "Level info for level");
		setConfigEntry("lang.maxLevel", "This is the maximum level.");
		setConfigEntry("lang.plotTypesAvailable", "Available plot types:");
		setConfigEntry("lang.commandPlayerOnly", "This command is for Players only!");
		setConfigEntry("lang.noPermission", "You dont have the permission to do that.");
		setConfigEntry("lang.errorNoMayor", "You must be the town Mayor for this.");
		setConfigEntry("lang.errorNotInTown", "You are not in a town.");
		setConfigEntry("lang.outputChestRemoved", "Output chest was removed successfully.");
		setConfigEntry("lang.noChest", "That is not a chest.");
		setConfigEntry("lang.buildingNotFound", "No building found.");
		setConfigEntry("lang.buildingFound", "This is already a building.");
		setConfigEntry("lang.plottypeSet", "Plottype set:");
		setConfigEntry("lang.lumberhut", "Lumberhut");
		setConfigEntry("lang.mine", "Mine");
		setConfigEntry("lang.blocks", "Blocks");
		setConfigEntry("lang.seconds", "Seconds");
		setConfigEntry("lang.levelInfo", "Level Info");
		setConfigEntry("lang.levelInfoMine", "Current Depth");
		setConfigEntry("lang.buildingReset", "You have successfully removed this");
		setConfigEntry("lang.registerChest", "Right click a chest to register it.");
		setConfigEntry("lang.specifyChestType", "Please specify a chest type");
		setConfigEntry("lang.chestRegistered", "Chest registered!");
		setConfigEntry("lang.noMoreBuildings", "No more buildings allowed on the server.");
		
		
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
