package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import com.enkigaming.enkimods.rank.*;

public class CmdSethome extends CmdEnki
{
	public CmdSethome()
	{ super("sethome"); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/sethome [name]"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayer ep = getCommandSenderAsPlayer(ics);
		ChunkCoordinates c = ep.getPlayerCoordinates();
		LMPlayer p = LMPlayer.getPlayer(ep);
		
		NBTTagCompound map = p.serverData.getCompoundTag("EnkiHomes");
		
		int maxHomes = Rank.getConfig(ep, RankConfig.MAX_HOME_COUNT).getInt();
		if(maxHomes <= 0 || map.func_150296_c().size() > maxHomes)
			return "You can't set any more home locations!";
		
		String name = args.length == 1 ? args[0] : "Default";
		map.setIntArray(name, new int[] { c.posX, c.posY, c.posZ, ep.worldObj.provider.dimensionId });
		p.serverData.setTag("EnkiHomes", map);
		
		if(name.equals("Default"))
			return FINE + "Home set!";
		else
			return FINE + "Home '" + name + "' set!";
	}
}