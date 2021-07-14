package com.hazeltrinity.invtemplates.config.impl;

import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.config.KeyItem;

public class ItemKeyItem extends KeyItem {
    @Expose(serialize = true)
    public Type type = Type.Item;

    @Expose(serialize = true)
    public String identifier;

    public ItemKeyItem(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void verify() {
        if (identifier.length() == 0) {
            identifier = "minecraft:poppy";
        }
    }
}
