package com.hazeltrinity.invtemplates.config;

import com.google.gson.annotations.Expose;
import net.minecraft.item.ItemStack;

public class KeyItem {
    @Expose(serialize = false)
    public String type;

    public KeyItem(String type) {
        this.type = type;
    }

    public void verify() {

    }

    /**
     * Get a sorting key representing infinity.
     * @return an infinite SortingKey
     */
    public SortingKey<?> infinity() {
        return new SortingKey<Double>();
    }

    /**
     * Get the value of an ItemStack
     * @param stack the stack
     * @return the value of the stack
     */
    public SortingKey<?> valueOf(ItemStack stack) {
        return new SortingKey<Double>(0d);
    }
}
