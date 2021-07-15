package com.hazeltrinity.invtemplates;

import com.hazeltrinity.invtemplates.config.InvTemplate;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvTemplates implements ModInitializer {

	public static final String MODID = "invtemplates";
	public static final Logger LOGGER = LogManager.getLogger();

	public static final Identifier SORT_PACKET_ID = new Identifier(MODID, "sort");

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing");
		ServerSidePacketRegistryImpl.INSTANCE.register(SORT_PACKET_ID, (packetContext, attachedData) -> {
			packetContext.getTaskQueue().execute(() -> {
				String json = attachedData.readString();

				try {
					InvTemplate template = InvTemplate.loadFromJSONString(json);
					template.verify();
					template.sort(packetContext.getPlayer());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
	}
}
