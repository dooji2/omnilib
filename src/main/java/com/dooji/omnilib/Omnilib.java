package com.dooji.omnilib;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Omnilib implements ModInitializer {
	public static final String MOD_ID = "omnilib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Omnilib] " + getWelcomeMessage());
	}

	private String getWelcomeMessage() {
		String[] messages = {
				"The future is now.",
				"A new frontier begins.",
				"Welcome to the next level.",
				"Where possibilities take shape.",
				"Let’s craft something amazing."
		};
		Random random = new Random();
		return messages[random.nextInt(messages.length)];
	}
}