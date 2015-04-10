package com.enkigaming.enkimods;

import java.io.File;

import latmod.core.LatCoreMC;

public class EnkiData
{
	public static final String TAG_HOMES = "";
	public static final String TAG_LAST_POS = "LastSavedPos";
	public static final String TAG_LAST_DEATH = "LastDeath";
	public static final String TAG_MAIL = "Mail";
	
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