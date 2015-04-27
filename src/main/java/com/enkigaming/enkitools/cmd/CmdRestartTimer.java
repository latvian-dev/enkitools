package com.enkigaming.enkitools.cmd;

import latmod.core.util.LatCore;
import net.minecraft.command.ICommandSender;

import com.enkigaming.enkitools.*;


public class CmdRestartTimer extends CmdEnki
{
	public CmdRestartTimer()
	{ super("restartTimer"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(!EnkiToolsConfig.General.enableRestartClock) return "Restart timer disabled!";
		return "Time left until next restart: " + LatCore.formatTime(EnkiToolsTickHandler.instance.getSecondsUntilRestart(), false);
	}
}