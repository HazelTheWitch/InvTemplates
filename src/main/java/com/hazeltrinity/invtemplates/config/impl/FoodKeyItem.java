package com.hazeltrinity.invtemplates.config.impl;

import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FoodKeyItem extends KeyItem {
    public FoodKeyItem() {
        super("food");
    }

    @Override
    public SortingKey<Float> infinity() {
        return new SortingKey<>();
    }

    @Override
    public SortingKey<Float> valueOf(ItemStack stack) {
        Item item = stack.getItem();
        FoodComponent food = item.getFoodComponent();

        if (food == null) {
            return new SortingKey<>(false, 0f, 0f);
        }

        return new SortingKey<>(true, -(float)food.getHunger(), -food.getSaturationModifier());
    }
}
