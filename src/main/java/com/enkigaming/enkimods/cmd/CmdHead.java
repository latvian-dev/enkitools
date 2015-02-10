package com.enkigaming.enkimods.cmd;

import latmod.core.InvUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;

public class CmdHead extends CmdEnki
{
	public CmdHead()
	{ super("head"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(ics);
		
		ItemStack is = player.getHeldItem();
		
		if(is != null)
		{
			if(is.getItem() instanceof ItemBlock)
			{
				if(player.inventory.armorInventory[3] != null)
				{
					InvUtils.dropItem(player, player.inventory.armorInventory[3].copy());
					player.inventory.armorInventory[3] = null;
				}
				
				player.inventory.armorInventory[3] = InvUtils.singleCopy(player.getHeldItem());
				
				player.inventory.mainInventory[player.inventory.currentItem] = InvUtils.reduceItem(player.inventory.mainInventory[player.inventory.currentItem]);
				player.inventory.markDirty();
			}
			else if(is.getItem() instanceof ItemSkull && is.getItemDamage() == 3)
			{
				if(is.stackTagCompound == null)
					is.stackTagCompound = new NBTTagCompound();
				
				is.stackTagCompound.setString("SkullOwner", player.getCommandSenderName());
				
				player.inventory.markDirty();
			}
		}
		
		return null;
	}
}