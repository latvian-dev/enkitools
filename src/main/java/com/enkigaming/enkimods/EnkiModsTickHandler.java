package com.enkigaming.enkimods;

import static net.minecraft.util.EnumChatFormatting.*;

import java.util.*;

import latmod.core.*;
import latmod.core.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.enkigaming.enkimods.PlayerClaims.Claim;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class EnkiModsTickHandler
{
	public static final EnkiModsTickHandler instance = new EnkiModsTickHandler();
	public static final String DATA_KEY = "EnkiTick";
	
	public boolean serverStarted = false;
	private long startMillis = 0L;
	private long startSeconds = 0L;
	private long currentMillis = 0L;
	private long currentSeconds = 0L;
	private long restartSeconds = 0L;
	
	private static void printServer(String s)
	{ LatCoreMC.printChat(MinecraftServer.getServer(), s, true); }
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent e)
	{
		if(LatCoreMC.isServer() && e.side == Side.SERVER && e.phase == TickEvent.Phase.END && e.type == TickEvent.Type.WORLD)
		{
			long t = System.currentTimeMillis();
			
			if(t - currentMillis >= 1000L)
			{
				currentMillis = t;
				currentSeconds = currentMillis / 1000L;
				
				long secondsLeft = getSecondsUntilRestart();
				
				String msg = null;
				
				if(secondsLeft <= 0) { MinecraftServer.getServer().initiateShutdown(); return; }
				else if(secondsLeft <= 10) msg = secondsLeft + " Seconds";
				else if(secondsLeft == 30) msg = "30 Seconds";
				else if(secondsLeft == 60) msg = "1 Minute";
				else if(secondsLeft == 300) msg = "5 Minutes";
				else if(secondsLeft == 600) msg = "10 Minutes";
				
				if(msg != null)
				{
					if(secondsLeft >= 300)
						printServer(LIGHT_PURPLE + "Server will be restarting after " + msg);
					
					if(secondsLeft <= 10L)
						LatCoreMC.notifyPlayer(null, new Notification("Server restart!", null, new ItemStack(Items.clock, (int)secondsLeft), 1000L));
					else
						LatCoreMC.notifyPlayer(null, new Notification("Server restarts in...", msg, new ItemStack(Items.clock), 4000L));
				}
				
				if(LatCoreMC.hasOnlinePlayers())
				{
					FastList<EntityPlayerMP> players = LatCoreMC.getAllOnlinePlayers().values;
					
					for(int i = 0; i < players.size(); i++)
					{
						EntityPlayerMP ep = players.get(i);
						TrackedPlayer tp = TrackedPlayer.get(ep);
						Vertex.DimPos.Rot pos = new Vertex.DimPos.Rot(ep);
						
						if(tp.hasMoved(pos))
						{
							if(EnkiMods.isOutsideWorldBorder(ep.worldObj, ep.posX, ep.posZ))
							{
								if(ep.dimension == tp.last.dim)
								{
									ep.motionX = ep.motionY = ep.motionZ = 0D;
									ep.playerNetServerHandler.setPlayerLocation(tp.last.pos.x, tp.last.pos.y, tp.last.pos.z, ep.rotationYaw, ep.rotationPitch);
									LatCoreMC.printChat(ep, "You have reached the world border!");
								}
								else LatCoreMC.printChat(ep, "You have reached the world border, please return to your home or portal!");
							}
							
							tp.updatePos(pos);
						}
						
						printChunkChangedMessage(ep);
					}
				}
			}
		}
	}
	
	public static class TrackedPlayer
	{
		public final EntityPlayerMP player;
		public final LMPlayer playerLM;
		public Vertex.DimPos.Rot last;
		public Notification lastChunkMessage = new Notification("", "", null);
		
		private TrackedPlayer(EntityPlayerMP ep)
		{
			player = ep;
			playerLM = LMPlayer.getPlayer(ep);
			last = new Vertex.DimPos.Rot(ep);
		}
		
		public void load()
		{
			NBTTagCompound tag = playerLM.tempData.getCompoundTag(DATA_KEY);
			last.readFromNBT(tag.getCompoundTag("Pos"));
			lastChunkMessage = Notification.readFromNBT(tag.getCompoundTag("Msg"));
		}
		
		public void save()
		{
			NBTTagCompound tag = new NBTTagCompound();
			
			NBTTagCompound tagLast = new NBTTagCompound();
			last.writeToNBT(tagLast);
			tag.setTag("Pos", tagLast);
			
			NBTTagCompound tagMsg = new NBTTagCompound();
			lastChunkMessage.writeToNBT(tagMsg);
			tag.setTag("Msg", tagMsg);
			
			playerLM.tempData.setTag(DATA_KEY, tag);
		}
		
		public boolean hasMoved(Vertex.DimPos.Rot pos)
		{ return !last.equals(pos); }
		
		public void updatePos(Vertex.DimPos.Rot pos)
		{ last = pos; save(); }
		
		public static TrackedPlayer get(EntityPlayerMP ep)
		{
			TrackedPlayer tp = new TrackedPlayer(ep);
			if(tp.playerLM.tempData.hasKey(DATA_KEY)) tp.load();
			else tp.updatePos(new Vertex.DimPos.Rot(ep));
			return tp;
		}
	}
	
	public void printChunkChangedMessage(EntityPlayerMP ep)
	{
		PlayerClaims claims0 = PlayerClaims.getClaims(ep);
		if(claims0 == null || claims0.notifyType == 0) return;
		
		TrackedPlayer tp = new TrackedPlayer(ep);
		tp.load();
		
		Notification n;
		
		if(claims0.notifyType == 1)
			n = getChunkScreenMessage(ep.worldObj, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ), ep);
		else if(claims0.notifyType == 2)
		{
			String s = getChunkStatusMessage(ep.worldObj, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ), ep, false);
			n = new Notification(s, "", null);
		}
		else return;
		
		if(!tp.lastChunkMessage.equals(n))
		{
			if(claims0.notifyType == 2)
				LatCoreMC.printChat(ep, n.title);
			else
				LatCoreMC.notifyPlayer(ep, n);
			
			tp.lastChunkMessage = n;
			tp.save();
		}
	}
	
	public String getChunkStatusMessage(World w, int x, int z, EntityPlayer ep, boolean owner)
	{
		Claim claim = PlayerClaims.getClaim(x, z, w.provider.dimensionId);
		
		if(claim == null)
		{
			if(EnkiMods.isSpawnChunk(w, x, z))
				return AQUA + "Spawn area";
			else
				return GREEN + "Wilderness";
		}
		else
		{
			EnumChatFormatting ecf = BLUE;
			if(claim.playerClaims.owner.isFriend(LMPlayer.getPlayer(ep)))
				ecf = GREEN;
			
			return ecf + claim.playerClaims.getDesc(owner);
		}
	}
	
	public Notification getChunkScreenMessage(World w, int x, int z, EntityPlayer ep)
	{
		Claim claim = PlayerClaims.getClaim(x, z, w.provider.dimensionId);
		
		long t = 1500L;
		
		if(claim == null)
		{
			if(EnkiMods.isSpawnChunk(w, x, z))
				return new Notification(AQUA + "Spawn area", "", new ItemStack(Items.nether_star), t);
			else
				return new Notification(DARK_GREEN + "Wilderness", "", new ItemStack(Items.skull, 1, 4), t);
		}
		else
		{
			if(claim.playerClaims.owner.isFriend(LMPlayer.getPlayer(ep)))
				return new Notification(GREEN + claim.playerClaims.getRawDesc(), GRAY + claim.playerClaims.owner.username, new ItemStack(Items.skull, 1, 3), t);
			else
				return new Notification(BLUE + claim.playerClaims.getRawDesc(), GRAY + claim.playerClaims.owner.username, new ItemStack(Items.skull, 1, 1), t);
		}
	}
	
	public void resetTimer(boolean started)
	{
		serverStarted = started;
		
		if(serverStarted)
		{
			currentMillis = startMillis = System.currentTimeMillis();
			currentSeconds = startSeconds = startMillis / 1000L;
			
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+00:00"));
			long gtm0sec = (c.getTimeInMillis() / 1000L) % LatCore.DAY24;
			long rrSec = EnkiModsConfig.General.restartClock.object1 * 3600L + EnkiModsConfig.General.restartClock.object2 * 60L;
			
			if(rrSec < gtm0sec) rrSec += LatCore.DAY24;
			
			restartSeconds = rrSec - gtm0sec;
			
			EnkiMods.mod.logger.info("Server restart in " + LatCore.formatTime(restartSeconds, false));
		}
	}
	
	public long getSecondsUntilRestart()
	{ return restartSeconds - (currentSeconds - startSeconds); }
	
	public void forceShutdown(int sec)
	{
		restartSeconds = sec + 1;
		currentMillis = startMillis = System.currentTimeMillis();
		currentSeconds = startSeconds = startMillis / 1000L;
	}
	
	public long currentMillis()
	{ return currentMillis; }
	
	public long currentSeconds()
	{ return currentSeconds; }
	
	public long secondsElapsed()
	{ return currentSeconds - startMillis; }
}