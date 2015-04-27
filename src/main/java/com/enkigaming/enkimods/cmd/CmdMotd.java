package com.enkigaming.enkimods.cmd;

import latmod.core.LatCoreMC;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import com.enkigaming.enkimods.EnkiModsConfig;

public class CmdMotd extends CmdEnki
{
	public CmdMotd()
	{ super("motd"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{ printMotd(getCommandSenderAsPlayer(ics)); return null; }
	
	public static void printMotd(EntityPlayerMP ep)
	{
		if(!EnkiModsConfig.Login.motd.isEmpty()) for(String s : EnkiModsConfig.Login.motd)
			LatCoreMC.printChat(ep, s.replace("&", LatCoreMC.FORMATTING).replace("<PlayerName>", ep.getDisplayName()));
		CmdRules.printRules(ep);
	}
}