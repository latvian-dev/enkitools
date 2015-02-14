package com.enkigaming.enkimods.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.*;


public class CmdVanish extends CmdEnki
{
	public CmdVanish()
	{ super("vanish"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayer ep = getCommandSenderAsPlayer(ics);
		
		if(ep.getActivePotionEffect(Potion.invisibility) != null)
			ep.removePotionEffect(Potion.invisibility.id);
		else ep.addPotionEffect(new PotionEffect(Potion.invisibility.id, Integer.MAX_VALUE, 0, true));
		return null;
	}
}