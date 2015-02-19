package com.enkigaming.enkimods.cmd;

import latmod.core.util.LatCore;
import net.minecraft.command.ICommandSender;

import com.enkigaming.enkimods.EnkiModsTickHandler;

public class CmdShutdownTimer extends CmdEnki
{
	public CmdShutdownTimer()
	{ super("shutdownTimer"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{ return "Time left until next restart: " + LatCore.formatTime(EnkiModsTickHandler.instance.getSecondsUntilRestart(), false); }
}