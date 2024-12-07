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
				"[OmniLib] The future is now.",
				"[OmniLib] A new frontier begins.",
				"[OmniLib] Welcome to the next level.",
				"[OmniLib] Where possibilities take shape.",
				"[OmniLib] Let’s craft something amazing."
		};
		Random random = new Random();
		return messages[random.nextInt(messages.length)];
	}
}