package latmod.enkitools;

import static net.minecraft.util.EnumChatFormatting.*;

import java.util.*;

import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class EnkiToolsTickHandler
{
	public static final EnkiToolsTickHandler instance = new EnkiToolsTickHandler();
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
				
				if(EnkiToolsConfig.get().general.enableRestartClock)
				{
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
						if(secondsLeft >= 60)
							printServer(LIGHT_PURPLE + "Server will be restarting after " + msg);
						
						LatCoreMC.notifyPlayer(null, new Notification("Server restarts in...", msg, new ItemStack(Items.clock), 4000));
					}
				}
				
				for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers().values)
				{
					EnkiData.Data d = EnkiData.getData(ep);
					Vertex.DimPos.Rot pos = new Vertex.DimPos.Rot(ep);
					
					if(d.hasMoved(pos))
					{
						if(EnkiTools.isOutsideWorldBorder(ep.worldObj.provider.dimensionId, ep.posX, ep.posZ))
						{
							ep.motionX = ep.motionY = ep.motionZ = 0D;
							LatCoreMC.printChat(ep, "You have reached the world border!");
							
							if(EnkiTools.isOutsideWorldBorder(d.last.dim, d.last.pos.x, d.last.pos.z))
							{
								LatCoreMC.printChat(ep, "Teleporting to spawn!");
								Vertex spawn = LatCoreMC.getSpawnPoint(0);
								
								if(EnkiTools.isOutsideWorldBorder(0, spawn.x, spawn.z))
								{
									spawn.x = spawn.z = 0.5D;
									spawn.y = DimensionManager.getWorld(0).getTopSolidOrLiquidBlock(0, 0);
								}
								
								Teleporter.travelEntity(ep, spawn.x, spawn.y, spawn.z, 0);
							}
							else
							{
								Teleporter.travelEntity(ep, d.last.pos.x, d.last.pos.y, d.last.pos.z, ep.worldObj.provider.dimensionId);
							}
						}
						
						d.updatePos(pos);
					}
					
					printChunkChangedMessage(ep);
				}
			}
		}
	}
	
	public void printChunkChangedMessage(EntityPlayerMP ep)
	{
		int notify = EnkiData.getData(LMPlayer.getPlayer(ep)).notifications;
		
		EnkiData.Data d = EnkiData.getData(ep);
		
		Notification n;
		
		if(notify == 1)
			n = getChunkScreenMessage(ep.worldObj, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ), ep);
		else if(notify == 2)
		{
			String s = getChunkStatusMessage(ep.worldObj, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ), ep, false);
			n = new Notification(s, "", null);
		}
		else return;
		
		if(!d.lastChunkMessage.equals(n))
		{
			if(notify == 2)
				LatCoreMC.printChat(ep, n.title);
			else
				LatCoreMC.notifyPlayer(ep, n);
			
			d.lastChunkMessage = n;
		}
	}
	
	public String getChunkStatusMessage(World w, int x, int z, EntityPlayer ep, boolean owner)
	{
		EnkiData.Claim claim = EnkiData.Claim.getClaim(x, z, w.provider.dimensionId);
		
		if(claim == null)
		{
			if(EnkiTools.isSpawnChunk(w, x, z))
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
		EnkiData.Claim claim = EnkiData.Claim.getClaim(x, z, w.provider.dimensionId);
		
		int t = 1500;
		
		if(claim == null)
		{
			if(EnkiTools.isSpawnChunk(w, x, z))
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
			long rrSec = EnkiToolsConfig.get().general.restartHours * 3600L + EnkiToolsConfig.get().general.restartMinutes * 60L;
			
			if(rrSec < gtm0sec) rrSec += LatCore.DAY24;
			
			restartSeconds = rrSec - gtm0sec;
			
			EnkiTools.mod.logger.info("Server restart in " + LatCore.formatTime(restartSeconds, false));
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