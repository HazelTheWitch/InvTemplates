package com.hazeltrinity.invtemplates.config.impl.sorting;

import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.config.KeyItem;

public class SortingKeyItem extends KeyItem {
    @Expose(serialize = true)
    public Type type = Type.Sorting;
    @Expose(serialize = true)
    public SortingType[] sortingTypes;

    public SortingKeyItem(SortingType... sortingTypes) {
        this.sortingTypes = sortingTypes;
    }

    @Override
    public void verify() {
        if (sortingTypes.length == 0) {
            sortingTypes = new SortingType[]{ new SortingType("alphabetically") };
        }
    }
}
