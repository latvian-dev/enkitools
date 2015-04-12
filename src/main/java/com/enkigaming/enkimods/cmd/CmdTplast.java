package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import latmod.core.util.Vertex.DimPos;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;

import com.enkigaming.enkimods.EnkiData;

public class CmdTplast extends CmdEnki
{
	public CmdTplast()
	{ super("tplast"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayer p = getLMPlayer(args[0]);
		
		if(p.isOnline()) LatCoreMC.executeCommand(ics, "tp " + args[0]);
		else
		{
			NBTTagCompound tag = (NBTTagCompound)p.serverData.getTag(EnkiData.TAG_LAST_POS);
			
			if(tag != null)
			{
				DimPos dp = new DimPos();
				dp.readFromNBT(tag);
				
				if(dp.dim != p.getPlayerMP().dimension) return "Can't teleport to another dimension!";
				LatCoreMC.executeCommand(ics, "tp " + dp.intX() + " " + dp.intY() + " " + dp.intZ());
			}
		}
		
		return null;
	}
}