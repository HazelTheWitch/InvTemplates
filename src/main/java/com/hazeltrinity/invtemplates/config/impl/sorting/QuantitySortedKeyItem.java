package com.hazeltrinity.invtemplates.config.impl.sorting;

import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.item.ItemStack;

public class QuantitySortedKeyItem extends KeyItem {
    public QuantitySortedKeyItem(String type) {
        super("quantity-sorting");
    }

    @Override
    public void verify() {

    }

    @Override
    public SortingKey<Integer> infinity() { return new SortingKey<>(); }

    @Override
    public SortingKey<Integer> valueOf(ItemStack stack) {
        String name = stack.getName().getString();
        Integer[] key = new Integer[1 + name.length()];
        key[0] = -stack.getCount();

        char c;
        for (int i = 0; i < name.length(); i ++) {
            c = name.charAt(i);

            key[i + 1] = (int)c;
        }

        return new SortingKey<>(key);
    }
}
