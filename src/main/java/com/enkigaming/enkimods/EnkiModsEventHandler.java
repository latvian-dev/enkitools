package com.enkigaming.enkimods;

import latmod.core.*;
import latmod.core.event.*;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.*;
import net.minecraft.event.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;

import com.enkigaming.enkimods.PlayerClaims.Claim;
import com.enkigaming.enkimods.cmd.CmdMotd;
import com.enkigaming.enkimods.rank.*;

import cpw.mods.fml.common.eventhandler.*;

public class EnkiModsEventHandler
{
	public static final EnkiModsEventHandler instance = new EnkiModsEventHandler();
	public static final RankConfig IGNORE_SPAWN = new RankConfig("ignoreSpawnProtection", "false");
	
	@SubscribeEvent
	public void playerLoggedIn(LMPlayerEvent.LoggedIn e)
	{
		if(e.side.isClient()) return;
		EntityPlayerMP ep = (EntityPlayerMP) e.entityPlayer;
		
		Rank.getPlayerRank(e.player);
		
		CmdMotd.printMotd(ep);
		int c = Mailbox.getMailFor(e.player.uuid).size();
		if(c > 0) printIncomingMail(ep, c);
		
		if(e.firstTime)
		{
			for(int i = 0; i < EnkiModsConfig.Login.startingInv.size(); i++)
				InvUtils.giveItem(ep, EnkiModsConfig.Login.startingInv.get(i));
		}
		
		EnkiModsTickHandler.TrackedPlayer.get(ep);
	}
	
	@SubscribeEvent
	public void loadLMData(LoadCustomLMDataEvent e)
	{
		if(e.phase.isPre())
		{
			Rank.reload();
			PlayerClaims.claimsMap.clear();
		}
		
		if(e.phase.isPost())
		{
			Mailbox.readFromNBT(e.tag);
		}
	}
	
	@SubscribeEvent
	public void saveLMData(SaveCustomLMDataEvent e)
	{
		Mailbox.writeToNBT(e.tag);
	}
	
	@SubscribeEvent
	public void loadPlayerData(LMPlayerEvent.DataLoaded e)
	{
		PlayerClaims.loadPlayerClaims(e.player);
	}
	
	@SubscribeEvent
	public void savePlayerData(LMPlayerEvent.DataSaved e)
	{
		PlayerClaims.savePlayerClaims(e.player);
	}
	
	public void printIncomingMail(EntityPlayerMP ep, int m)
	{ LatCoreMC.notifyPlayer(ep, new Notification(m + " New message" + MathHelperLM.getPluralWord(m, "!", "s!"), EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "EnkiMail", new ItemStack(Items.writable_book), 10000L)); }
	
	@SubscribeEvent
	public void playerLoggedOut(LMPlayerEvent.LoggedOut e)
	{
		if(e.side.isClient()) return;
		
		NBTTagCompound tag = new NBTTagCompound();
		new Vertex.DimPos(e.entityPlayer).writeToNBT(tag);
		e.player.customData.setTag("LastSavedPos", tag);
	}
	
	@SubscribeEvent
	public void onPlayerDeath(net.minecraftforge.event.entity.living.LivingDeathEvent e)
	{
		if(e.entity instanceof EntityPlayer)
		{
			LMPlayer p = LMPlayer.getPlayer(e.entity);
			NBTTagCompound tag = new NBTTagCompound();
			new Vertex.DimPos(e.entity).writeToNBT(tag);
			p.customData.setTag("LastDeath", tag);
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.world.isRemote) return;
		
		if(e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
		if(!canInteract(e))
		{
			if(!e.entityPlayer.capabilities.isCreativeMode)
				e.setCanceled(true);
		}
	}
	
	private boolean canInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.entityPlayer.capabilities.isCreativeMode) return true;
		
		if(e.world.provider.dimensionId == 0 && EnkiMods.isSpawnChunkD(e.world, e.x, e.z))
		{
			boolean ignoreSpawn = Rank.getConfig(e.entityPlayer, IGNORE_SPAWN).getBool();
			
			if(ignoreSpawn) return true;
			
			if(e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && EnkiModsConfig.WorldCategory.spawnBreakWhitelist.contains(getName(e)))
				return true;
			
			if(e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && EnkiModsConfig.WorldCategory.spawnInteractWhitelist.contains(getName(e)))
				return true;
			
			return false;
		}
		
		Claim cc = PlayerClaims.getClaimD(e.x, e.z, e.entity.dimension);
		
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
		if((e.entity instanceof IMob || (e.entity instanceof EntityChicken && e.entity.riddenByEntity != null)) && EnkiMods.isSpawnChunkD(e.world, e.entity.posX, e.entity.posZ))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onPlayerAttacked(net.minecraftforge.event.entity.living.LivingAttackEvent e)
	{
		if(e.entity instanceof EntityPlayer
		&& e.source instanceof EntityDamageSource
		&& ((EntityDamageSource)e.source).getEntity() instanceof EntityPlayer
		&& EnkiMods.isSpawnChunkD(e.entity.worldObj, e.entity.posX, e.entity.posZ)
		&& !((EntityPlayer)((EntityDamageSource)e.source).getEntity()).capabilities.isCreativeMode
		&& !Rank.getConfig(((EntityDamageSource)e.source).getEntity(), IGNORE_SPAWN).getBool())
			e.setCanceled(true);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChatEvent(ServerChatEvent e)
	{
		LMPlayer p = LMPlayer.getPlayer(e.username);
		if(p == null) return;
		
		Rank r = Rank.getPlayerRank(p);
		
		String name = e.username + "";
		
		if(r.prefix != null && r.prefix.length() > 0)
			name = r.prefix.replace("c_", LatCoreMC.FORMATTING) + name;
		
		if(name.contains(LatCoreMC.FORMATTING)) name = name + EnumChatFormatting.RESET;
		
		e.component = new ChatComponentTranslation("");
		
		IChatComponent nameC = new ChatComponentText(name);
		
		nameC.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("/tell " + e.username)));
		nameC.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + e.username + " "));
		
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
		if(EnkiMods.isSpawnChunkD(e.world, e.explosion.explosionX, e.explosion.explosionZ)
		|| EnkiMods.isOutsideWorldBorder(e.world, e.explosion.explosionX, e.explosion.explosionZ)
		) e.setCanceled(true);
		else
		{
			PlayerClaims.Claim p = PlayerClaims.getClaimD(e.explosion.explosionX, e.explosion.explosionZ, e.world.provider.dimensionId);
			
			if(p != null && !p.playerClaims.canExplode)
				e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onReload(ReloadEvent r)
	{
		if(r.side.isServer())
		{
			EnkiMods.mod.config.reload();
			EnkiModsTickHandler.instance.resetTimer(true);
			
			Rank.reload();
			
			FastList<EntityPlayerMP> players = LatCoreMC.getAllOnlinePlayers().values;
			for(int i = 0;i < players.size(); i++)
				players.get(i).refreshDisplayName();
		}
	}
}