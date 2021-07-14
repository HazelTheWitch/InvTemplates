package com.hazeltrinity.invtemplates.config.impl;

import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.config.KeyItem;

public class MultiItemKeyItem extends KeyItem {
    @Expose(serialize = true)
    public Type type = Type.MultiItem;

    @Expose(serialize = true)
    public String[] identifiers;

    public MultiItemKeyItem(String... identifiers) {
        this.identifiers = identifiers;
    }

    @Override
    public void verify() {
        if (identifiers.length == 0) {
            identifiers = new String[]{ "minecraft:poppy" };
        }
    }
}
