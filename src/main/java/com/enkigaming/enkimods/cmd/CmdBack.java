package com.enkigaming.enkimods.cmd;

import latmod.core.LMPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import com.enkigaming.enkimods.EnkiData;

public class CmdBack extends CmdEnki
{
	public CmdBack()
	{ super("back"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayer p = LMPlayer.getPlayer(ep);
		EnkiData.Data d = EnkiData.getData(p);
		if(d.lastDeath == null) return "No deathpoint found!";
		if(d.lastDeath.dim != ep.worldObj.provider.dimensionId) return "You can't teleport to another dimension!";
		else ep.playerNetServerHandler.setPlayerLocation(d.lastDeath.pos.x, d.lastDeath.pos.y, d.lastDeath.pos.z, 0F, 0F);
		return null;
	}
}