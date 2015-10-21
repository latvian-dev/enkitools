package latmod.enkitools.cmd;

import ftb.lib.item.LMInvUtils;
import latmod.ftbu.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class CmdHead extends CommandLM
{
	public CmdHead()
	{ super("head", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(ics);
		
		ItemStack is = player.getHeldItem();
		
		if(is != null && is.getItem() instanceof ItemBlock)
		{
			if(player.inventory.armorInventory[3] != null)
			{
				LMInvUtils.giveItem(player, player.inventory.armorInventory[3].copy());
				player.inventory.armorInventory[3] = null;
			}
			
			player.inventory.armorInventory[3] = LMInvUtils.singleCopy(player.getHeldItem());
			
			player.inventory.mainInventory[player.inventory.currentItem] = LMInvUtils.reduceItem(player.inventory.mainInventory[player.inventory.currentItem]);
			player.inventory.markDirty();
			return null;
		}
		
		return error(new ChatComponentText("Invalid block!")); //TODO: Remove
	}
}