package com.hazeltrinity.invtemplates.inventory;

import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.*;

/**
 * An inventory which has slots with KeyItems used to sort inventories.
 */
public class SortableInventory {
    private final HashMap<Integer, KeySlot> inventorySlots;
    private final int[] priorities;

    public SortableInventory(HashMap<Integer, KeySlot> inventorySlots) {
        this.inventorySlots = inventorySlots;

        HashSet<Integer> priorities = new HashSet<>();

        for (KeySlot slot : inventorySlots.values()) {
            priorities.add(slot.getPriority());
        }

        List<Integer> prioritiesList = new ArrayList<Integer>(priorities);
        Collections.sort(prioritiesList);

        this.priorities = new int[prioritiesList.size()];
        for (int i = 0; i < prioritiesList.size(); i ++) {
            this.priorities[i] = prioritiesList.get(i);
        }
    }

    public boolean isValid(Inventory inventory) {
        return isValid(inventory.size());
    }

    public boolean isValid(int size) {
        int maxIndex = size - 1;

        for (int slot : inventorySlots.keySet()) {
            if (slot > maxIndex) {
                return false;
            }
        }

        return true;
    }

    public SortedInventory apply(Inventory inventory) {
        if (!isValid(inventory)) {
            return null;
        }

        SortedInventory sorted = new SortedInventory();

        ArrayList<ItemStack> oldItems = clumpInventory(Helper.inventoryToArrayList(inventory, true));

        HashSet<Integer> completed = new HashSet<>();

        if (fillSlots(sorted, inventory.size(), oldItems, true, completed))
            fillSlots(sorted, inventory.size(), oldItems, false, completed);

        sorted.simplify();

        return sorted;
    }

    private boolean fillSlots(SortedInventory sorted, int size, ArrayList<ItemStack> items, boolean firstRun, HashSet<Integer> completed) {
        HashMap<ItemStack, Integer> slots = new HashMap<>();

        for (int i = 0; i < items.size(); i ++) {
            ItemStack stack = items.get(i);

            if (stack != ItemStack.EMPTY) {
                slots.put(items.get(i), i);
            }
        }

        int slot, prioIndex, targetSlot;
        for (int i = 0; i < priorities.length; i ++) {
            prioIndex = i;
            if (!firstRun) {
                prioIndex = priorities.length - 1 - prioIndex;
            }

            for (int j = 0; j < size; j ++) {
                slot = j;
                if (!firstRun) {
                    slot = size - 1 - slot;
                }

                if (completed.contains(slot)) {
                    continue;
                }

                targetSlot = fillSlot(slots, firstRun, slot, priorities[prioIndex]);

                if (targetSlot >= 0 && items.get(targetSlot) != ItemStack.EMPTY) {
                    sorted.set(targetSlot, slot);

                    if (slots.isEmpty()) {
                        return false;
                    }

                    completed.add(slot);
                }
            }
        }

        return true;
    }

    /**
     * Fill a given slot with an item from stacks.
     * @param stacks the items remaining to place
     * @param requirePreferred true if this slot needs a preferred item
     * @param slot the slot index to fill
     * @return the index of the slot to pull from or -1 if it could not find an item
     */
    private int fillSlot(HashMap<ItemStack, Integer> stacks, boolean requirePreferred, int slot, int priority) {
        // We do not have a slot configured here so just leave the item
        if (!inventorySlots.containsKey(slot)) {
            ItemStack stack = itemStackFromSlots(stacks, slot);
            if (stack != null)
                stacks.remove(stack, slot);
            return slot;
        }

        KeySlot keySlot = inventorySlots.get(slot);

        if (keySlot.ignore()) {
            ItemStack stack = itemStackFromSlots(stacks, slot);
            if (stack != null)
                stacks.remove(stack, slot);
            return slot;
        }

        if (keySlot.getPriority() != priority) {
            return -1;
        }

        // Get the best item from the slot
        ItemStack bestItem = null;
        SortingKey bestValue = keySlot.getKey().infinity();

        for (ItemStack stack : stacks.keySet()) {
            SortingKey value = keySlot.getKey().valueOf(stack);

            if (requirePreferred && !value.getPreferred()) {
                continue;
            }

            if (value.compareTo(bestValue) <= 0) {
                bestItem = stack;
                bestValue = value;
            }
        }

        if (bestItem != null) {
            int s = stacks.get(bestItem);
            stacks.remove(bestItem, s);
            return s;
        }

        return -1;
    }

    private ItemStack itemStackFromSlots(HashMap<ItemStack, Integer> stacks, int slot) {
        for (ItemStack stack : stacks.keySet()) {
            if (stacks.get(stack) == slot) {
                return stack;
            }
        }

        return null;
    }

    public static void clumpInventoryAndApply(Inventory inventory, boolean markDirty) {
        clumpInventoryAndApply(inventory);

        if (markDirty)
            inventory.markDirty();
    }

    public static void clumpInventoryAndApply(Inventory inventory) {
        ArrayList<ItemStack> items = clumpInventory(Helper.inventoryToArrayList(inventory, false));

        inventory.clear();

        for (int i = 0; i < items.size(); i ++) {
            inventory.setStack(i, items.get(i));
        }
    }

    protected static ArrayList<ItemStack> clumpInventory(ArrayList<ItemStack> items) {
        for (int i = 0; i < items.size(); i ++) {
            ItemStack itemA = items.get(i);

            for (int j =  i + 1; j < items.size(); j ++) {
                ItemStack itemB = items.get(j);

                if (ItemStack.canCombine(itemA, itemB)) {
                    int M = itemA.getMaxCount();

                    int x = itemA.getCount();
                    int y = itemB.getCount();

                    itemA.setCount(Math.min(x + y, M));
                    itemB.setCount(x + y - Math.min(x + y, M));
                }
            }
        }

        for (int i = 0; i < items.size(); i ++) {
            if (items.get(i).getCount() == 0) {
                items.set(i, ItemStack.EMPTY);
            }
        }

        return items;
    }
}
