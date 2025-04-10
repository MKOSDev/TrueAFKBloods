package com.afkbloods;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class afkBloodsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(afkBloodsPlugin.class);
		RuneLite.main(args);
	}
}