package latmod.enkitools.cmd;

import latmod.enkitools.EnkiToolsConfig;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LatCore;
import net.minecraft.command.ICommandSender;


public class CmdRestartTimer extends CommandLM
{
	public CmdRestartTimer()
	{ super("restartTimer", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(!EnkiToolsConfig.get().general.enableRestartClock) return "Restart timer disabled!";
		return "Time left until next restart: " + LatCore.formatTime(EnkiToolsTickHandler.instance.getSecondsUntilRestart(), false);
	}
}