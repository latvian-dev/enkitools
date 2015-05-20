package latmod.enkitools;

import latmod.core.*;
import latmod.core.event.*;
import latmod.core.util.*;
import latmod.enkitools.EnkiData.Home;
import latmod.enkitools.cmd.CmdMotd;
import latmod.enkitools.rank.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.*;
import net.minecraft.event.*;
import net.minecraft.item.ItemStack;
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
		Rank.getPlayerRank(e.player);
		EnkiData.getData(e.player).updatePos(new Vertex.DimPos.Rot(e.playerMP));
		CmdMotd.printMotd(e.playerMP);
		
		if(e.firstTime && EnkiToolsConfig.get() != null && EnkiToolsConfig.get().login != null)
		{
			FastList<ItemStack> items = EnkiToolsConfig.get().login.startingInvI;
			if(items != null && !items.isEmpty()) for(ItemStack is : items)
				InvUtils.giveItem(e.playerMP, is);
		}
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
				
				{
					NBTTagCompound tag1 = (NBTTagCompound)tag.getTag("Warps");
					
					if(tag1 != null && !tag1.hasNoTags())
					{
						FastList<String> l = NBTHelper.getMapKeys(tag1);
						
						for(int i = 0; i < l.size(); i++)
						{
							int[] a = tag1.getIntArray(l.get(i));
							EnkiData.Warps.setWarp(l.get(i), a[0], a[1], a[2], a[3]);
						}
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
		
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			for(int i = 0; i < EnkiData.Warps.warps.size(); i++)
			{
				Home h1 = EnkiData.Warps.warps.get(i);
				tag1.setIntArray(h1.name, new int[] { h1.x, h1.y, h1.z, h1.dim });
			}
			
			tag.setTag("Warps", tag1);
		}
		
		NBTHelper.writeMap(e.getFile("EnkiMods.dat"), tag);
	}
	
	@SubscribeEvent
	public void playerLoggedOut(LMPlayerEvent.LoggedOut e)
	{
		EnkiData.Data d = EnkiData.getData(e.player);
		d.lastPos = new Vertex.DimPos(e.playerMP);
	}
	
	@SubscribeEvent
	public void onPlayerDeath(net.minecraftforge.event.entity.living.LivingDeathEvent e)
	{
		if(e.entity instanceof EntityPlayer)
		{
			LMPlayer p = LMPlayer.getPlayer(e.entity);
			EnkiData.Data d = EnkiData.getData(p);
			d.lastDeath = new Vertex.DimPos(e.entity);
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.world.isRemote) return;
		
		if(e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
		if(!canInteract(e)) e.setCanceled(true);
		else if(e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);
			
			if(te != null && !te.isInvalid() && te instanceof TileEntitySign)
			{
				TileEntitySign t = (TileEntitySign)te;
				
				if(EnkiToolsConfig.get().general.enableHomeSigns)
				{
					if(t.signText[1].equals("[home]"))
					{
						LatCoreMC.executeCommand(e.entityPlayer, "home " + t.signText[2]);
						e.setCanceled(true);
						return;
					}
				}
				
				if(EnkiToolsConfig.get().general.enableWarpSigns)
				{
					if(!t.signText[2].isEmpty() && t.signText[1].equals("[warp]"))
					{
						LatCoreMC.executeCommand(e.entityPlayer, "warp " + t.signText[2]);
						e.setCanceled(true);
						return;
					}
				}
			}
		}
	}
	
	private boolean canInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.entityPlayer.capabilities.isCreativeMode) return true;
		
		if(e.world.provider.dimensionId == 0 && EnkiToolsConfig.get().world.spawnDistance > 0F && EnkiTools.isSpawnChunkD(e.world, e.x, e.z))
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
		
		if((e.entity instanceof IMob || (e.entity instanceof EntityChicken && e.entity.riddenByEntity != null)) && EnkiTools.isSpawnChunkD(e.world, e.entity.posX, e.entity.posZ))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onPlayerAttacked(net.minecraftforge.event.entity.living.LivingAttackEvent e)
	{
		if(EnkiToolsConfig.get().world.spawnPVP || !EnkiTools.isSpawnChunkD(e.entity.worldObj, e.entity.posX, e.entity.posZ)) return;
		
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
		
		String[] msg = LatCore.split(e.message, " ");
		
		for(int i = 0; i < msg.length; i++)
		{
			IChatComponent c = new ChatComponentText(" " + msg[i]);
			
			if(msg[i].startsWith("http://") || msg[i].startsWith("https://"))
			{
				c = new ChatComponentText(" [Link]");
				c.getChatStyle().setColor(EnumChatFormatting.GOLD);
				c.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(msg[i])));
				c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, msg[i]));
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
		if(EnkiTools.isSpawnChunkD(e.world, e.explosion.explosionX, e.explosion.explosionZ)
		|| EnkiTools.isOutsideWorldBorder(e.world, e.explosion.explosionX, e.explosion.explosionZ)
		) e.setCanceled(true);
		else
		{
			EnkiData.Claim p = EnkiData.Claim.getClaimD(e.explosion.explosionX, e.explosion.explosionZ, e.world.provider.dimensionId);
			
			if(p != null && !p.playerClaims.canExplode)
				e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onReload(ReloadEvent r)
	{
		if(r.side.isServer())
		{
			EnkiToolsConfig.loadConfig();
			EnkiToolsTickHandler.instance.resetTimer(true);
			Rank.reload();
		}
	}
	
	@SubscribeEvent
	public void customInfo(LMPlayerEvent.CustomInfo e)
	{
		Rank r = Rank.getPlayerRank(e.player);
		e.info.add("Rank: " + EnumChatFormatting.YELLOW + r.rankID);
	}
}