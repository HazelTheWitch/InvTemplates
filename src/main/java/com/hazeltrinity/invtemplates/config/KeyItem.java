package com.hazeltrinity.invtemplates.config;

import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.config.impl.EmptyKeyItem;
import com.hazeltrinity.invtemplates.config.impl.ItemKeyItem;
import com.hazeltrinity.invtemplates.config.impl.MultiItemKeyItem;
import com.hazeltrinity.invtemplates.config.impl.sorting.SortingKeyItem;
import com.hazeltrinity.invtemplates.config.impl.sorting.SortingType;

public class KeyItem {
    public enum Type {
        Sorting,
        Empty,
        Item,
        MultiItem
    }

    @Expose(serialize = false)
    public Type type;

    // SortingKeyItem
    @Expose(serialize = false)
    public SortingType[] sortingTypes;

    // ItemKeyItem
    @Expose(serialize = false)
    public String identifier;

    // MultiItemKeyItem
    @Expose(serialize = false)
    public String[] identifiers;

    public void verify() {

    }

    public static KeyItem convert(KeyItem input) {
        if (input.type == null) {
            return input;
        }

        return switch (input.type) {
            case Sorting -> new SortingKeyItem(input.sortingTypes);
            case Empty -> new EmptyKeyItem();
            case Item -> new ItemKeyItem(input.identifier);
            case MultiItem -> new MultiItemKeyItem(input.identifiers);
        };
    }
}
