package com.enkigaming.enkimods;

import java.io.File;

import latmod.core.*;
import latmod.core.util.*;
import net.minecraft.nbt.NBTTagCompound;

public class EnkiData
{
	public static final String TAG_LAST_POS = "LastSavedPos";
	public static final String TAG_LAST_DEATH = "LastDeath";
	public static final String TAG_MAIL = "Mail";
	
	public static final FastMap<Integer, NBTTagCompound> data = new FastMap<Integer, NBTTagCompound>();
	
	public static NBTTagCompound data(int p)
	{
		NBTTagCompound tag = data.get(p);
		
		if(tag == null)
		{
			tag = new NBTTagCompound();
			data.put(p, tag);
		}
		
		return tag;
	}
	
	public static File config;
	public static File ranks;
	public static File players;
	
	public static void init()
	{
		config = new File(LatCoreMC.latmodFolder, "EnkiMods/Config.cfg");
		ranks = new File(LatCoreMC.latmodFolder, "EnkiMods/Ranks.txt");
		players = new File(LatCoreMC.latmodFolder, "EnkiMods/Players.txt");
	}
	
	public static class Claims
	{
		public static void load(LMPlayer p)
		{
			PlayerClaims pc = PlayerClaims.getClaims(p);
			pc.readFromNBT(data(p.playerID).getCompoundTag("Claims"));
		}
		
		public static void save(LMPlayer p)
		{
			PlayerClaims pc = PlayerClaims.getClaims(p);
			
			if(pc != null && pc.shouldSave())
			{
				NBTTagCompound tag2 = new NBTTagCompound();
				pc.writeToNBT(tag2);
				data(p.playerID).setTag("Claims", tag2);
			}
		}
	}
	
	public static class Homes
	{
		public static final String TAG = "Homes";
		public static final String TAG_OLD = "EnkiHomes";
		
		public final LMPlayer player;
		public final FastMap<String, Home> homes;
		
		public Homes(LMPlayer p)
		{
			player = p;
			homes = new FastMap<String, Home>();
			
			if(p.commonData.hasKey(TAG_OLD))
			{
				data(p.playerID).setTag(TAG, p.commonData.getTag(TAG_OLD));
				p.commonData.removeTag(TAG_OLD);
				
				LatCoreMC.printChat(null, "Loaded old homes from " + p.username + ": " + data(p.playerID).getTag(TAG));
			}
			
			NBTTagCompound map = (NBTTagCompound)data(p.playerID).getTag(TAG);
			
			if(map != null && !map.hasNoTags())
			{
				FastList<String> l = NBTHelper.getMapKeys(map);
				
				for(int i = 0; i < l.size(); i++)
				{
					int[] a = map.getIntArray(l.get(i));
					Home h = new Home(l.get(i), a[0], a[1], a[2], a[3]);
					homes.put(h.name, h);
				}
			}
		}
		
		public void save()
		{
			NBTTagCompound map = new NBTTagCompound();
			
			for(int i = 0; i < homes.size(); i++)
			{
				Home h = homes.get(i);
				
				if(h != null && h.name != null)
					map.setIntArray(h.name, new int[] { h.x, h.y, h.z, h.dim });
			}
			
			data(player.playerID).setTag(TAG, map);
		}
		
		public String[] list(boolean remDef)
		{
			String[] s = new String[homes.size()];
			for(int i = 0; i < s.length; i++)
				if(!remDef || !s[i].equals("Default"))
					s[i] = homes.get(i).name;
			return s;
		}
		
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