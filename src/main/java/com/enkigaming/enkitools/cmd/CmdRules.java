package com.enkigaming.enkitools.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;

import com.enkigaming.enkitools.EnkiToolsConfig;

public class CmdRules extends CmdEnki
{
	public CmdRules()
	{ super("rules"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{ if(!printRules(ics)) return "Link not set!"; return null; }
	
	public static boolean printRules(ICommandSender ics)
	{
		if(EnkiToolsConfig.Login.rules == null || EnkiToolsConfig.Login.rules.isEmpty()) return false;
		
		IChatComponent c = new ChatComponentText("[Click here to open rules]");
		c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, EnkiToolsConfig.Login.rules));
		c.getChatStyle().setColor(EnumChatFormatting.GOLD);
		ics.addChatMessage(c);
		return true;
	}
}