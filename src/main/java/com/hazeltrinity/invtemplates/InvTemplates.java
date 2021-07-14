package com.hazeltrinity.invtemplates;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvTemplates implements ModInitializer {

	public static final String MODID = "invtemplates";
	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing");
	}
}
