package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import latmod.core.util.FastList;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.enkigaming.enkimods.EnkiModsConfig;

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
			LMPlayer p = LMPlayer.getPlayer(ics);
			NBTTagCompound map = p.customData.getCompoundTag("EnkiHomes");
			FastList<String> keys = NBTHelper.getMapKeys(map);
			keys.remove("Default"); keys.sort(null);
			return keys.isEmpty() ? null : keys.toArray(new String[0]);
		}
		
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayer p = LMPlayer.getPlayer(ep);
		NBTTagCompound map = p.customData.getCompoundTag("EnkiHomes");
		
		String name = args.length == 1 ? args[0] : "Default";
		int[] pos = map.getIntArray(name);
		
		if(pos.length == 4)
		{
			if(ep.worldObj.provider.dimensionId != pos[3])
			{
				if(EnkiModsConfig.General.crossDimHomes)
					LatCoreMC.teleportEntity(ep, pos[3]);
				else return "You can't teleport to another dimension!";
			}
			
			ep.playerNetServerHandler.setPlayerLocation(pos[0] + 0.5D, pos[1] + 0.5D, pos[2] + 0.5D, ep.rotationYaw, ep.rotationPitch);
			
			if(name.equals("Default")) return FINE + "Teleported to home";
			else return FINE + "Teleported to '" + name + "'";	
		}
		else return "Home not set!";
	}
}