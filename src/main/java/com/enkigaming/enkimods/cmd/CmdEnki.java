package com.enkigaming.enkimods.cmd;

import latmod.core.cmd.CommandLM;
import net.minecraft.command.ICommandSender;

public abstract class CmdEnki extends CommandLM
{
	public CmdEnki(String s)
	{ super(s); }
	
	public final int getRequiredPermissionLevel()
	{ return 0; }
	
	public final boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return true; }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return null; }
	
	public void printHelp(ICommandSender ics)
	{
	}
}