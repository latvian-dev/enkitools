package latmod.enkitools.cmd;

import net.minecraft.command.*;

import java.util.List;

@SuppressWarnings("all")
public class CmdOverride implements ICommand
{
	public final ICommand command;
	
	public CmdOverride(ICommand c)
	{ command = c; }
	
	public String getCommandName()
	{ return command.getCommandName(); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return command.getCommandUsage(ics); }
	
	public void processCommand(ICommandSender ics, String[] args)
	{ command.processCommand(ics, args); }
	
	public int compareTo(Object o)
	{ return command.compareTo(o); }
	
	public List getCommandAliases()
	{ return command.getCommandAliases(); }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return true; }
	
	public List addTabCompletionOptions(ICommandSender ics, String[] args)
	{ return command.addTabCompletionOptions(ics, args); }
	
	public boolean isUsernameIndex(String[] args, int i)
	{ return command.isUsernameIndex(args, i); }
}