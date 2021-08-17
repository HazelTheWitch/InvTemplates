package com.hazeltrinity.invtemplates.config.impl.sorting;

import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.InvTemplates;
import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AlphabeticallySortedKeyItem extends KeyItem {
    @Expose(serialize = false, deserialize = false)
    private static final String DIGIT_STRING = "9876543210";

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

    @Override
    public SortingKey<String> valueOf(ItemStack stack) {
        return new SortingKey<>(stack.getName().getString(), constructQuantityString(stack.getCount(), stack.getMaxCount()));
    }

    private String constructQuantityString(int quantity, int maxQuantity) {
        StringBuilder builder = new StringBuilder();

        int digit;

        while (maxQuantity > 0) {
            digit = quantity % 10;
            quantity /= 10;
            maxQuantity /= 10;

            builder.append(DIGIT_STRING.charAt(digit));
        }

        builder.reverse();

        return builder.toString();
    }
}
