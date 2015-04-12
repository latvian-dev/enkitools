package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import com.enkigaming.enkimods.EnkiData;
import com.enkigaming.enkimods.rank.*;

public class CmdSethome extends CmdEnki
{
	public CmdSethome()
	{ super("sethome"); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/sethome [name]"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayer p = LMPlayer.getPlayer(ep);
		EnkiData.Homes h = new EnkiData.Homes(p);
		ChunkCoordinates c = ep.getPlayerCoordinates();
		
		int maxHomes = Rank.getConfig(ep, RankConfig.MAX_HOME_COUNT).getInt();
		if(maxHomes <= 0 || h.homes.size() >= maxHomes)
			return "You can't set any more home locations!";
		
		String name = args.length == 1 ? args[0] : "Default";
		EnkiData.Homes.Home h1 = new EnkiData.Homes.Home(name, c.posX, c.posY, c.posZ, ep.dimension);
		h.homes.put(h1.name, h1);
		h.save();
		
		if(name.equals("Default"))
			return FINE + "Home set!";
		else
			return FINE + "Home '" + name + "' set!";
	}
}