package com.enkigaming.enkimods.cmd;

import latmod.core.LMPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class CmdBack extends CmdEnki
{
	public CmdBack()
	{ super("back"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayer p = LMPlayer.getPlayer(ep);
		NBTTagCompound map = p.serverData.getCompoundTag("LastDeath");
		if(map.hasNoTags()) return null;
		if(map.getInteger("Dim") != ep.worldObj.provider.dimensionId) return "You can't teleport to another dimension!";
		else ep.playerNetServerHandler.setPlayerLocation(map.getDouble("X"), map.getDouble("Y") + 0.5D, map.getDouble("Z"), 0F, 0F);
		return null;
	}
}