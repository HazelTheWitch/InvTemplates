package com.hazeltrinity.invtemplates;

import com.hazeltrinity.invtemplates.inventory.Helper;
import com.hazeltrinity.invtemplates.inventory.SortPacketData;
import com.hazeltrinity.invtemplates.inventory.SortableInventory;
import com.hazeltrinity.invtemplates.inventory.SortedInventory;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.inventory.Inventory;
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
		LOGGER.info("Registering Packets");
		ServerSidePacketRegistryImpl.INSTANCE.register(SORT_PACKET_ID, (packetContext, attachedData) -> {
			boolean sortPlayer = attachedData.readBoolean();
			String json = attachedData.readString();
			packetContext.getTaskQueue().execute(() -> {
				SortedInventory sorted = SortedInventory.fromJSONString(json);

				Inventory inv = null;
				if (sortPlayer) {
					inv = packetContext.getPlayer().getInventory();
				} else {
					inv = Helper.getScreenInventory(packetContext.getPlayer());
				}

				if (inv != null) {
					try {
						sorted.validate(inv.size());

						if (!sorted.apply(inv)) {
							LOGGER.info("Could not apply sorting mask " + json);
						}

						if (sortPlayer) {
							packetContext.getPlayer().playerScreenHandler.syncState();
						} else {
							packetContext.getPlayer().currentScreenHandler.syncState();
						}
					} catch (IllegalArgumentException e) {
						LOGGER.warn(packetContext.getPlayer().getName().getString() + " sent an illegal sorting packet.");
					}
				}
			});
		});
	}
}
