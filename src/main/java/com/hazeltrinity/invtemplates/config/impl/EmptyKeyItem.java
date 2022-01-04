package com.hazeltrinity.invtemplates.config.impl;

import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.item.ItemStack;

public class EmptyKeyItem extends KeyItem {
    public EmptyKeyItem() {
        super("empty");
    }

    @Override
    public void verify() {
    }

    @Override
    public SortingKey<?> valueOf(ItemStack stack) {
        return new SortingKey<>(false, 0d);
    }
}
