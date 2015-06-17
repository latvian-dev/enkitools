package latmod.enkitools.cmd;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LatCore;
import net.minecraft.command.ICommandSender;

public class CmdShutdown extends CommandLM
{
	public CmdShutdown()
	{ super("shutdown", CommandLevel.OP); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 2);
		
		int sec = 60;
		
		if(args.length == 2)
		{
			if(args[1].contains(":"))
			{
				String s[] = args[1].split(":");
				int h = Integer.parseInt(s[0]);
				int m = Integer.parseInt(s[1]);
				sec = h * 3600 + m * 60;
			}
			else sec = parseInt(ics, args[1]);
		}
		
		EnkiToolsTickHandler.instance.forceShutdown(sec);
		LatCoreMC.printChat(ics, "Forced server restart after " + LatCore.formatTime(EnkiToolsTickHandler.instance.getSecondsUntilRestart() - 1, false), true);
		return null;
	}
}