package com.enkigaming.enkimods.cmd;

import latmod.core.cmd.CommandLevel;
import net.minecraft.command.CommandGameRule;

public class CmdGameruleOverride extends CommandGameRule
{
	public final int getRequiredPermissionLevel()
	{ return CommandLevel.ALL.requiredPermsLevel(); }
}