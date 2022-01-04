package com.hazeltrinity.invtemplates;

import com.hazeltrinity.invtemplates.config.InvTemplate;
import com.hazeltrinity.invtemplates.config.impl.sorting.AlphabeticallySortedKeyItem;
import com.hazeltrinity.invtemplates.inventory.Helper;
import com.hazeltrinity.invtemplates.inventory.SortableInventory;
import com.hazeltrinity.invtemplates.inventory.SortedInventory;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

import static com.hazeltrinity.invtemplates.InvTemplates.SORT_PACKET_ID;

@Environment(EnvType.CLIENT)
public class InvTemplatesClient implements ClientModInitializer {
    public static InvTemplate TEMPLATE;

    private static KeyBinding sortKeyBinding;
    private static KeyBinding reloadConfigBinding;

    private static final int RELOAD_COOLDOWN = 100;
    private static final int SORT_COOLDOWN = 40;

    private static int reloadTimeLeft = 0;
    private static int sortTimeLeft = 0;

    @Override
    public void onInitializeClient() {
        InvTemplates.LOGGER.info("Initializing Client");
        reloadConfig();

        ClientSidePacketRegistryImpl.INSTANCE.register(InvTemplates.SORTED_PACKET_ID,
                (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new TranslatableText("invtemplates.chat.sorted").formatted(Formatting.GRAY));
                }));

        sortKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.invtemplates.sort",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                "category.invtemplates.sorting"
        ));

        reloadConfigBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.invtemplates.reload",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.invtemplates.sorting"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean doSortInventory = false;
            boolean doReloadConfig = false;

            while (sortKeyBinding.wasPressed()) {
                doSortInventory = true;
            }

            while (reloadConfigBinding.wasPressed()) {
                doReloadConfig = true;
            }

            if (reloadTimeLeft > 0)
                reloadTimeLeft -= 1;

            if (sortTimeLeft > 0)
                sortTimeLeft -= 1;

            if (canReload() && doReloadConfig) {
                InvTemplates.LOGGER.info("Reloading Config");
                reloadConfig();
                client.inGameHud.getChatHud().addMessage(new TranslatableText("invtemplates.chat.reloaded").formatted(Formatting.GRAY));
            }

            if (canSort() && doSortInventory) {
                sortInventory(true);
            }
        });
    }

    private static void reloadConfig() {
        InvTemplates.LOGGER.info("Reading Config");
        TEMPLATE = InvTemplate.loadFromJSON("InvTemplates.json");
        InvTemplates.LOGGER.info("Verifying Template");
        TEMPLATE.verify();
        InvTemplates.LOGGER.info("Writing config back for formatting");
        try {
            TEMPLATE.writeToJSON("InvTemplates.json");
            InvTemplates.LOGGER.info("Config written successfully");
        } catch (IOException e) {
            InvTemplates.LOGGER.error("Could not write to config file");
        }

        reloadTimeLeft = RELOAD_COOLDOWN;
    }

    public static void sortInventory(boolean sortPlayer) {
        if (sortPlayer)
            InvTemplates.LOGGER.info("Sorting Inventory");
        else
            InvTemplates.LOGGER.info("Sorting Container");

        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null) {
            InvTemplates.LOGGER.error("You don't exist LOL");
            return;
        }

        SortableInventory sortable = null;
        Inventory inv = null;
        if (sortPlayer) {
            if (InvTemplatesClient.TEMPLATE == null) {
                InvTemplates.LOGGER.error("No template was loaded so can not sort");
                return;
            }

            inv = player.getInventory();

            sortable = InvTemplatesClient.TEMPLATE.sort();
        } else {
            inv = Helper.getScreenInventory(player);

            if (inv != null) {
                sortable = Helper.createDefaultSortable(inv, new AlphabeticallySortedKeyItem());
            }
        }

        if (sortable != null && inv != null) {
            SortedInventory sorted = sortable.apply(inv);

            PacketByteBuf passed = new PacketByteBuf(Unpooled.buffer());
            passed.writeBoolean(sortPlayer);
            passed.writeString(sorted.toJSONString());

            ClientSidePacketRegistryImpl.INSTANCE.sendToServer(SORT_PACKET_ID, passed);
            sortTimeLeft = SORT_COOLDOWN;
        } else {
            InvTemplates.LOGGER.warn("Could not initialize sorting template or inventory");
        }
    }

    public static boolean canSort() {
        return sortTimeLeft == 0;
    }

    public static boolean canReload() {
        return reloadTimeLeft == 0;
    }
}
