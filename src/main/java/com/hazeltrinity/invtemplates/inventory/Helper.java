package com.hazeltrinity.invtemplates.inventory;

import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.impl.sorting.AlphabeticallySortedKeyItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class Helper {
    public static ArrayList<ItemStack> inventoryToArrayList(Inventory inventory, boolean copy) {
        ArrayList<ItemStack> items = new ArrayList<>(inventory.size());

        for (int i = 0; i < inventory.size(); i ++) {
            if (copy) {
                items.add(inventory.getStack(i).copy());
            } else {
                items.add(inventory.getStack(i));
            }
        }

        return items;
    }

    public static @Nullable Inventory getScreenInventory(PlayerEntity player) {
        if (player.currentScreenHandler.slots.size() > 0) {
            return player.currentScreenHandler.slots.get(0).inventory;
        }
        return null;
    }

    public static SortableInventory createDefaultSortable(Inventory inv, KeyItem keyItem) {
        HashMap<Integer, KeySlot> slots = new HashMap<>();

        for (int i = 0; i < inv.size(); i ++) {
            slots.put(i, new KeySlot(keyItem, 0));
        }

        return new SortableInventory(slots);
    }
}
