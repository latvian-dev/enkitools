package latmod.enkitools;

import cpw.mods.fml.common.eventhandler.*;
import ftb.lib.FTBLib;
import ftb.lib.api.EventFTBReload;
import latmod.enkitools.rank.*;
import latmod.ftbu.api.EventLMPlayerServer;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraftforge.event.*;

public class EnkiToolsEventHandler // EnkiTools
{
	public static final EnkiToolsEventHandler instance = new EnkiToolsEventHandler();
	
	@SubscribeEvent
	public void onReloaded(EventFTBReload e)
	{ /*if(e.side.isServer() && e.sender != null) Rank.reload(); */ }
	
	@SubscribeEvent
	public void playerLoggedIn(EventLMPlayerServer.LoggedIn e)
	{ Rank.getPlayerRank(e.player); }
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChatEvent(ServerChatEvent e)
	{
		if(!EnkiToolsConfig.override_chat.get()) return;
		
		LMPlayerServer p = LMWorldServer.inst.getPlayer(e.player);
		if(p == null) return;
		
		Rank r = Rank.getPlayerRank(p);
		if(r == null) return;
		
		String name = r.getUsername(e.username);
		
		e.component = new ChatComponentTranslation("");
		IChatComponent nameC = new ChatComponentText(name);
		
		e.component.appendSibling(new ChatComponentText("<"));
		e.component.appendSibling(nameC);
		e.component.appendSibling(new ChatComponentText("> "));
		e.component.appendSibling(new ChatComponentText(e.message));
	}
	
	@SubscribeEvent
	public void onCommandEvent(CommandEvent e)
	{
		if(e.sender instanceof EntityPlayerMP)
		{
			LMPlayerServer p = LMWorldServer.inst.getPlayer(e.sender);
			Rank r = Rank.getPlayerRank(p);
			
			if(!r.allowCommand(new RankCommand(e.command.getCommandName(), e.parameters)))
			{
				ChatComponentTranslation c = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
                c.getChatStyle().setColor(EnumChatFormatting.RED);
                FTBLib.printChat(e.sender, c);
				e.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void customInfo(EventLMPlayerServer.CustomInfo e)
	{
		//Rank r = Rank.getPlayerRank(e.player);
		//if(r != null) e.info.add(new ChatComponentText(r.getColor() + '[' + r.rankID + ']'));
	}
}