package me.kleinerminer.townyplots;

import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Mine;
import me.kleinerminer.townyplots.building.SheepFarm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class CE_building implements CommandExecutor {
	private TownyPlots plugin;
	public CE_building(TownyPlots townyplots) {
		this.plugin = townyplots;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1) { 
			//Argument count must be 1
			sender.sendMessage(plugin.lang("wrongSyntax") + " /b [info/addchest]");
			return true;
		}
		if(!(sender instanceof Player)) {
			//Command sender must be a player
			sender.sendMessage(plugin.lang("commandPlayerOnly"));
			return true;
		}
		Player player = (Player) sender;
		TownBlock plot = getPlot(player);
		Building building = plugin.buildinghandler.getBuilding(player.getLocation());
		if(building == null){
			sender.sendMessage(plugin.lang("buildingNotFound"));
			return true;
		}
		try {
			if(!TownyUniverse.getPlayer(plot.getTown().getMayor()).equals(player)) {
				//If player is not the mayor of the town of the plot he is standing in
				sender.sendMessage(plugin.lang("errorNoMayor"));
				return true;
			}
		} catch (NotRegisteredException e) {
			//If the player is not in a plot which belongs to a town
			sender.sendMessage(plugin.lang("errorNotInTown"));
			return true;
		} catch (NullPointerException e) {
			//If the player is not in a plot which belongs to a town
			sender.sendMessage(plugin.lang("errorNotInTown"));
			return true;
		} catch (TownyException e) {
			sender.sendMessage(plugin.lang("errorNoMayor"));
			return true;
		}
		if(!player.hasPermission("townyplots.building." + args[0])) {
			//Permission Check (for Plot Type)
			sender.sendMessage(plugin.lang("noPermission"));
			return true;
		}
		if(args[0].equalsIgnoreCase("addchest")){
			if(args.length < 2) { //When args are wrong
				sender.sendMessage(plugin.lang("specifyChestType")+":");
				sender.sendMessage("[input / output / level]");
				return true;
			}
			//To undestand the following: A chest ArrayList is not initialized if the chestType is unused.
			if(building.getChests(args[1]) != null) {
				plugin.playersRegisteringChests.put(player, "input");
				sender.sendMessage(plugin.lang("registerChest"));
				return true;
			} else {
				sender.sendMessage(plugin.lang("chestNotAppliable")+ ".");
				return true;
				}
		} else
		if(args[0].equalsIgnoreCase("info")){
			sender.sendMessage(plugin.heading(plugin.config.getString("lang."+ building.getType())));
			sender.sendMessage("ID: "+ building.getId());
			if(!building.getLevelInfo().equals(""))
				sender.sendMessage(building.getLevelInfo());
			if(building.isWorkCeased())
				sender.sendMessage(plugin.config.getString("lang.working")+": "+ plugin.config.getString("lang.no"));
			else
				sender.sendMessage(plugin.config.getString("lang.working")+": "+ plugin.config.getString("lang.yes"));
			if(building instanceof SheepFarm) {
				SheepFarm sf = (SheepFarm) building;
				sender.sendMessage(plugin.config.getString("lang.sheeps") + ": " + sf.getSheepCount()+"/"+plugin.config.getInt("sheepfarm.sheepsPerLevel") * sf.getLevel());
				sender.sendMessage(plugin.config.getString("lang.radius") + ": " + sf.getRadius());
				sender.sendMessage(plugin.config.getString("lang.sheeps")+": "+sf.getSheepCount()); 
			} else if(building instanceof Mine) {
				Mine m = (Mine) building;
				sender.sendMessage(plugin.config.getString("lang.efficiency") + ": " + m.getEfficiency()+"%"); 
			}
		return true;
		} else
		if(args[0].equalsIgnoreCase("work")) {
			if(args.length <= 1) {
				sender.sendMessage(plugin.lang("wrongSyntax") + " /b work [cease/resume]");
				return true;
			}	
			if(args[1].equalsIgnoreCase("cease")) {
				if(building.isWorkCeased()) {
					sender.sendMessage(plugin.lang("alreadyCeased"));
					return true;
				}
				sender.sendMessage(plugin.lang("workCeased"));
				building.setIsWorkCeased(true);
				return true;
			} else
			if(args[1].equalsIgnoreCase("resume")) {
				if(!building.isWorkCeased()) {
					sender.sendMessage(plugin.lang("alreadyWorking"));
					return true;
				}
				sender.sendMessage(plugin.lang("workContinues"));
				building.setIsWorkCeased(false);
				return true;
			} else {
				sender.sendMessage(plugin.lang("wrongSyntax") + " /b work [cease/resume]");
				return true;
			}
		}
		
		sender.sendMessage(plugin.lang("wrongSyntax") + " /b [info/addchest/work]");
		return true;
	}
	private TownBlock getPlot(Player player) {
		return TownyUniverse.getTownBlock(player.getLocation());
	}
}
