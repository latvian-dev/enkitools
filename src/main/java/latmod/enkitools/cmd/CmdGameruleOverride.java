package latmod.enkitools.cmd;

import latmod.ftbu.core.cmd.CommandLevel;
import net.minecraft.command.CommandGameRule;

public class CmdGameruleOverride extends CommandGameRule
{
	public final int getRequiredPermissionLevel()
	{ return CommandLevel.ALL.requiredPermsLevel(); }
}