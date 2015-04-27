package com.enkigaming.enkitools.cmd;

import latmod.core.LatCoreMC;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import com.enkigaming.enkitools.EnkiToolsConfig;

public class CmdMotd extends CmdEnki
{
	public CmdMotd()
	{ super("motd"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{ printMotd(getCommandSenderAsPlayer(ics)); return null; }
	
	public static void printMotd(EntityPlayerMP ep)
	{
		if(!EnkiToolsConfig.Login.motd.isEmpty()) for(String s : EnkiToolsConfig.Login.motd)
			LatCoreMC.printChat(ep, s.replace("&", LatCoreMC.FORMATTING).replace("<PlayerName>", ep.getDisplayName()));
		CmdRules.printRules(ep);
	}
}