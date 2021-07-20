package com.hazeltrinity.invtemplates.inventory;

import com.hazeltrinity.invtemplates.config.KeyItem;

public class KeySlot {
    public KeyItem getKey() {
        return key;
    }

    public boolean ignore() {
        return ignore;
    }

    public int getPriority() {
        return priority;
    }

    private final KeyItem key;
    private final boolean ignore;
    private final int priority;

    public KeySlot() {
        this(null, true, -1);
    }

    public KeySlot(KeyItem key, int priority) {
        this(key, key == null, priority);
    }

    private KeySlot(KeyItem key, boolean ignore, int priority) {
        this.key = key;
        this.ignore = ignore;
        this.priority = priority;
    }
}
