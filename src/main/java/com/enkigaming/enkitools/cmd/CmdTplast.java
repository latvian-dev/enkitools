package com.enkigaming.enkitools.cmd;

import latmod.core.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import com.enkigaming.enkitools.EnkiData;

public class CmdTplast extends CmdEnki
{
	public CmdTplast()
	{ super("tpl"); }
	
	public NameType getUsername(String[] args, int i)
	{ if(i == 0) return NameType.OFF; return NameType.NONE; }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		LMPlayer p = getLMPlayer(args[0]);
		
		if(p.isOnline())
		{
			EntityPlayerMP ep1 = p.getPlayerMP();
			LatCoreMC.teleportPlayer(ep, ep1.posX, ep1.posY, ep1.posZ, ep1.dimension);
		}
		else
		{
			EnkiData.Data d = EnkiData.getData(p);
			if(d.lastPos == null) return "No last position!";
			LatCoreMC.teleportPlayer(ep, d.lastPos.pos.x, d.lastPos.pos.y, d.lastPos.pos.z, d.lastPos.dim);
		}
		
		return FINE + "Teleported to " + p.username + "!";
	}
}