package com.hazeltrinity.invtemplates.mixin;

import com.hazeltrinity.invtemplates.InvTemplates;
import com.hazeltrinity.invtemplates.InvTemplatesClient;
import com.hazeltrinity.invtemplates.inventory.SortPacketData;
import com.hazeltrinity.invtemplates.inventory.SortableInventory;
import com.hazeltrinity.invtemplates.inventory.SortedInventory;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.hazeltrinity.invtemplates.InvTemplates.SORT_PACKET_ID;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class MiddleClickSort {
	@Shadow @Nullable protected abstract Slot getSlotAt(double x, double y);

	@Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
	private void middleMouseClickCheck(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (button == 2) { // Only do sorting on middle click
			Slot hoveredSlot = getSlotAt(mouseX, mouseY);

			if (InvTemplatesClient.canSort()) {
				InvTemplatesClient.sortInventory(hoveredSlot == null || hoveredSlot.inventory instanceof PlayerInventory);

				cir.setReturnValue(true);
			}
		}
	}
}
