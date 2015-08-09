package latmod.enkitools.cmd;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.inv.LMInvUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.*;

public class CmdHead extends CommandLM
{
	public CmdHead()
	{ super("head", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
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
		
		return "Invalid block!";
	}
}