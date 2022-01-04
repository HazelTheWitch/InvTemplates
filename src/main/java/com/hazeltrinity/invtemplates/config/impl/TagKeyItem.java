package com.hazeltrinity.invtemplates.config.impl;

import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TagKeyItem extends KeyItem {
    @Expose()
    public String tag;

    private Identifier identifier;
    private Tag<Item> tagObj;

    public TagKeyItem(String tag) {
        super("tag");
        this.tag = tag;
    }

    @Override
    public void verify() {
        if (tag.length() == 0) {
            tag = "minecraft:flowers";
        }

        int i = tag.indexOf(':');

        String namespace = tag.substring(0, i);
        String id = tag.substring(i + 1);

        identifier = new Identifier(namespace, id);
        try {
            tagObj = ServerTagManagerHolder.getTagManager().getTag(
                    Registry.ITEM_KEY,
                    identifier,
                    (identifierx) -> new IllegalArgumentException("Unknown item tag '" + identifierx + "'")
            );
        } catch (IllegalArgumentException e) {
            tagObj = null;
        }

    }

    @Override
    public SortingKey<Integer> infinity() {
        return new SortingKey<>();
    }

    @Override
    public SortingKey<Integer> valueOf(ItemStack stack) {
        if (tagObj == null) {
            return new SortingKey<>(false, 10);
        }
        boolean fitsTag = tagObj.contains(stack.getItem());
        return new SortingKey<>(fitsTag, fitsTag ? 0 : 1, -stack.getCount());
    }
}
