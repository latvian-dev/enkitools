package com.enkigaming.enkimods.cmd;

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
		
		ep.playerNetServerHandler.setPlayerLocation(spawn.posX + 0.5D, spawn.posY + 1D, spawn.posZ + 0.5D, ep.rotationYaw, ep.rotationPitch);
		
		return FINE + "Teleported to spawn";
	}
}