package com.hazeltrinity.invtemplates.inventory;

import com.google.gson.Gson;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A final sorted inventory which contains information on where all the items end up.
 */
public class SortedInventory {
    public HashMap<Integer, Integer> destinations;

    public SortedInventory() {
        this(new HashMap<>());
    }

    public SortedInventory(int... destinations) {
        this();

        for (int i = 0; i < destinations.length; i++) {
            set(i, destinations[i]);
        }
    }

    public SortedInventory(HashMap<Integer, Integer> destinations) {
        this.destinations = destinations;
    }

    public void set(int source, int destination) throws IllegalArgumentException {
        if (destinations.containsKey(source)) {
            throw new IllegalArgumentException("Source " + source + " already has a destination.");
        }

        if (destinations.containsValue(destination)) {
            throw new IllegalArgumentException("Destination " + destination + " already has a source.");
        }

        destinations.put(source, destination);
    }

    public boolean contains(int slot) {
        return destinations.containsKey(slot) || destinations.containsValue(slot);
    }

    public boolean isValid(Inventory inventory) {
        return isValid(inventory.size());
    }

    public boolean isValid(int size) {
        int maxIndex = size - 1;

        for (int source : destinations.keySet()) {
            if (source > maxIndex || destinations.get(source) > maxIndex) {
                return false;
            }
        }

        return true;
    }

    public void validate(int size) throws IllegalArgumentException {
        SortedInventory newSorted = new SortedInventory();

        for (int source : destinations.keySet()) {
            newSorted.set(source, destinations.get(source));
        }

        for (int i = 0; i < size; i++) {
            if (!newSorted.contains(i)) {
                newSorted.set(i, i);
            }
        }

        this.destinations = newSorted.destinations;
    }

    public void simplify() {
        HashSet<Integer> toRemove = new HashSet<>();

        for (int source : destinations.keySet()) {
            if (destinations.get(source) == source) {
                toRemove.add(source);
            }
        }

        for (int remove : toRemove) {
            destinations.remove(remove);
        }
    }

    /**
     * This is only run on the logical server side.
     *
     * @param inventory the inventory to apply this sorted state to
     * @return true if the inventory was applied
     */
    public boolean apply(Inventory inventory) {
        if (!isValid(inventory)) {
            return false;
        }

        ArrayList<ItemStack> stacks = SortableInventory.clumpInventory(Helper.inventoryToArrayList(inventory, true));

        inventory.clear();

        for (int source : destinations.keySet()) {
            inventory.setStack(destinations.get(source), stacks.get(source));
        }

        inventory.markDirty();

        return true;
    }

    public String toJSONString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static SortedInventory fromJSONString(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SortedInventory.class);
    }
}
