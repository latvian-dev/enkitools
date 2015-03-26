package com.enkigaming.enkimods;

import java.io.File;
import java.util.UUID;

import latmod.core.*;
import latmod.core.event.*;
import latmod.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.*;
import net.minecraft.event.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;

import com.enkigaming.enkimods.Mailbox.Mail;
import com.enkigaming.enkimods.PlayerClaims.Claim;
import com.enkigaming.enkimods.cmd.CmdMotd;
import com.enkigaming.enkimods.rank.*;

import cpw.mods.fml.common.eventhandler.*;

public class EnkiModsEventHandler
{
	public static final EnkiModsEventHandler instance = new EnkiModsEventHandler();
	public static final FastMap<UUID, String> customNames = new FastMap<UUID, String>();
	
	@SubscribeEvent
	public void playerLoggedIn(LMPlayerEvent.LoggedIn e)
	{
		Rank.getPlayerRank(e.player);
		
		CmdMotd.printMotd(e.playerMP);
		int c = Mailbox.getMailFor(e.player.uuid).size();
		if(c > 0) printIncomingMail(e.playerMP, c);
		
		if(e.firstTime)
		{
			for(int i = 0; i < EnkiModsConfig.Login.startingInv.size(); i++)
				InvUtils.giveItem(e.playerMP, EnkiModsConfig.Login.startingInv.get(i));
		}
		
		EnkiModsTickHandler.TrackedPlayer.get(e.playerMP);
	}
	
	@SubscribeEvent
	public void loadLMData(LoadLMDataEvent e)
	{
		if(e.phase.isPre())
		{
			Rank.reload();
			PlayerClaims.claimsMap.clear();
			
			customNames.clear();
			
			try
			{
				FastList<String> l = LatCore.loadFile(new File(LatCoreMC.latmodFolder, "EnkiMods/CustomNames.txt"));
				
				for(int i = 0; i < l.size(); i++)
				{
					String[] s = LatCore.split(l.get(i), ": ", 2);
					customNames.put(UUID.fromString(s[0]), s[1]);
				}
			}
			catch(Exception ex) { }
		}
		
		if(e.phase.isPost())
		{
			Mailbox.mailMap.clear();
			
			NBTTagCompound tag = NBTHelper.readMap(e.getFile("EnkiMods.dat"));
			
			if(tag != null)
			{
				NBTTagCompound players = tag.getCompoundTag("Players");
				
				for(int i = 0; i < LMPlayer.map.size(); i++)
				{
					LMPlayer p = LMPlayer.map.values.get(i);
					NBTTagCompound tag1 = players.getCompoundTag("" + p.playerID);
					
					if(!tag1.hasNoTags())
					{
						PlayerClaims pc = PlayerClaims.getClaims(p);
						pc.readFromNBT(tag1.getCompoundTag("Claims"));
					}
				}
				
				NBTTagList mail = (NBTTagList)tag.getTag("Mail");
				
				if(mail != null && mail.tagCount() > 0)
				{
					for(int i = 0; i < mail.tagCount(); i++)
					{
						NBTTagCompound tag1 = mail.getCompoundTagAt(i);
						
						int id = tag1.getInteger("ID");
						Mail m = new Mail(id);
						m.readFromNBT(tag1);
						Mailbox.mailMap.put(id, m);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void loadCommonLMData(LoadLMDataEvent.CommonData e)
	{
		Mailbox.nextID = e.tag.getInteger("EnkiMailNextID");
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
			
			PlayerClaims pc = PlayerClaims.getClaims(p);
			
			if(pc != null && pc.shouldSave())
			{
				NBTTagCompound tag2 = new NBTTagCompound();
				pc.writeToNBT(tag2);
				tag1.setTag("Claims", tag2);
			}
			
			players.setTag("" + p.playerID, tag1);
		}
		
		tag.setTag("Players", players);
		
		NBTTagList mail = new NBTTagList();
		
		for(int i = 0; i < Mailbox.mailMap.size(); i++)
		{
			Mail m = Mailbox.mailMap.values.get(i);
			
			NBTTagCompound tag1 = new NBTTagCompound();
			
			tag1.setInteger("ID", m.mailID);
			m.writeToNBT(tag1);
			mail.appendTag(tag1);
		}
		
		tag.setTag("Mail", mail);
		
		NBTHelper.writeMap(e.getFile("EnkiMods.dat"), tag);
	}
	
	@SubscribeEvent
	public void saveCommonLMData(SaveLMDataEvent.CommonData e)
	{
		e.tag.setInteger("EnkiMailNextID", Mailbox.nextID);
	}
	
	@SubscribeEvent
	public void loadPlayerData(LMPlayerEvent.DataLoaded e)
	{
		if(e.player.customData.hasKey("EnkiClaims"))
		{
			NBTTagCompound tag = e.player.customData.getCompoundTag("EnkiClaims");
			PlayerClaims pc = new PlayerClaims(e.player);
			pc.readFromNBT(tag);
			PlayerClaims.claimsMap.put(pc.owner.playerID, pc);
			e.player.customData.removeTag("EnkiClaims");
		}
	}
	
	public void printIncomingMail(EntityPlayerMP ep, int m)
	{ LatCoreMC.notifyPlayer(ep, new Notification(m + " New message" + MathHelperLM.getPluralWord(m, "!", "s!"), EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "EnkiMail", new ItemStack(Items.writable_book), 10000L)); }
	
	@SubscribeEvent
	public void playerLoggedOut(LMPlayerEvent.LoggedOut e)
	{
		NBTTagCompound tag = new NBTTagCompound();
		new Vertex.DimPos(e.playerMP).writeToNBT(tag);
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
		if(!canInteract(e)) e.setCanceled(true);
	}
	
	private boolean canInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.entityPlayer.capabilities.isCreativeMode) return true;
		
		if(e.world.provider.dimensionId == 0 && EnkiModsConfig.WorldCategory.spawnDistance > 0F && EnkiMods.isSpawnChunkD(e.world, e.x, e.z))
		{
			if(Rank.getConfig(e.entityPlayer, RankConfig.IGNORE_SPAWN).getBool()) return true;
			
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
		if(e.entity instanceof EntityPlayer && e.source instanceof EntityDamageSource)
		{
			Entity e1 = ((EntityDamageSource)e.source).getEntity();
			
			if(e1 instanceof EntityPlayerMP && !(e1 instanceof FakePlayer))
			{
				EntityPlayerMP ep = (EntityPlayerMP)e1;
				
				if(EnkiMods.isSpawnChunkD(e.entity.worldObj, e.entity.posX, e.entity.posZ) && !ep.capabilities.isCreativeMode && !Rank.getConfig(ep, RankConfig.IGNORE_SPAWN).getBool())
					e.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChatEvent(ServerChatEvent e)
	{
		LMPlayer p = LMPlayer.getPlayer(e.username);
		if(p == null) return;
		
		Rank r = Rank.getPlayerRank(p);
		
		String name = EnkiModsEventHandler.customNames.get(e.player.getUniqueID());
		if(name == null) name = e.username + "";
		
		if(r.prefix != null && r.prefix.length() > 0)
			name = r.prefix.replace("c_", LatCoreMC.FORMATTING) + name;
		
		if(name.contains(LatCoreMC.FORMATTING)) name = name + EnumChatFormatting.RESET;
		
		e.component = new ChatComponentTranslation("");
		
		IChatComponent nameC = new ChatComponentText(name);
		
		nameC.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("/tell " + name)));
		nameC.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + name + " "));
		
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
	
	/*
	@SubscribeEvent
	public void customInfo(LMPlayerEvent.CustomInfo e)
	{
		Rank r = Rank.getPlayerRank(e.player);
		
		if(r != Rank.getDefaultRank())
			e.player.clientInfo.add("Rank: " + r.prefix.replace("c_", LatCoreMC.FORMATTING) + r.rankID);
		
		PlayerClaims pc = PlayerClaims.getClaims(e.player);
		if(pc.claims.size() > 0)
			e.player.clientInfo.add("Claimed chunks: " + pc.claims.size() + " / " + pc.getMaxPower());
	}
	*/
}