package com.enkigaming.enkitools.cmd;

import latmod.core.LatCoreMC;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class CmdSpawn extends CmdEnki
{
	public CmdSpawn()
	{ super("spawn"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		ChunkCoordinates spawn = ep.worldObj.getSpawnPoint();
		
		while(ep.worldObj.getBlock(spawn.posX, spawn.posY, spawn.posZ).getCollisionBoundingBoxFromPool(ep.worldObj, spawn.posX, spawn.posY, spawn.posZ) != null)
			spawn.posY++;
		
		LatCoreMC.teleportPlayer(ep, spawn.posX + 0.5D, spawn.posY + 1D, spawn.posZ + 0.5D, ep.worldObj.provider.dimensionId);
		return FINE + "Teleported to spawn";
	}
}