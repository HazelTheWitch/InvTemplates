package com.hazeltrinity.invtemplates.mixin;

import com.hazeltrinity.invtemplates.InvTemplatesClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class MiddleClickSort {
    @Shadow
    @Nullable
    protected abstract Slot getSlotAt(double x, double y);

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
