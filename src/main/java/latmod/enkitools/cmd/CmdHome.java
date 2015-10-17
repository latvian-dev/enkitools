package latmod.enkitools.cmd;

import latmod.enkitools.EnkiData;
import latmod.enkitools.config.EnkiToolsConfigGeneral;
import latmod.enkitools.rank.*;
import latmod.ftbu.cmd.*;
import latmod.ftbu.util.*;
import latmod.ftbu.world.LMPlayerServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdHome extends CommandLM
{
	public CmdHome()
	{ super("home", CommandLevel.ALL); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0 || (i == 1 && isArg(args, 0, "set", "del"))) return EnkiData.Homes.listHomes(getLMPlayer(ics));
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		if(args.length == 0)
		{
			LatCoreMC.printChat(ics, "/home <name>");
			LatCoreMC.printChat(ics, "/home set <name>");
			LatCoreMC.printChat(ics, "/home del <name>");
			return null;
		}
		
		LMPlayerServer p = getLMPlayer(ep);
		
		if(args[0].equals("set"))
		{
			checkArgs(args, 2);
			
			int maxHomes = Rank.getConfig(p, RankConfig.MAX_HOME_COUNT).getInt();
			if(maxHomes <= 0 || EnkiData.Homes.homesSize(p) >= maxHomes)
				return error(new ChatComponentText("You can't set any more home locations!"));
			
			EnkiData.Homes.setHome(p, args[1], p.getPos());
			return new ChatComponentText("Home '" + args[1] + "' set!");
		}
		
		if(args[0].equals("del"))
		{
			checkArgs(args, 2);
			
			if(EnkiData.Homes.remHome(p, args[1]))
				return new ChatComponentText("Deleted '" + args[1] + "'");
			return error(new ChatComponentText("Home '" + args[1] + "' not set!"));
		}
		
		EntityPos pos = EnkiData.Homes.getHome(p, args[0]);
		
		if(pos == null) return error(new ChatComponentText("Home '" + args[0] + "' not set!"));
		
		if(ep.dimension != pos.dim && !EnkiToolsConfigGeneral.crossDimHomes.get())
			return error(new ChatComponentText("You can't teleport to another dimension!"));
		
		LMDimUtils.teleportPlayer(ep, pos);
		return new ChatComponentText("Teleported to '" + args[0] + "'");
	}
}