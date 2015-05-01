package latmod.enkitools.cmd;

import latmod.core.*;
import latmod.enkitools.EnkiData;
import latmod.enkitools.rank.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class CmdSethome extends CmdEnki
{
	public CmdSethome()
	{ super("sethome"); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/sethome [name]"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayer p = LMPlayer.getPlayer(ep);
		EnkiData.Data h = EnkiData.getData(p);
		ChunkCoordinates c = ep.getPlayerCoordinates();
		
		int maxHomes = Rank.getConfig(ep, RankConfig.MAX_HOME_COUNT).getInt();
		if(maxHomes <= 0 || h.homesSize() >= maxHomes)
			return "You can't set any more home locations!";
		
		String name = args.length == 1 ? args[0] : "Default";
		h.addHome(name, c.posX, c.posY, c.posZ, ep.dimension);
		
		if(name.equals("Default"))
			return FINE + "Home set!";
		else
			return FINE + "Home '" + name + "' set!";
	}
}