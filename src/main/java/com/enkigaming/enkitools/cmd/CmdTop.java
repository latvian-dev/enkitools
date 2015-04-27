package com.enkigaming.enkitools.cmd;

import latmod.core.util.MathHelperLM;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdTop extends CmdEnki
{
	public CmdTop()
	{ super("top"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		int x = MathHelperLM.floor(ep.posX);
		int z = MathHelperLM.floor(ep.posZ);
		int y = ics.getEntityWorld().getHeightValue(x, z) + 1;
		ep.playerNetServerHandler.setPlayerLocation(x + 0.5D, y, z + 0.5D, ep.rotationYaw, ep.rotationPitch);
		return null;
	}
}