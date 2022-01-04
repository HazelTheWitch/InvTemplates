package com.hazeltrinity.invtemplates.config;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortingKey<T extends Comparable<T>> {
    private final List<T> keys;

    private boolean isInfinite = false;
    private boolean preferred = true;

    @SafeVarargs // Literally no clue what this does
    public SortingKey(T... keys) {
        this.keys = Arrays.asList(keys);
    }

    @SafeVarargs // Literally no clue what this does
    public SortingKey(boolean preferred, T... keys) {
        this.preferred = preferred;
        this.keys = Arrays.asList(keys);
    }

    /**
     * Construct an infinite sorting key
     */
    public SortingKey() {
        keys = Collections.emptyList();
        isInfinite = true;
    }

    public List<T> getKeys() {
        return keys;
    }

    public boolean getPreferred() {
        return preferred;
    }

    public int compareTo(@NotNull SortingKey<T> o) {
        int maxSize = Math.max(keys.size(), o.getKeys().size());

        if (isInfinite) {
            if (o.isInfinite) {
                return 0;
            }

            return 1;
        }

        if (o.isInfinite) {
            return -1;
        }

        List<T> myKeys = List.copyOf(keys);
        List<T> otherKeys = List.copyOf(o.getKeys());

        for (int i = 0; i < maxSize; i++) {
            if (myKeys.size() <= i) {
                return -1;
            }

            if (otherKeys.size() <= i) {
                return 1;
            }

            int comparedValue = myKeys.get(i).compareTo((T) otherKeys.get(i));
            if (comparedValue < 0) {
                return -1;
            } else if (comparedValue > 0) {
                return 1;
            }
        }

        return 0;
    }
}
