package latmod.enkitools.cmd;

import latmod.core.util.LatCore;
import latmod.enkitools.*;
import net.minecraft.command.ICommandSender;


public class CmdRestartTimer extends CmdEnki
{
	public CmdRestartTimer()
	{ super("restartTimer"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(!EnkiToolsConfig.get().general.enableRestartClock) return "Restart timer disabled!";
		return "Time left until next restart: " + LatCore.formatTime(EnkiToolsTickHandler.instance.getSecondsUntilRestart(), false);
	}
}