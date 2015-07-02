package latmod.enkitools.cmd;

import latmod.enkitools.EnkiData;
import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdBack extends CommandLM
{
	public CmdBack()
	{ super("back", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayer p = LMPlayer.getPlayer(ep);
		EnkiData.Data d = EnkiData.getData(p);
		if(d.lastDeath == null) return "No deathpoint found!";
		Teleporter.travelEntity(ep, d.lastDeath);
		return null;
	}
}