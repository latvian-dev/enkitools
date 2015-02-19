package com.enkigaming.enkimods.cmd;

import java.util.List;

import latmod.core.*;
import latmod.core.util.FastList;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import com.enkigaming.enkimods.rank.Rank;

public class CmdHelpOverride extends CommandHelp
{
	public void processCommand(ICommandSender ics, String[] args)
	{
		try { super.processCommand(ics, args); }
		catch(Exception e)
		{
			e.printStackTrace();
			LatCoreMC.printChat(ics, EnumChatFormatting.RED + "Command failed! Blame LatvianModder!");
		}
	}
	
	@SuppressWarnings("all")
	protected List getSortedPossibleCommands(ICommandSender ics)
	{
		FastList<String> list = new FastList<String>();
		list.addAll(MinecraftServer.getServer().getCommandManager().getPossibleCommands(ics));
		
		FastList<String> list2 = new FastList<String>();
		
		if(ics instanceof EntityPlayer)
		{
			LMPlayer p = LMPlayer.getPlayer(ics);
			
			if(p.isOP()) list2.addAll(list);
			else
			{
				Rank r = Rank.getPlayerRank(p);
				
				for(int i = 0; i < list.size(); i++)
				{
					String c = list.get(i);
					
					try { if(c != null && c.length() > 0 && r.allowCommand(c, new String[0])) list2.add(c); }
					catch(Exception e) { e.printStackTrace(); }
				}
			}
		}
		else list2.addAll(list);
		
		list2.sort(null);
		return list2;
	}
}