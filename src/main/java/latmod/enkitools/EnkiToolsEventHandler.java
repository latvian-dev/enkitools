package latmod.enkitools;

import latmod.enkitools.rank.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.event.*;
import latmod.ftbu.core.util.LatCore;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.*;
import net.minecraft.event.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import cpw.mods.fml.common.eventhandler.*;

public class EnkiToolsEventHandler
{
	public static final EnkiToolsEventHandler instance = new EnkiToolsEventHandler();
	
	@SubscribeEvent
	public void playerLoggedIn(LMPlayerEvent.LoggedIn e)
	{
		if(e.side.isClient()) return;
		
		Rank.getPlayerRank(e.player);
	}
	
	@SubscribeEvent
	public void loadLMData(LoadLMDataEvent e)
	{
		if(e.phase.isPre())
		{
			Rank.reload();
			EnkiData.clearData();
		}
		
		if(e.phase.isPost())
		{
			NBTTagCompound tag = NBTHelper.readMap(e.getFile("EnkiMods.dat"));
			
			if(tag != null)
			{
				{
					NBTTagCompound tag1 = tag.getCompoundTag("Players");
					
					for(int i = 0; i < LMPlayer.map.size(); i++)
					{
						LMPlayer p = LMPlayer.map.values.get(i);
						EnkiData.load(p, tag1.getCompoundTag("" + p.playerID));
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void saveLMData(SaveLMDataEvent e)
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		NBTTagCompound players = new NBTTagCompound();
		
		for(int i = 0; i < LMPlayer.map.size(); i++)
		{
			LMPlayer p = LMPlayer.map.values.get(i);
			NBTTagCompound tag1 = new NBTTagCompound();
			EnkiData.save(p, tag1);
			players.setTag("" + p.playerID, tag1);
		}
		
		tag.setTag("Players", players);
		
		NBTHelper.writeMap(e.getFile("EnkiMods.dat"), tag);
	}
	
	@SubscribeEvent
	public void onPlayerDeath(net.minecraftforge.event.entity.living.LivingDeathEvent e)
	{
		if(e.entity instanceof EntityPlayer)
		{
			LMPlayer p = LMPlayer.getPlayer(e.entity);
			EnkiData.Data d = EnkiData.getData(p);
			if(d.lastDeath == null) d.lastDeath = new EntityPos();
			d.lastDeath.set(e.entity);
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		//if(e.world.isRemote) return;
		
		if(e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
		if(!canInteract(e)) e.setCanceled(true);
		else if(e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);
			
			if(te != null && !te.isInvalid() && te instanceof TileEntitySign)
			{
				TileEntitySign t = (TileEntitySign)te;
				
				if(EnkiToolsConfig.get().general.enableHomeSigns && t.signText[1].equals("[home]"))
				{
					LatCoreMC.executeCommand(e.entityPlayer, "home " + t.signText[2]);
					e.setCanceled(true);
					return;
				}
				else if(EnkiToolsConfig.get().general.enableWarpSigns && !t.signText[2].isEmpty() && t.signText[1].equals("[warp]"))
				{
					LatCoreMC.executeCommand(e.entityPlayer, "warp " + t.signText[2]);
					e.setCanceled(true);
					return;
				}
			}
		}
	}
	
	private boolean canInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(EnkiTools.isOutsideWorldBorderD(e.world.provider.dimensionId, e.x, e.z)) return false;
		
		if(e.entityPlayer.capabilities.isCreativeMode) return true;
		
		if(e.world.provider.dimensionId == 0 && EnkiToolsConfig.get().world.spawnDistance > 0F && EnkiTools.isSpawnChunkD(e.world.provider.dimensionId, e.x, e.z))
		{
			if(Rank.getConfig(e.entityPlayer, RankConfig.IGNORE_SPAWN).getBool()) return true;
			
			String bn = getName(e);
			
			if(e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && LatCore.contains(EnkiToolsConfig.get().world.spawnBreakWhitelist, bn))
				return true;
			
			if(e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && LatCore.contains(EnkiToolsConfig.get().world.spawnInteractWhitelist, bn))
				return !LatCore.contains(EnkiToolsConfig.get().world.placementBlacklist, bn);
			
			return false;
		}
		
		EnkiData.Claim cc = EnkiData.Claim.getClaimD(e.x, e.z, e.entity.dimension);
		
		if(cc != null && !cc.playerClaims.owner.isFriend(LMPlayer.getPlayer(e.entityPlayer)))
		{
			e.entityPlayer.swingItem();
			return false;
		}
		
		return true;
	}
	
	private String getName(PlayerInteractEvent e)
	{ return Block.blockRegistry.getNameForObject(e.world.getBlock(e.x, e.y, e.z)); }
	
	@SubscribeEvent
	public void onMobSpawned(net.minecraftforge.event.entity.EntityJoinWorldEvent e)
	{
		if(!EnkiToolsConfig.get().world.peacefulSpawn) return;
		
		if((e.entity instanceof IMob || (e.entity instanceof EntityChicken && e.entity.riddenByEntity != null)) && EnkiTools.isSpawnChunkD(e.world.provider.dimensionId, e.entity.posX, e.entity.posZ))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onPlayerAttacked(net.minecraftforge.event.entity.living.LivingAttackEvent e)
	{
		if(EnkiToolsConfig.get().world.spawnPVP || !EnkiTools.isSpawnChunkD(e.entity.worldObj.provider.dimensionId, e.entity.posX, e.entity.posZ)) return;
		
		if(e.entity instanceof EntityPlayer && e.source instanceof EntityDamageSource)
		{
			Entity e1 = ((EntityDamageSource)e.source).getEntity();
			
			if(e1 instanceof EntityPlayerMP && !(e1 instanceof FakePlayer))
			{
				EntityPlayerMP ep = (EntityPlayerMP)e1;
				
				if(!ep.capabilities.isCreativeMode && !Rank.getConfig(ep, RankConfig.IGNORE_SPAWN).getBool())
					e.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChatEvent(ServerChatEvent e)
	{
		if(!EnkiToolsConfig.get().general.overrideChat) return;
		
		LMPlayer p = LMPlayer.getPlayer(e.username);
		if(p == null) return;
		
		Rank r = Rank.getPlayerRank(p);
		if(r == null) return;
		
		String name = r.getUsername(e.username);
		
		e.component = new ChatComponentTranslation("");
		IChatComponent nameC = new ChatComponentText(name);
		
		//nameC.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("/tell " + name)));
		//nameC.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + name + " "));
		
		e.component.appendSibling(new ChatComponentText("<"));
		e.component.appendSibling(nameC);
		e.component.appendSibling(new ChatComponentText(">"));
		
		String[] msg = e.message.split(" ");
		
		for(String s : msg)
		{
			IChatComponent c = new ChatComponentText(" " + s);
			
			if(s.startsWith("http://") || s.startsWith("https://"))
			{
				c = new ChatComponentText(" [Link]");
				c.getChatStyle().setColor(EnumChatFormatting.GOLD);
				c.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(s)));
				c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, s));
			}
			
			e.component.appendSibling(c);
		}
	}
	
	@SubscribeEvent
	public void onCommandEvent(CommandEvent e)
	{
		if(e.sender instanceof EntityPlayer)
		{
			LMPlayer p = LMPlayer.getPlayer(e.sender);
			Rank r = Rank.getPlayerRank(p);
			
			if(!r.allowCommand(e.command.getCommandName(), e.parameters))
			{
				LatCoreMC.printChat(e.sender, EnumChatFormatting.RED + "You don't have permissions to use this command!");
				e.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onExplosion(ExplosionEvent.Start e)
	{
		if(EnkiTools.isSpawnChunkD(e.world.provider.dimensionId, e.explosion.explosionX, e.explosion.explosionZ)
		|| EnkiTools.isOutsideWorldBorderD(e.world.provider.dimensionId, e.explosion.explosionX, e.explosion.explosionZ)
		) e.setCanceled(true);
		else
		{
			EnkiData.Claim p = EnkiData.Claim.getClaimD(e.explosion.explosionX, e.explosion.explosionZ, e.world.provider.dimensionId);
			
			if(p != null && !p.playerClaims.canExplode)
				e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onReload(ReloadEvent e)
	{
		if(e.side.isClient()) return;
		
		EnkiToolsConfig.loadConfig();
		//FIXME: EnkiToolsTickHandler.instance.resetTimer(true);
		Rank.reload();
	}
	
	@SubscribeEvent
	public void customInfo(LMPlayerEvent.CustomInfo e)
	{
		if(e.side.isClient()) return;
		
		Rank r = Rank.getPlayerRank(e.player);
		e.info.add("Rank: " + r.getColor() + r.rankID);
	}
}