package com.hazeltrinity.invtemplates.config.impl.sorting;

import com.google.gson.annotations.Expose;

public class SortingType {
    @Expose(serialize = true)
    public String sortingType;
    @Expose(serialize = true)
    public boolean reversed;

    public SortingType(String sortingType) {
        this(sortingType, false);
    }

    public SortingType(String sortingType, boolean reversed) {
        this.sortingType = sortingType;
        this.reversed = reversed;
    }
}
