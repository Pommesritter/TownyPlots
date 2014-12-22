package me.kleinerminer.townyplots.building;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import com.palmergames.bukkit.towny.object.Town;

public abstract class Building {
	public int x;
	public int z;
	public int x2;
	public int z2;
	public int size;
	public int ID;
	public Town town;
	public String type;
	public World world;
	Building(int x, int z, World world, int ID) {
		x = this.x;
		z = this.z;
		x2 = x + size;
		z2 = z + size;
		if(x < 0) x2 = -x2;
		if(z < 0) z2 = -z2;
		world = this.world;
		ID = this.ID;
	}
	public abstract int getX();
	public abstract int getY();
	public abstract int getZ();
	public abstract int getX2();
	public abstract int getZ2();
	public abstract int getSize();
	public abstract String getType();
	public abstract Town getTown();
	public abstract World getWorld();
	public abstract int getId();
	public abstract int getLevel();
	public abstract Sign getInfoSign();
	public abstract Location[] getOutputChests();
	
	public abstract void setX(int x);
	public abstract void setY(int y);
	public abstract void setZ(int z);
	public abstract void setX2(int x2);
	public abstract void setZ2(int z2);
	public abstract void setSize(int size);
	public abstract void setType(String type);
	public abstract void setTown(Town town);
	public abstract void setWorld(World world);
	public abstract void setId(int id);
	public abstract void setInfoSign(Sign sign);
	public abstract void setOutputChests(int index, Location loc);
	public abstract void setOutputChests(Location[] outputChests);
	
	public abstract String getLevelInfo();
	
}
