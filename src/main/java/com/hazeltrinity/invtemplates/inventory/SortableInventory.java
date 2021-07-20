package com.hazeltrinity.invtemplates.inventory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.hazeltrinity.invtemplates.config.KeyItem;
import com.hazeltrinity.invtemplates.config.SortingKey;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

/**
 * An inventory which has slots with KeyItems used to sort inventories.
 */
public class SortableInventory {
    private class ValidItems {
        public ArrayList<ItemStack> preferred;
        public ArrayList<ItemStack> allowed;

        public ValidItems(ArrayList<ItemStack> preferred, ArrayList<ItemStack> allowed) {
            this.preferred = preferred;
            this.allowed = allowed;
        }
    }

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

        HashMap<Integer, Integer> sourceToDestination = new HashMap<>();

        /*
          Works through a backtracking algorithm.

          First we clump items together, this step is also done server side so we can expect the same output.
         */

        ArrayList<ItemStack> itemsList = clumpInventory(Helper.inventoryToArrayList(inventory, true));

        // Create a mapping of item stacks to slots
        HashMap<ItemStack, Integer> stacks = new HashMap<>();

        for (int slot = 0; slot < itemsList.size(); slot ++) {
            if (itemsList.get(slot) != ItemStack.EMPTY)
                stacks.put(itemsList.get(slot), slot);
        }

        // Convert create a set of remaining items to place
        HashSet<ItemStack> remainingItems = new HashSet<>(stacks.keySet());

        // Create a list of valid items in each slot
        HashMap<Integer, ValidItems> validItems = new HashMap<>();

        for (int slot = 0; slot < inventory.size(); slot ++) {
            KeySlot keySlot = inventorySlots.get(slot);

            if (keySlot != null && !keySlot.ignore()) {
                ArrayList<ItemStack> valid = new ArrayList<>();

                for (ItemStack stack : remainingItems) {
                    if (stack != ItemStack.EMPTY && inventory.isValid(slot, stack))
                        valid.add(stack);
                }

                validItems.put(slot, sortedByKeySlot(keySlot, valid));
            } else {
                remainingItems.remove(inventory.getStack(slot));
            }
        }

        // Order slots based on priority and location
        ArrayDeque<Integer> slotOrder = new ArrayDeque<>(inventory.size());

        for (int priority : priorities) {
            for (int slot = 0; slot < inventory.size(); slot ++) {
                KeySlot keySlot = inventorySlots.get(slot);
                if (keySlot != null && !keySlot.ignore() && keySlot.getPriority() == priority) {
                    slotOrder.add(slot);
                }
            }
        }

        fillSlot(sourceToDestination, stacks, remainingItems, validItems, slotOrder);

        return new SortedInventory(sourceToDestination);
    }

    private boolean fillSlot(HashMap<Integer, Integer> sourceToDestination,
                             HashMap<ItemStack, Integer> stacks,
                             HashSet<ItemStack> remainingItems,
                             HashMap<Integer, ValidItems> validItems,
                             ArrayDeque<Integer> slotOrder) {
        if (remainingItems.size() == 0) {
            return true;
        }

        int slot = slotOrder.removeFirst();

        for (ItemStack stack : validItems.get(slot).preferred) {
            if (remainingItems.contains(stack)) {
                int itemSlot = stacks.get(stack);
                sourceToDestination.put(itemSlot, slot);
                remainingItems.remove(stack);

                if (fillSlot(sourceToDestination, stacks, remainingItems, validItems, slotOrder)) {
                    return true;
                }

                sourceToDestination.remove(itemSlot, slot);
                remainingItems.add(stack);
            }
        }

        if (fillSlot(sourceToDestination, stacks, remainingItems, validItems, slotOrder))
            return true;

        slotOrder.addFirst(slot);
        slot = slotOrder.removeLast();

        for (ItemStack stack : validItems.get(slot).allowed) {
            if (remainingItems.contains(stack)) {
                int itemSlot = stacks.get(stack);
                sourceToDestination.put(itemSlot, slot);
                remainingItems.remove(stack);

                if (fillSlot(sourceToDestination, stacks, remainingItems, validItems, slotOrder)) {
                    return true;
                }

                sourceToDestination.remove(itemSlot, slot);
                remainingItems.add(stack);
            }
        }

        return false;
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

    private ValidItems sortedByKeySlot(KeySlot slot, ArrayList<ItemStack> items) { // TODO: use bestter sorting algorithm
        ArrayList<ItemStack> preferred = new ArrayList<ItemStack>();
        ArrayList<ItemStack> allowed = new ArrayList<ItemStack>();

        HashMap<ItemStack, SortingKey> sortingKeys = new HashMap<>();

        KeyItem keyItem = slot.getKey();

        for (ItemStack stack : items) {
            sortingKeys.put(stack, keyItem.valueOf(stack));
        }

        while (!sortingKeys.isEmpty()) {
            SortingKey bestKey = keyItem.infinity();
            ItemStack bestItem = null;

            for (ItemStack stack : sortingKeys.keySet()) {
                SortingKey other = sortingKeys.get(stack);
                if (other.getPreferred() && other.compareTo(bestKey) <= 0) {
                    bestKey = other;
                    bestItem = stack;
                }
            }

            if (bestItem == null) {
                break;
            }

            preferred.add(bestItem);
            sortingKeys.remove(bestItem);
        }

        while (!sortingKeys.isEmpty()) {
            SortingKey bestKey = keyItem.infinity();
            ItemStack bestItem = null;

            for (ItemStack stack : sortingKeys.keySet()) {
                SortingKey other = sortingKeys.get(stack);
                if (other.compareTo(bestKey) <= 0) {
                    bestKey = other;
                    bestItem = stack;
                }
            }

            allowed.add(bestItem);
            sortingKeys.remove(bestItem);
        }

        return new ValidItems(preferred, allowed);
    }
}
