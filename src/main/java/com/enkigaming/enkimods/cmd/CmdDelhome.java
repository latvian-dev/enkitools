package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;

public class CmdDelhome extends CmdEnki
{
	public CmdDelhome()
	{ super("delhome"); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/delhome [name]"); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0)
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			LMPlayer p = LMPlayer.getPlayer(ep);
			NBTTagCompound map = p.customData.getCompoundTag("EnkiHomes");
			FastList<String> keys = NBTHelper.getMapKeys(map);
			keys.remove("Default"); keys.sort(null);
			return keys.isEmpty() ? null : keys.toArray(new String[0]);
		}
		
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayer ep = getCommandSenderAsPlayer(ics);
		LMPlayer p = LMPlayer.getPlayer(ep);
		
		NBTTagCompound map = p.customData.getCompoundTag("EnkiHomes");
		
		String name = args.length == 1 ? args[0] : "Default";
		map.removeTag(name);
		p.customData.setTag("EnkiHomes", map);
		
		if(name.equals("Default"))
			return FINE + "Home deleted!";
		else
			return FINE + "Deleted '" + name + "'";
	}
}