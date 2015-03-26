package com.enkigaming.enkimods;

import java.util.UUID;

import latmod.core.*;
import latmod.core.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.enkigaming.enkimods.rank.*;

public class PlayerClaims
{
	public final LMPlayer owner;
	public final FastList<Claim> claims;
	private String desc = "";
	public byte notifyType = 0;
	public boolean canExplode = true;
	
	public PlayerClaims(LMPlayer p)
	{
		owner = p;
		claims = new FastList<Claim>();
	}
	
	public int getMaxPower()
	{ return Rank.getConfig(owner, RankConfig.MAX_CLAIM_POWER).getInt(); }
	
	public void readFromNBT(NBTTagCompound tag)
	{
		claims.clear();
		
		NBTTagList c = (NBTTagList)tag.getTag("Claims");
		if(c != null) for(int i = 0; i < c.tagCount(); i++)
		{
			int[] pos = c.func_150306_c(i);
			claims.add(new Claim(this, pos[0], pos[1], pos[2]));
		}
		
		desc = tag.getString("Desc");
		
		if(!tag.hasKey("Notify")) notifyType = 1;
		else notifyType = tag.getByte("Notify");
		
		if(!tag.hasKey("Explode")) canExplode = true;
		else canExplode = tag.getBoolean("Explode");
	}
	
	public boolean shouldSave()
	{ return claims.size() > 0 || !desc.isEmpty() || notifyType != 1; }
	
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagList c = new NBTTagList();
		
		for(int i = 0; i < claims.size(); i++)
		{
			Claim c1 = claims.get(i);
			c.appendTag(new NBTTagIntArray(new int[] { c1.posX, c1.posZ, c1.dim }));
		}
		
		if(c.tagCount() > 0) tag.setTag("Claims", c);
		
		tag.setString("Desc", desc);
		tag.setByte("Notify", notifyType);
		tag.setBoolean("Explode", canExplode);
	}
	
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
		if(EnkiMods.isSpawnChunk(ep.worldObj, c.posX, c.posZ) || EnkiMods.isOutsideWorldBorder(ep.worldObj, c.posX * 16D + 8D, c.posZ * 16D + 8D))
			return ClaimResult.SPAWN;
		
		Claim c0 = getClaim(c.posX, c.posZ, c.dim);
		
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
				return ClaimResult.SUCCESS;
			}
		}
		else
		{
			if(claims.contains(c))
			{
				claims.remove(c);
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
	
	// Classes //
	
	public static enum ClaimResult
	{
		SUCCESS,
		ALREADY_DONE,
		SPAWN,
		NO_POWER,
		NOT_OWNER,
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
	}
	
	// Static methods //
	
	public static final FastMap<Integer, PlayerClaims> claimsMap = new FastMap<Integer, PlayerClaims>();
	
	public static PlayerClaims getClaims(Object o)
	{
		LMPlayer p = LMPlayer.getPlayer(o);
		if(p == null) return null;
		
		PlayerClaims pc = claimsMap.get(p.playerID);
		
		if(pc == null)
		{
			pc = new PlayerClaims(p);
			claimsMap.put(pc.owner.playerID, pc);
		}
		
		return pc;
	}
	
	public static Claim getClaim(int cx, int cz, int dim)
	{
		for(int i = 0; i < claimsMap.size(); i++)
		{
			PlayerClaims pc = claimsMap.values.get(i);
			
			for(int j = 0; j < pc.claims.size(); j++)
			{
				Claim c = pc.claims.get(j);
				
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