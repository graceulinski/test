package com.warmthalert;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("warmthalert")
public interface WarmthAlertConfig extends Config
{
	@ConfigItem(
			keyName = "getWarmthThreshold",
			name = "Warmth Threshold",
			description = "Set warmth threshold",
			position = 1
	)
	default int getWarmthThreshold() {
		return 50;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayColor",
			name = "Overlay Color",
			description = "Set the notification overlay color",
			position = 2
	)
	default Color getWarmthOverlayColor() {
		return new Color(1.0f, 0.0f, 0.0f, 0.25f);
	}

	@ConfigItem(
			keyName = "disableOverlay",
			name = "Disable Overlay",
			description = "Disable overlay notifications",
			position = 3
	)
	default boolean disableWarmthOverlay() {
		return false;
	}

	@ConfigItem(
			keyName = "disableNotification",
			name = "Disable Notification",
			description = "Disable tray notifications",
			position = 4
	)
	default boolean disableWarmthNotifications() { return true; }

	@ConfigItem(
			keyName = "warmthNotifyTime",
			name = "Warmth Notify Time",
			description = "Seconds between Warmth notifications",
			position = 5
	)
	default int getWarmthNotifyTime() {
		return 3;
	}
}

