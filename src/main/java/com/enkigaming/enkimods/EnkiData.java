package com.enkigaming.enkimods;

import java.io.File;

import latmod.core.*;
import latmod.core.util.*;
import latmod.core.util.Vertex.DimPos;
import net.minecraft.nbt.NBTTagCompound;

public class EnkiData
{
	public static final String TAG_MAIL = "Mail";
	
	public static final FastMap<Integer, Data> data = new FastMap<Integer, Data>();
	
	public static File config;
	public static File ranks;
	public static File players;
	
	public static void init()
	{
		config = new File(LatCoreMC.latmodFolder, "EnkiMods/Config.cfg");
		ranks = new File(LatCoreMC.latmodFolder, "EnkiMods/Ranks.txt");
		players = new File(LatCoreMC.latmodFolder, "EnkiMods/Players.txt");
	}
	
	public static Data getData(LMPlayer p)
	{
		Data h = data.get(p.playerID);
		if(h == null) { h = new Data(p); data.put(p.playerID, h); }
		return h;
	}
	
	public static void load(LMPlayer p, NBTTagCompound tag)
	{
		Data d = getData(p);
		
		{
			d.homes.clear();
			
			NBTTagCompound tag1 = (NBTTagCompound)tag.getTag("Homes");
			
			if(tag1 != null && !tag1.hasNoTags())
			{
				FastList<String> l = NBTHelper.getMapKeys(tag1);
				
				for(int i = 0; i < l.size(); i++)
				{
					int[] a = tag1.getIntArray(l.get(i));
					d.addHome(l.get(i), a[0], a[1], a[2], a[3]);
				}
			}
		}
		
		PlayerClaims pc = PlayerClaims.getClaims(p);
		pc.readFromNBT(tag.getCompoundTag("Claims"));
		
		d.lastDeath = null;
		
		if(tag.hasKey("LastDeath"))
		{
			d.lastDeath = new DimPos();
			d.lastDeath.readFromNBT(tag.getCompoundTag("LastDeath"));
		}
		
		d.lastPos = null;
		
		if(tag.hasKey("LastPos"))
		{
			d.lastPos = new DimPos();
			d.lastPos.readFromNBT(tag.getCompoundTag("LastPos"));
		}
		
		d.notifications = tag.getByte("Notify");
	}
	
	public static void save(LMPlayer p, NBTTagCompound tag)
	{
		Data d = data.get(p.playerID);
		if(d == null) return;
		
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			for(int i = 0; i < d.homes.size(); i++)
			{
				Data.Home h1 = d.homes.get(i);
				tag1.setIntArray(h1.name, new int[] { h1.x, h1.y, h1.z, h1.dim });
			}
			
			tag.setTag("Homes", tag1);
		}
		
		{
			PlayerClaims pc = PlayerClaims.getClaims(p);
			
			if(pc != null)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				pc.writeToNBT(tag1);
				tag.setTag("Claims", tag1);
			}
		}
		
		if(d.lastDeath != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			d.lastDeath.writeToNBT(tag1);
			tag.setTag("LastDeath", tag1);
		}
		
		if(d.lastPos != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			d.lastPos.writeToNBT(tag1);
			tag.setTag("LastPos", tag1);
		}
		
		tag.setByte("Notify", (byte)d.notifications);
	}
	
	public static class Data
	{
		public final LMPlayer player;
		private final FastList<Home> homes;
		public DimPos lastDeath = null;
		public DimPos lastPos = null;
		public int notifications = 1;
		
		private Data(LMPlayer p)
		{
			player = p;
			homes = new FastList<Home>();
		}
		
		public String[] listHomes()
		{
			String[] s = new String[homes.size()];
			for(int i = 0; i < s.length; i++)
				s[i] = homes.get(i).name;
			return s;
		}
		
		public String[] listHomesNoDef()
		{
			FastList<String> list = new FastList<String>();
			for(Home h : homes) if(!h.name.equals("Default")) list.add(h.name);
			return list.toArray(new String[0]);
		}
		
		public Home getHome(String s)
		{ return homes.getObj(s); }
		
		public boolean addHome(String s, int x, int y, int z, int dim)
		{
			int i = homes.indexOf(s);
			if(i == -1) { homes.add(new Home(s, x, y, z, dim)); return true; }
			else { homes.set(i, new Home(s, x, y, z, dim)); return false; }
		}
		
		public boolean remHome(String s)
		{ return homes.remove(s); }
		
		public int homesSize()
		{ return homes.size(); }
		
		public static class Home
		{
			public String name;
			public int x, y, z;
			public int dim;
			
			public Home(String s, int px, int py, int pz, int d)
			{ name = s; x = px; y = py; z = pz; dim = d; }
			
			public String toString()
			{ return name; }
			
			public boolean equals(Object o)
			{ return o != null && (o == this || o.toString().equals(toString())); }
		}
	}
}