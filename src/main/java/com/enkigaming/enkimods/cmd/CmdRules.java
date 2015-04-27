package com.enkigaming.enkimods.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;

import com.enkigaming.enkimods.EnkiModsConfig;

public class CmdRules extends CmdEnki
{
	public CmdRules()
	{ super("rules"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{ if(!printRules(ics)) return "Link not set!"; return null; }
	
	public static boolean printRules(ICommandSender ics)
	{
		if(EnkiModsConfig.Login.rules == null || EnkiModsConfig.Login.rules.isEmpty()) return false;
		
		IChatComponent c = new ChatComponentText("[Click here to open rules]");
		c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, EnkiModsConfig.Login.rules));
		c.getChatStyle().setColor(EnumChatFormatting.GOLD);
		ics.addChatMessage(c);
		return true;
	}
}