package com.toamistaketracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ToaMistakeTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ToaMistakeTrackerPlugin.class);
		RuneLite.main(args);
	}
}