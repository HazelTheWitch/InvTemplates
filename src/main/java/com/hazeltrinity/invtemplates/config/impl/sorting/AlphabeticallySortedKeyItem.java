package com.hazeltrinity.invtemplates.config.impl.sorting;

import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.item.ItemStack;

public class AlphabeticallySortedKeyItem extends KeyItem {
    public AlphabeticallySortedKeyItem() {
        super("alphabetically-sorting");
    }

    @Override
    public void verify() {
    }

    @Override
    public SortingKey<String> infinity() {
        return new SortingKey<>();
    }

    public SortingKey<String> valueOf(ItemStack stack) {
        return new SortingKey<>(stack.getName().asString());
    }
}
