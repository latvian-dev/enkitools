package latmod.enkitools;

import java.io.File;

import latmod.core.*;
import latmod.core.util.*;
import latmod.core.util.Vertex.DimPos;
import net.minecraft.entity.player.*;
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
		config = new File(LatCoreMC.latmodFolder, "enkitools/config.txt");
		
		ranks = new File(LatCoreMC.latmodFolder, "enkitools/ranks.txt");
		players = new File(LatCoreMC.latmodFolder, "enkitools/players.txt");
		
		File oldDir = new File(LatCoreMC.latmodFolder, "EnkiMods/");
		
		if(oldDir.exists())
		{
			File oldRanks = new File(oldDir, "Ranks.txt");
			if(oldRanks.exists()) LatCore.copyFile(oldRanks, ranks);
			
			File oldPlayers = new File(oldDir, "Players.txt");
			if(oldPlayers.exists()) LatCore.copyFile(oldPlayers, players);
			
			File oldConfig = new File(oldDir, "Config.cfg");
			if(oldConfig.exists()) LatCore.copyFile(oldConfig, new File(LatCoreMC.latmodFolder, "enkitools/old_config_backup.cfg"));
			
			LatCore.deleteFile(oldDir);
		}
	}
	
	public static Data getData(EntityPlayer ep)
	{ return getData(LMPlayer.getPlayer(ep)); }
	
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
					d.setHome(l.get(i), a[0], a[1], a[2], a[3]);
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
				Home h1 = d.homes.get(i);
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
		
		// Local //
		public Vertex.DimPos.Rot last;
		public Notification lastChunkMessage = new Notification("", "", null);
		
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
		
		public boolean setHome(String s, int x, int y, int z, int dim)
		{
			int i = homes.indexOf(s);
			if(i == -1) { homes.add(new Home(s, x, y, z, dim)); return true; }
			else { homes.set(i, new Home(s, x, y, z, dim)); return false; }
		}
		
		public boolean remHome(String s)
		{ return homes.remove(s); }
		
		public int homesSize()
		{ return homes.size(); }
		
		public boolean hasMoved(Vertex.DimPos.Rot pos)
		{ return !last.equals(pos); }
		
		public void updatePos(Vertex.DimPos.Rot pos)
		{ last = pos; }
	}
	
	public static class Warps
	{
		public static final FastList<Home> warps = new FastList<Home>();
		
		public static String[] listWarps()
		{
			String[] s = new String[warps.size()];
			for(int i = 0; i < s.length; i++)
				s[i] = warps.get(i).name;
			return s;
		}
		
		public static Home getWarp(String s)
		{ return warps.getObj(s); }
		
		public static boolean setWarp(String s, int x, int y, int z, int dim)
		{
			int i = warps.indexOf(s);
			if(i == -1) { warps.add(new Home(s, x, y, z, dim)); return true; }
			else { warps.set(i, new Home(s, x, y, z, dim)); return false; }
		}
		
		public static boolean remWarp(String s)
		{ return warps.remove(s); }
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
		
		public void teleportPlayer(EntityPlayerMP ep)
		{ LatCoreMC.teleportPlayer(ep, x + 0.5D, y + 0.5D, z + 0.5D, dim); }
	}
}