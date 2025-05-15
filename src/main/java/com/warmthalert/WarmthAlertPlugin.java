package com.warmthalert;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;

import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.Notifier;

import java.awt.*;

@Slf4j
@PluginDescriptor(
		name = "Warmth Alert",
		description = "Warmth Alert for Wintertodt",
		tags = {"warmth, wintertodt"}
)

public class WarmthAlertPlugin extends Plugin {
	private boolean shouldNotifyWarmth = true;
	private long lastWarmthNotificationTime = 0L;
	private int currentWarmth;

	@Inject
	private Client client;

	@Inject
	private com.warmthalert.WarmthAlertConfig config;

	@Inject
	private com.warmthalert.WarmthAlertOverlay warmthOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Notifier notifier;

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged) {
		currentWarmth = client.getVarbitValue(Varbits.WINTERTODT_WARMTH)/10;
	}

	@Provides
	com.warmthalert.WarmthAlertConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(com.warmthalert.WarmthAlertConfig.class);
	}

	public boolean shouldRenderOverlay() {
		if (!isClientReady()) {
			return false;
		}

		/* Render for warmth */
		return !config.disableWarmthOverlay() && warmthTotalBelowThreshold();
	}

	public Color getOverlayColor() {
		if (!config.disableWarmthOverlay() && warmthTotalBelowThreshold() && isInWintertodtRegion()) {
			return config.getWarmthOverlayColor();
		}

		/* Return transparent overlay if we somehow get here */
		return new Color(0.0f, 0.0f, 0.0f, 0.0f);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(warmthOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(warmthOverlay);
	}

	private boolean isInWintertodtRegion() {
		if (client.getLocalPlayer() != null) {
			return client.getLocalPlayer().getWorldLocation().getRegionID() == 6462;
		}

		return false;
	}

	public boolean warmthTotalBelowThreshold()  {
		return isClientReady() && (currentWarmth < config.getWarmthThreshold()) ;
	}

	public boolean isClientReady() {
		return ((client.getGameState() == GameState.LOGGED_IN) && (client.getLocalPlayer() != null));
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (!isClientReady()) {
			return;
		}
		if (isInWintertodtRegion()) {
			if (!config.disableWarmthNotifications() && warmthTotalBelowThreshold()) {
				int warmthNotifyTime = config.getWarmthNotifyTime();
				if (warmthNotifyTime == 0) {
					if (shouldNotifyWarmth) {
						notifier.notify("Your warmth is below " + config.getWarmthThreshold());
						shouldNotifyWarmth = false;
					}
				} else {
					long currentTime = System.currentTimeMillis();
					if (lastWarmthNotificationTime == 0L || currentTime - lastWarmthNotificationTime >= warmthNotifyTime * 1000L) {
						notifier.notify("Your warmth is below " + config.getWarmthThreshold());
						lastWarmthNotificationTime = currentTime;
					}
				}
			}

			if (!warmthTotalBelowThreshold()) {
				shouldNotifyWarmth = true;
			}
		}
	}
}
