package me.kleinerminer.townyplots.building;


import java.util.ArrayList;

import me.kleinerminer.townyplots.TownyPlots;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;

import com.palmergames.bukkit.towny.object.Town;

public class Mine extends Building {
	private ArrayList<Location> outputChests = new ArrayList<Location>(); //Location of chests to add items to
	private ArrayList<Location> inputChests = null; //Location of chests to get items From, unused!
	private ArrayList<Location> levelChests = null; //Location of chests to get the blocks for next level
	
	int depth = 0;
	int y;
	int y2; //Flexible y-coord (will move downwards on lv up
	int ymin = 5;
	int size;
	Town town;
	World world;
	Sign infoSign;
	Thread work;
	int id;
	double efficiency = 100;
	private boolean isWorkCeased = false;
	String type = "mine";
	// World world;
	private TownyPlots plugin;
	public Mine(Location loc, Town town, int id, TownyPlots townyplots) {
		super((int) loc.getX(), (int) loc.getZ(), loc.getWorld(), id);
		this.plugin = townyplots;
		this.x = (int) loc.getX();
		this.z = (int) loc.getZ();
		y = (int) (loc.getWorld().getHighestBlockAt(loc).getLocation().getY() - 8);
		if(y > 100) y = y-25;
		y2 = y;
		int size = plugin.plotSize - 1;
		setX2(x + size);
		setZ2(z + size);
		this.town = town;
		setWorld(loc.getWorld());
		setId(id);
		setSize(size);
		work = plugin.buildinghandler.startWork(this);
	}
	


	public void refreshDepth() {
		y2 = y;
		depth = 0;
		Location l1 = new Location(world, x, y2, z);
		Location l2 = new Location(world, x2, y2, z2);
		while(plugin.buildinghandler.countBlocks(l1, l2, Material.RAILS) > 0) {
			y2--;
			depth++;
			l1 = new Location(world, x, y2, z);
			l2 = new Location(world, x2, y2, z2);
			if(y2 == ymin) break;
		}
		System.gc(); //Remove all the locations
	}
	public void setEfficiency(double d) {
		efficiency = d;
	}
	public double getEfficiency() {
		return efficiency;
	}
	@Override
	public String getLevelInfo() {
		int depth = y - y2;
		return (lang(plugin.config.getString("lang.levelInfoMine") + ": " + depth));
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	@Override
	public int getZ() {
		return this.z;
	}
	@Override
	public int getX2() {
		return this.x2;
	}
	@Override
	public int getZ2() {
		return this.z2;
	}
	@Override
	public int getSize() {
		return this.size;
	}
	@Override
	public String getType() {
		return this.type;
	}
	@Override
	public Town getTown() {
		return this.town;
	}
	@Override
	public World getWorld() {
		return this.world;
	}
	@Override
	public int getId() {
		return this.ID;
	}
	@Override
	public void setX(int x) {
		this.x = x;
	}
	@Override
	public void setZ(int z) {
		this.z = z;
	}
	@Override
	public void setX2(int x2) {
		this.x2 = x2;
	}
	@Override
	public void setZ2(int z2) {
		this.z2 = z2;
	}
	@Override
	public void setSize(int size) {
		this.size = size;
	}
	@Override
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public void setTown(Town town) {
		this.town = town;
	}
	@Override
	public void setWorld(World world) {
		this.world = world;
	}
	@Override
	public void setId(int id) {
		this.ID = id;
	}
	@Override
	public ArrayList<Location> getChests(String type) {
		if(type.equals("input")) {
			return inputChests;
		}
		if(type.equals("output")) {
			return outputChests;
		}
		if(type.equals("level")) {
			return levelChests;
		}
		return null;
	}
	@Override
	public void addChest(String type, Location loc) {
		if(type.equals("input")) {
			this.inputChests.add(loc);
			return;
		}
		if(type.equals("output")) {
			outputChests.add(loc);
			return;
		}
		if(type.equals("level")) {
			levelChests.add(loc);
		}
	}
	@Override
	public void setChests(String type, ArrayList<Location> chests) {
		if(type.equals("input")) {
			this.inputChests = chests;
			return;
		}
		if(type.equals("output")) {
			outputChests = chests;
			return;
		}
		if(type.equals("level")) {
			levelChests = chests;
		}
		
	}
	@Override
	public int getLevel() {
		return y - y2;
	}
	private String lang(String s) {
		return "["+plugin.config.getString("lang.mine")+"] " + s;
	}
	public int getDepth() {
		return depth;
	}
	public int getY() {
		return y;
	}
	public int getY2() {
		return y2;
	}
	@Override
	public void setY(int y) {
		this.y = y;
	}
	@Override
	public Sign getInfoSign() {
		return infoSign;
	}
	@Override
	public void setInfoSign(Sign sign) {
		infoSign = sign;
	}
	@Override
	public Location getLocation() {
		return new Location(world,x,y,z);
	}
	@Override
	public Thread getThread() {
		return work;
	}
	@Override
	public boolean isWorkCeased() {
		return isWorkCeased;
	}

	@Override
	public void setIsWorkCeased(boolean bool) {
		isWorkCeased = bool;
	}
}
