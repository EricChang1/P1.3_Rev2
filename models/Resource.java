package models;

import java.util.ArrayList;

public class Resource implements Cloneable
{
	//Pent Constructor
	public Resource(Block Block, int Inventory, double Volume, boolean Infinite, ArrayList<Block> Rot, BlockType Type, ArrayList<ArrayList<ArrayList<Integer>>> Cells, ArrayList<Position> RotPos)
	{
		block = Block.clone();
		inventory = Inventory;
		volume = Volume;
		infinite = Infinite;
		rot = Rot;
		type = Type;
		cells = Cells;
		rotPos = RotPos;
	}
	
	public Resource(Block Block, int Inventory, double Volume, boolean Infinite)
	{
		block = Block.clone();
		inventory = Inventory;
		volume = Volume;
		infinite = Infinite;
	}
	//Parcel Constructor
	public Resource(Block Block, int Inventory, double Volume, boolean Infinite, ArrayList<Block> Rot, BlockType Type)
	{
		block = Block.clone();
		inventory = Inventory;
		volume = Volume;
		infinite = Infinite;
		rot = Rot;
		type = Type;
	}
	
	
	/**
	 * deep copy clone method
	 */
	
	public Resource clone()
	{
		return new Resource (block, inventory, volume, infinite);
	}
	
	
	public void refill()
	{
		inventory++;
	}
	
	public void deduct()
	{
		if (inventory>0)
			inventory--;
		
		if (infinite==true)
			refill();
	}
	public int getInventory()
	{
		return inventory;
	}
	public boolean isInfinite()
	{
		return infinite;
	}
	
	/**
	 * @return true if inventory == 0 or !infinite
	 */
	public boolean isEmpty()
	{
		return (inventory == 0 || !isInfinite());
	}
	
	public Block getBlock()
	{
		return block.clone();
	}
	
	public double getVolume()
	{
		return volume;
	}
	
	public ArrayList<Block> getRot()
	{
		return rot;
	}
	
	public enum BlockType
	{
		PENT, PARCEL
	}
	
	public BlockType getType()
	{
		return type;
	}
	public  ArrayList<ArrayList<ArrayList<Integer>>> getCells()
	{
		return cells;
	}
	
	public ArrayList<Position> rotatedPos()
	{
		return rotPos;
	}
	
	private Block block;
	private int inventory;
	private double volume;
	private boolean infinite;
	private ArrayList<Block> rot;
	private BlockType type;
	private ArrayList<ArrayList<ArrayList<Integer>>> cells;
	private ArrayList<Position> rotPos;
	
}