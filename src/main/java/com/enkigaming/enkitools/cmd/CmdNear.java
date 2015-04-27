package com.enkigaming.enkitools.cmd;

import latmod.core.LatCoreMC;
import latmod.core.util.FastList;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.*;

import com.enkigaming.enkitools.EnkiToolsConfig;

public class CmdNear extends CmdEnki
{
	public CmdNear()
	{ super("near"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayer player = null;
		
		if(ics instanceof EntityPlayer)
			player = (EntityPlayer)ics;
		else
			player = getPlayer(ics, args[0]);
		
		int pn = 0;
		
		FastList<EntityPlayerMP> players = LatCoreMC.getAllOnlinePlayers().values;
		
		for(int i = 0; i < players.size(); i++)
		{
			EntityPlayer ep = players.get(i);
			
			if(ep.worldObj.provider.dimensionId == player.worldObj.provider.dimensionId && ep.getUniqueID() != player.getUniqueID())
			{
				double d = EnkiToolsConfig.General.nearDistance;
				double dist = ep.getDistanceSqToEntity(player);
				if(dist <= d * d)
				{
					if(pn == 0) LatCoreMC.printChat(ics, "Players near:"); pn++;
					LatCoreMC.printChat(ics, "[" + pn + "] " + ep.getDisplayName() + " [ " + ((int)Math.sqrt(dist)) + "m ]");
				}
			}
		}
		
		if(pn == 0) return FINE + "No near players found";
		return null;
	}
}