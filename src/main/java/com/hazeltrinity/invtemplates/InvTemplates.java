package com.hazeltrinity.invtemplates;

import com.hazeltrinity.invtemplates.inventory.Helper;
import com.hazeltrinity.invtemplates.inventory.SortedInventory;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvTemplates implements ModInitializer {

    public static final String MODID = "invtemplates";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Identifier SORT_PACKET_ID = new Identifier(MODID, "sort");
    public static final Identifier SORTED_PACKET_ID = new Identifier(MODID, "sorted");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing");
        LOGGER.info("Registering Packets");
        ServerSidePacketRegistryImpl.INSTANCE.register(SORT_PACKET_ID, (packetContext, attachedData) -> {
            boolean sortPlayer = attachedData.readBoolean();
            String json = attachedData.readString();
            PlayerEntity player = packetContext.getPlayer();
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
                            player.playerScreenHandler.syncState();
                        } else {
                            player.currentScreenHandler.syncState();
                        }

                        ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, SORTED_PACKET_ID, new PacketByteBuf(Unpooled.buffer()));
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn(packetContext.getPlayer().getName().getString() + " sent an illegal sorting packet.");
                    }
                }
            });
        });
    }
}
