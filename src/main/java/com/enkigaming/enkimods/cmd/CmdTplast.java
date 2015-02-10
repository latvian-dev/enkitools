package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;

public class CmdTplast extends CmdEnki
{
	public CmdTplast()
	{ super("tplast"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayer p = getLMPlayer(args[0]);
		
		if(p.isOnline()) LatCoreMC.executeCommand(ics, "tp", args[0]);
		else
		{
			NBTTagCompound tag = (NBTTagCompound)p.customData.getTag("LastSavedPos");
			
			if(tag != null)
			{
				double x = tag.getDouble("X");
				double y = tag.getDouble("Y");
				double z = tag.getDouble("Z");
				int dim = tag.getInteger("Dim");
				
				if(dim != p.getPlayerMP().dimension)
					return "Can't teleport to another dimension!";
				
				LatCoreMC.executeCommand(ics, "tp", new String[] { "" + x, "" + y, "" + z });
			}
		}
		
		return null;
	}
}