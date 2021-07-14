package com.hazeltrinity.invtemplates.config.impl;

import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.config.KeyItem;

public class EmptyKeyItem extends KeyItem {
    @Expose(serialize = true)
    public Type type = Type.Empty;

    @Override
    public void verify() { }
}
