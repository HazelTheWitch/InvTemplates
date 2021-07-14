package com.hazeltrinity.invtemplates.config.impl;

import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class ItemKeyItem extends KeyItem {
    @Expose()
    public String identifier;

    public ItemKeyItem(String identifier) {
        super("item");
        this.identifier = identifier;
    }

    @Override
    public void verify() {
        if (identifier.length() == 0) {
            identifier = "minecraft:poppy";
        }
    }

    @Override
    public SortingKey<Integer> infinity() {
        return new SortingKey<>();
    }

    @Override
    public SortingKey<Integer> valueOf(ItemStack stack) {
        boolean isItem = Registry.ITEM.getId(stack.getItem()).toString().equals(identifier);
        return new SortingKey<>(isItem, isItem ? 0 : 1, -stack.getCount());
    }
}
