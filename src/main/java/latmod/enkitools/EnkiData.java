package latmod.enkitools;

import java.io.File;
import java.util.UUID;

import latmod.core.*;
import latmod.core.util.*;
import latmod.core.util.Vertex.DimPos;
import latmod.enkitools.rank.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EnkiData
{
	public static final String TAG_MAIL = "Mail";
	
	public static final FastMap<Integer, Data> data = new FastMap<Integer, Data>();
	public static final Data fakePlayerData = new Data(new LMPlayer(-1, new UUID(0L, 0L), "FakePlayer"));
	
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
		EnkiData.Warps.warps.clear();
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
		public final PlayerClaims claims;
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
			claims = new PlayerClaims(player);
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
		{ Teleporter.travelEntity(ep, x + 0.5D, y + 0.5D, z + 0.5D, dim); }
	}
	
	public static class Claim
	{
		public final PlayerClaims playerClaims;
		public final int posX;
		public final int posZ;
		public final int dim;
		
		public Claim(PlayerClaims p, int x, int z, int d)
		{ playerClaims = p; posX = x; posZ = z; dim = d; }
		
		public Claim(PlayerClaims p, EntityPlayer ep)
		{ this(p, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ), ep.dimension); }
		
		public boolean equals(Object o)
		{ return (o != null && o instanceof Claim) ? equalsClaim((Claim)o) : null; }
		
		public boolean equalsClaim(Claim c)
		{ return dim == c.dim && posX == c.posX && posZ == c.posZ; }
		
		public Chunk getChunk(World w)
		{ return w.getChunkFromChunkCoords(posX, posZ); }
		
		public double getDistSq(double x, double z)
		{
			double x0 = (posX + 0.5D) * 16D;
			double z0 = (posZ + 0.5D) * 16D;
			return MathHelperLM.distSq(x0, 0D, z0, x, 0D, z);
		}
		
		public double getDistSq(Claim c)
		{ return getDistSq((c.posX + 0.5D) * 16D, (c.posZ + 0.5D) * 16D); }
		
		// Static //
		
		public static Claim getClaim(int cx, int cz, int dim)
		{
			for(Data d : data)
			{
				for(Claim c : d.claims.claims)
				{
					if(c.dim == dim && c.posX == cx && c.posZ == cz)
						return c;
				}
			}
			
			return null;
		}
		
		public static Claim getClaimD(double x, double z, int dim)
		{ return getClaim(MathHelperLM.chunk(x), MathHelperLM.chunk(z), dim); }
		
		public static boolean claimsEquals(TwoObjects<PlayerClaims, Claim> c1, TwoObjects<PlayerClaims, Claim> c2)
		{
			if(c1 == null && c2 == null) return true;
			if(c1 != null && c2 == null) return false;
			if(c1 == null && c2 != null) return false;
			return c1.object1.owner.equals(c2.object1.owner);
		}
	}
	
	public static class PlayerClaims
	{
		public final LMPlayer owner;
		public final FastList<Claim> claims;
		private String desc = "";
		public boolean canExplode = true;
		
		public PlayerClaims(LMPlayer p)
		{
			owner = p;
			claims = new FastList<Claim>();
		}
		
		public int getMaxPower()
		{ return Rank.getConfig(owner, RankConfig.MAX_CLAIM_POWER).getInt(); }
		
		public String getDesc(boolean ownerDesc)
		{
			if(ownerDesc)
			{
				String s = desc.length() > 0 ? ("\"" + desc + "\" " + EnumChatFormatting.ITALIC) : "Claimed by ";
				return s + owner.username;
			}
			
			return EnumChatFormatting.ITALIC + (desc.length() > 0 ? ("\"" + desc + "\"") : "Claimed by " + owner.username);
		}
		
		public void setDesc(String s)
		{ desc = (s == null) ? "" : s; }
		
		public String getRawDesc()
		{ return desc.isEmpty() ? "Claimed area" : desc; }
		
		public ClaimResult changeChunk(EntityPlayer ep, Claim c, boolean add, boolean admin)
		{
			if(EnkiTools.isSpawnChunk(ep.worldObj, c.posX, c.posZ) || EnkiTools.isOutsideWorldBorder(ep.worldObj, c.posX * 16D + 8D, c.posZ * 16D + 8D))
				return ClaimResult.SPAWN;
			
			Claim c0 = Claim.getClaim(c.posX, c.posZ, c.dim);
			
			if(!admin && c0 != null && !c0.playerClaims.owner.equals(ep.getUniqueID()))
				return ClaimResult.NOT_OWNER;
			
			if(add && !admin)
			{
				if(claims.size() >= getMaxPower())
					return ClaimResult.NO_POWER;
				
				if(claims.contains(c))
					return ClaimResult.ALREADY_DONE;
				else
				{
					claims.add(c);
					owner.updateInfo(null);
					return ClaimResult.SUCCESS;
				}
			}
			else
			{
				if(claims.contains(c))
				{
					claims.remove(c);
					owner.updateInfo(null);
					return ClaimResult.SUCCESS;
				}
				else
					return ClaimResult.ALREADY_DONE;
			}
		}
		
		public boolean equals(Object o)
		{
			if(o == null) return false;
			if(o == this) return true;
			if(o instanceof UUID) return o.equals(owner);
			if(o instanceof PlayerClaims) return ((PlayerClaims)o).owner.equals(owner);
			return false;
		}
		
		public String toString()
		{ return LatCoreMC.removeFormatting(getDesc(true) + ", " + claims.size() + " / " + getMaxPower()); }
	}
	
	public static class AdminClaims
	{
	}
	
	public static enum ClaimResult
	{
		SUCCESS,
		ALREADY_DONE,
		SPAWN,
		NO_POWER,
		NOT_OWNER,
	}
}