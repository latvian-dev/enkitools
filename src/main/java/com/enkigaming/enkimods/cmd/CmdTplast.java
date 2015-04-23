package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import net.minecraft.command.ICommandSender;

import com.enkigaming.enkimods.EnkiData;

public class CmdTplast extends CmdEnki
{
	public CmdTplast()
	{ super("tplast"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		LMPlayer p = getLMPlayer(args[0]);
		
		if(p.isOnline()) LatCoreMC.executeCommand(ics, "tp", new String[]{ args[0] });
		else
		{
			EnkiData.Data d = EnkiData.getData(p);
			if(d.lastPos == null) return "No last position!";
			
			if(d.lastPos.dim != p.getPlayerMP().dimension) return "Can't teleport to another dimension!";
			LatCoreMC.executeCommand(ics, "tp", new String[]{ d.lastPos.pos.x + "", d.lastPos.pos.y + "", d.lastPos.pos.z + "" });
		}
		
		return null;
	}
}