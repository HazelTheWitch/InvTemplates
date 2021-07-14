package com.hazeltrinity.invtemplates;

import com.hazeltrinity.invtemplates.config.InvTemplate;
import net.fabricmc.api.ClientModInitializer;

import java.io.IOException;

public class InvTemplatesClient implements ClientModInitializer {
    public static InvTemplate TEMPLATE;

    @Override
    public void onInitializeClient() {
        InvTemplates.LOGGER.info("Initializing Client");
        InvTemplates.LOGGER.info("Reading Config");
        TEMPLATE = InvTemplate.loadFromJSON("InvTemplates.json");
        InvTemplates.LOGGER.info("Verifying Template");
        TEMPLATE.verify();
        InvTemplates.LOGGER.info("Writing Config Back for Formatting");
        try {
            TEMPLATE.writeToJSON("InvTemplates.json");
            InvTemplates.LOGGER.info("Config Written Successfully");
        } catch (IOException e) {
            InvTemplates.LOGGER.error("Could not write to config file");
        }

    }
}
