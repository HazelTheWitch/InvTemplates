package com.hazeltrinity.invtemplates.config.impl;

import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class MultiItemKeyItem extends KeyItem {
    @Expose()
    public String[] identifiers;

    public MultiItemKeyItem(String... identifiers) {
        super("multi-item");
        this.identifiers = identifiers;
    }

    @Override
    public void verify() {
        if (identifiers.length == 0) {
            identifiers = new String[]{"minecraft:poppy"};
        }
    }

    @Override
    public SortingKey<Integer> infinity() {
        return new SortingKey<>();
    }

    @Override
    public SortingKey<Integer> valueOf(ItemStack stack) {
        int index = identifiers.length;

        for (int i = 0; i < identifiers.length; i++) {
            if (Registry.ITEM.getId(stack.getItem()).toString().equals(identifiers[i])) {
                index = i;
                break;
            }
        }

        return new SortingKey<>(index != identifiers.length, index, -stack.getCount());
    }
}
