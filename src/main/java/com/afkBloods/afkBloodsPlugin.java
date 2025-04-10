package com.afkBloods;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.Notifier;

import java.time.Duration;
import java.time.Instant;




@Slf4j
@PluginDescriptor(
	name = "afkBloods"
)
public class afkBloodsPlugin extends Plugin
{
	private static final int FIRST_OBSTACLE_X = 1761;
	private static final int FIRST_OBSTACLE_Y = 3874;
	private static final int SECOND_OBSTACLE_X = 1752;
	private static final int SECOND_OBSTACLE_Y = 3854;

	public static final int IDLE = -1;

	private WorldPoint lastPosition;
	private boolean notifyPosition = false;
	private Instant lastMoving;
	private int lastAnimation = IDLE;

	@Inject
	private Client client;

	@Inject
	private afkBloodsConfig config;

	@Inject
	private Notifier notifier;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		final Player local = client.getLocalPlayer();
		final Duration waitDuration = Duration.ofMillis(600);

		if (checkFirstObstacle(waitDuration, local))
		{
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "WOULD SEND 1ST OBST NOTIFICATION NOW.", null);
			notifier.notify(Notification.ON, "You have stopped moving!");
		}

		if (checkSecondObstacle(waitDuration, local))
		{
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "WOULD SEND 2ND OBST NOTIFICATION NOW.", null);
			notifier.notify(Notification.ON, "You have stopped moving!");
		}
	}

	private boolean checkFirstObstacle(Duration waitDuration, Player local)
	{
		if (lastPosition == null)
		{
			lastPosition = local.getWorldLocation();
			return false;
		}




		WorldPoint position = local.getWorldLocation(); //current position
		final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

		if(inventory.count(ItemID.DENSE_ESSENCE_BLOCK) >= 1)
		{

			if (lastPosition.equals(position) && (position.getX() == FIRST_OBSTACLE_X) && (position.getY() == FIRST_OBSTACLE_Y) )
			{	//if last position equals current position
				if (notifyPosition
						&& local.getAnimation() == IDLE
						&& Instant.now().compareTo(lastMoving.plus(waitDuration)) >= 0)
				{
					//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "WOULD SEND NOTIFICATION NOW.", null);

					notifyPosition = false;
					// Return true only if we weren't just breaking out of an animation
					return lastAnimation == IDLE;
				}
			}
			else
			{
				notifyPosition = true;
				lastPosition = position;
				lastMoving = Instant.now();
			}
			return false;
		}
		else
		{
			return false;
		}
	}

	private boolean checkSecondObstacle(Duration waitDuration, Player local)
	{
		if (lastPosition == null)
		{
			lastPosition = local.getWorldLocation();
			return false;
		}

		WorldPoint position = local.getWorldLocation(); //current position
		final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

		if(inventory.count(ItemID.DENSE_ESSENCE_BLOCK) == 0)
		{
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "getX: " + position.getX() + " getY: " + position.getY(), null);

			if (lastPosition.equals(position) && (position.getX() == SECOND_OBSTACLE_X) && (position.getY() == SECOND_OBSTACLE_Y) )
			{	//if last position equals current position
				if (notifyPosition
						&& local.getAnimation() == IDLE
						&& Instant.now().compareTo(lastMoving.plus(waitDuration)) >= 0)
				{

					notifyPosition = false;
					// Return true only if we weren't just breaking out of an animation
					return lastAnimation == IDLE;
				}
			}
			else
			{
				notifyPosition = true;
				lastPosition = position;
				lastMoving = Instant.now();
			}
			return false;
		}
		else
		{
			return false;
		}
	}


	@Provides
	afkBloodsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(afkBloodsConfig.class);
	}
}
