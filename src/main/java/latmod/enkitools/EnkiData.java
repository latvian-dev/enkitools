package latmod.enkitools;

import java.io.File;
import java.util.UUID;

import latmod.enkitools.rank.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.mojang.authlib.GameProfile;

public class EnkiData
{
	public static final String TAG_MAIL = "Mail";
	
	public static final FastMap<Integer, Data> data = new FastMap<Integer, Data>();
	public static final Data fakePlayerData = new Data(new LMPlayer(-1, new GameProfile(new UUID(0L, 0L), "FakePlayer")));
	
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
		if(p == null) return fakePlayerData;
		Data h = data.get(p.playerID);
		if(h == null) { h = new Data(p); data.put(p.playerID, h); }
		return h;
	}
	
	public static void clearData()
	{
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
		
		{
			d.claims.claims.clear();
			
			NBTTagCompound tag1 = tag.getCompoundTag("Claims");
			
			NBTTagList c = (NBTTagList)tag1.getTag("Claims");
			if(c != null) for(int i = 0; i < c.tagCount(); i++)
			{
				int[] pos = c.func_150306_c(i);
				d.claims.claims.add(new Claim(d.claims, pos[0], pos[1], pos[2]));
			}
			
			d.claims.desc = tag1.getString("Desc");
			
			if(!tag1.hasKey("Explode")) d.claims.canExplode = true;
			else d.claims.canExplode = tag1.getBoolean("Explode");
		}
		
		d.lastDeath = null;
		
		if(tag.hasKey("LastDeath"))
		{
			d.lastDeath = new EntityPos();
			d.lastDeath.readFromNBT(tag.getCompoundTag("LastDeath"));
		}
	}
	
	public static void save(LMPlayer p, NBTTagCompound tag)
	{
		Data d = data.get(p.playerID);
		if(d == null) return;
		
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			for(int i = 0; i < d.homes.size(); i++)
				tag1.setIntArray(d.homes.keys.get(i), d.homes.values.get(i).toIntArray());
			
			tag.setTag("Homes", tag1);
		}
		
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			NBTTagList c = new NBTTagList();
			
			for(int i = 0; i < d.claims.claims.size(); i++)
			{
				Claim c1 = d.claims.claims.get(i);
				c.appendTag(new NBTTagIntArray(new int[] { c1.posX, c1.posZ, c1.dim }));
			}
			
			if(c.tagCount() > 0) tag1.setTag("Claims", c);
			
			tag1.setString("Desc", d.claims.desc);
			tag1.setBoolean("Explode", d.claims.canExplode);
			
			tag.setTag("Claims", tag1);
		}
		
		if(d.lastDeath != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			d.lastDeath.writeToNBT(tag1);
			tag.setTag("LastDeath", tag1);
		}
	}
	
	public static class Data
	{
		public final LMPlayer player;
		private final FastMap<String, EntityPos> homes;
		
		private Data(LMPlayer p)
		{
			player = p;
			homes = new FastMap<String, EntityPos>();
		}
		
		public String[] listHomes()
		{ return homes.keys.toArray(new String[0]); }
		
		public String[] listHomesNoDef()
		{
			FastList<String> list = homes.keys.clone();
			list.remove("Default");
			return list.toArray(new String[0]);
		}
		
		public EntityPos getHome(String s)
		{ return homes.get(s); }
		
		public boolean setHome(String s, int x, int y, int z, int dim)
		{ return homes.put(s, new EntityPos(x + 0.5D, y + 0.5D, z + 0.5D, dim)); }
		
		public boolean remHome(String s)
		{ return homes.remove(s); }
		
		public int homesSize()
		{ return homes.size(); }
	}
	
	public static enum ClaimResult
	{
		SUCCESS,
		ALREADY_DONE,
		SPAWN,
		WORLD_BORDER,
		NO_POWER,
		NOT_OWNER,
	}
}