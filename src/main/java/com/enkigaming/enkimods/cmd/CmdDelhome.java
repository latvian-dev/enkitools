package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import net.minecraft.command.ICommandSender;

import com.enkigaming.enkimods.EnkiData;

public class CmdDelhome extends CmdEnki
{
	public CmdDelhome()
	{ super("delhome"); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/delhome [name]"); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0)
		{
			LMPlayer p = LMPlayer.getPlayer(getCommandSenderAsPlayer(ics));
			EnkiData.Homes h = new EnkiData.Homes(p);
			return h.list(true);
		}
		
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayer p = LMPlayer.getPlayer(getCommandSenderAsPlayer(ics));
		EnkiData.Homes h = new EnkiData.Homes(p);
		
		String name = args.length == 1 ? args[0] : "Default";
		
		if(h.homes.remove(name))
		{
			if(name.equals("Default"))
				return FINE + "Home deleted!";
			else
				return FINE + "Deleted '" + name + "'";
		}
		
		return "Home '" + name + "' not set!";
	}
}