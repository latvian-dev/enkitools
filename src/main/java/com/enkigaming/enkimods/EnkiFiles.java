package com.enkigaming.enkimods;

import java.io.File;

import latmod.core.LatCoreMC;

public class EnkiFiles
{
	public static File config;
	public static File ranks;
	public static File players;
	
	public static void init()
	{
		config = new File(LatCoreMC.latmodFolder, "EnkiMods/Config.cfg");
		ranks = new File(LatCoreMC.latmodFolder, "EnkiMods/Ranks.txt");
		players = new File(LatCoreMC.latmodFolder, "EnkiMods/Players.txt");
	}
}