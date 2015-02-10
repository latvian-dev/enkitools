package com.enkigaming.enkimods.cmd;

import latmod.core.LatCoreMC;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;

import com.enkigaming.enkimods.EnkiModsConfig;

public class CmdMotd extends CmdEnki
{
	public CmdMotd()
	{ super("motd"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(ics instanceof EntityPlayer)
			printMotd((EntityPlayer)ics);
		else throw new PlayerNotFoundException();
		
		return null;
	}
	
	public static void printMotd(EntityPlayer ep)
	{
		if(!EnkiModsConfig.Login.motd.isEmpty()) for(String s : EnkiModsConfig.Login.motd)
			LatCoreMC.printChat(ep, s.replace("&", LatCoreMC.FORMATTING).replace("<PlayerName>", ep.getDisplayName()));
		
		LatCoreMC.executeCommand(ep, "rules");
	}
}