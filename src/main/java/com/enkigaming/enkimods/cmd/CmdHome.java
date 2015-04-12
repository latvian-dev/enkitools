package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import com.enkigaming.enkimods.*;

public class CmdHome extends CmdEnki
{
	public CmdHome()
	{ super("home"); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/home [name]"); }
	
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
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayer p = LMPlayer.getPlayer(ep);
		EnkiData.Homes h = new EnkiData.Homes(p);
		
		String name = args.length == 1 ? args[0] : "Default";
		
		EnkiData.Homes.Home h1 = h.homes.get(name);
		
		if(h1 == null) return "Home '" + name + "' not set!";
		
		if(ep.worldObj.provider.dimensionId != h1.dim)
		{
			if(EnkiModsConfig.General.crossDimHomes)
				LatCoreMC.teleportEntity(ep, h1.dim);
			else return "You can't teleport to another dimension!";
		}
		
		ep.playerNetServerHandler.setPlayerLocation(h1.x + 0.5D, h1.y + 0.5D, h1.z + 0.5D, ep.rotationYaw, ep.rotationPitch);
		
		if(name.equals("Default")) return FINE + "Teleported to home";
		else return FINE + "Teleported to '" + name + "'";
	}
}