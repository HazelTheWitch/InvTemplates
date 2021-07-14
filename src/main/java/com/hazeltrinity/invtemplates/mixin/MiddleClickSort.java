package com.hazeltrinity.invtemplates.mixin;

import com.hazeltrinity.invtemplates.InvTemplates;
import com.hazeltrinity.invtemplates.InvTemplatesClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class MiddleClickSort {
	@Inject(at = @At("HEAD"), method = "mouseClicked")
	private void init(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (button == 2) { // Only do sorting on middle click
			if (InvTemplatesClient.TEMPLATE == null) {
				InvTemplates.LOGGER.error("No Template was Loaded so can not Sort");
			}

			PacketByteBuf passed = new PacketByteBuf(Unpooled.buffer());
			passed.writeString(InvTemplatesClient.TEMPLATE.toJSONString()); // TODO: update system to allow larger packets and not send it every time.

			ClientSidePacketRegistryImpl.INSTANCE.sendToServer(InvTemplates.SORT_PACKET_ID, passed);
		}
	}
}
