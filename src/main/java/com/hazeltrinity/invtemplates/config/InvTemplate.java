package com.hazeltrinity.invtemplates.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.InvTemplates;
import com.hazeltrinity.invtemplates.config.impl.*;
import com.hazeltrinity.invtemplates.config.impl.sorting.AlphabeticallySortedKeyItem;
import com.hazeltrinity.invtemplates.inventory.KeySlot;
import com.hazeltrinity.invtemplates.inventory.SortableInventory;
import com.hazeltrinity.invtemplates.inventory.SortedInventory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class InvTemplate {
    @Expose(serialize = false, deserialize = false)
    private static final RuntimeTypeAdapterFactory<KeyItem> RUNTIME_TYPE_ADAPTER_FACTORY = RuntimeTypeAdapterFactory
            .of(KeyItem.class, "type")
            .registerSubtype(AlphabeticallySortedKeyItem.class, "alphabetically-sorting")
            .registerSubtype(EmptyKeyItem.class, "empty")
            .registerSubtype(ItemKeyItem.class, "item")
            .registerSubtype(MultiItemKeyItem.class, "multi-item")
            .registerSubtype(FoodKeyItem.class, "food")
            .registerSubtype(TagKeyItem.class, "tag");

    @Expose(serialize = false, deserialize = false)
    private static final char DEFAULT_CHAR = '$';

    @Expose(serialize = false, deserialize = false)
    private static final String DEFAULT_ROW = "$$$$$$$$$";

    @Expose(serialize = true)
    public String[] templateInventory = {
            "$$$$$$$$$",
            "$$$$$$$$$",
            "$$$$a$$$$",
            "SPAsBH bFt"
    };

    @Expose(serialize = true)
    public HashMap<Character, KeyItem> key = new HashMap<Character, KeyItem>() {{
        put('S', new MultiItemKeyItem("minecraft:netherite_sword", "minecraft:diamond_sword", "minecraft:iron_sword", "minecraft:golden_sword", "minecraft:stone_sword", "minecraft:wooden_sword"));
        put('P', new MultiItemKeyItem("minecraft:netherite_pickaxe", "minecraft:diamond_pickaxe", "minecraft:iron_pickaxe", "minecraft:golden_pickaxe", "minecraft:stone_pickaxe", "minecraft:wooden_pickaxe"));
        put('A', new MultiItemKeyItem("minecraft:netherite_axe", "minecraft:diamond_axe", "minecraft:iron_axe", "minecraft:golden_axe", "minecraft:stone_axe", "minecraft:wooden_axe"));
        put('s', new MultiItemKeyItem("minecraft:netherite_shovel", "minecraft:diamond_shovel", "minecraft:iron_shovel", "minecraft:golden_shovel", "minecraft:stone_shovel", "minecraft:wooden_shovel"));
        put('B', new ItemKeyItem("minecraft:bow"));
        put('H', new MultiItemKeyItem("minecraft:netherite_hoe", "minecraft:diamond_hoe", "minecraft:iron_hoe", "minecraft:golden_hoe", "minecraft:stone_hoe", "minecraft:wooden_hoe"));
        put('b', new MultiItemKeyItem("minecraft:cobblestone", "minecraft:netherrack", "minecraft:stone"));
        put('F', new FoodKeyItem());
        put('t', new ItemKeyItem("minecraft:torch"));
        put('a', new TagKeyItem("minecraft:arrows"));
        put('$', new AlphabeticallySortedKeyItem());
    }};

    @Expose(serialize = true)
    public String priorities = " SPAsBHFabt$";

    public SortableInventory sort() {
        HashMap<Integer, KeySlot> slots = new HashMap<>();

        for (int row = 3; row >= 0; row --) {
            int length = row == 3 ? 10 : 9;

            for (int column = 0; column < length; column++) {
                int slot = slotNumber(row, column);

                char charKey = templateInventory[row].charAt(column);

                KeySlot keySlot = new KeySlot(key.get(charKey), priorities.indexOf(charKey));

                slots.put(slot, keySlot);
            }
        }

        return new SortableInventory(slots);
    }

    private int slotNumber(int row, int column) {
        if (row == 3) {
            if (column == 9) {
                return PlayerInventory.OFF_HAND_SLOT;
            }
            return column;
        }

        return 9 * row + column + 9;
    }

    public void verify() {
        if (templateInventory.length < 4) {
            String[] newInventory = new String[4];
            for (int i = 0; i < 4 - templateInventory.length; i ++) {
                newInventory[i] = DEFAULT_ROW;
            }

            for (int i = 4 - templateInventory.length; i < 4; i ++) {
                newInventory[i] = templateInventory[i - 4 + templateInventory.length];
            }

            templateInventory = newInventory;
        } else if (templateInventory.length > 4) {
            String[] newInventory = new String[4];

            System.arraycopy(templateInventory, 0, newInventory, 0, 4);

            templateInventory = newInventory;
        }

        HashSet<Character> charsUsed = new HashSet<>();

        for (int i = 0; i < 4; i ++) {
            String bar = templateInventory[i];

            int length = i == 3 ? 10 : 9;

            while (bar.length() < length) {
                bar += DEFAULT_CHAR;
            }

            if (bar.length() > length) {
                bar = bar.substring(0, 9);
            }

            templateInventory[i] = bar;
        }

        for (String bar : templateInventory) {
            for (int i = 0; i < bar.length(); i ++) {
                charsUsed.add(bar.charAt(i));
            }
        }

        HashSet<Character> notPrioritized = new HashSet<>(charsUsed);

        for (int i = 0; i < priorities.length(); i ++) {
            notPrioritized.remove(priorities.charAt(i));
        }

        if (notPrioritized.size() > 0) {
            StringBuilder sb = new StringBuilder();

            for (Character ch : notPrioritized) {
                sb.append(ch);
            }

            priorities += sb.toString();
        }

        for (char defined : key.keySet()) {
            charsUsed.remove(defined);
        }

        for (char undefined : charsUsed) {
            key.put(undefined, new EmptyKeyItem());
        }

        for (KeyItem item : key.values()) {
            item.verify();
        }
    }

    /**
     * Writes this config file to JSON.
     * @param fileName the file name to write to
     * @throws IOException if it can not create the new file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void writeToJSON(String fileName) throws IOException {
        File configFile = FabricLoader.getInstance().getConfigDir().resolve(fileName).toFile();
        configFile.createNewFile();
        FileOutputStream oConfigFile = new FileOutputStream(configFile, false);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapterFactory(RUNTIME_TYPE_ADAPTER_FACTORY).setPrettyPrinting().create();

        String json = gson.toJson(this);

        try (PrintWriter out = new PrintWriter(oConfigFile)) {
            out.println(json);
        }
    }

    public String toJSONString() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapterFactory(RUNTIME_TYPE_ADAPTER_FACTORY).create();

        return gson.toJson(this);
    }

    /**
     * Reads a template from a config file in JSON or returns the default if there is an error.
     * @param fileName the file name to read from
     */
    public static InvTemplate loadFromJSON(String fileName) {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve(fileName);
        String json;
        try {
            if (configFile.toFile().createNewFile()) {
                InvTemplates.LOGGER.warn("Falling Back on Default Template [1]");
                return new InvTemplate();
            }

            json = Files.readString(configFile).trim();
        } catch (IOException | NoSuchElementException e) {
            InvTemplates.LOGGER.warn("Falling Back on Default Template [2]");
            return new InvTemplate();
        }

        try {
            InvTemplate template = loadFromJSONString(json);
            if (template == null) {
                InvTemplates.LOGGER.warn("Falling Back on Default Template [4]");
                return new InvTemplate();
            }
            return template;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            InvTemplates.LOGGER.warn("Falling Back on Default Template [5]");
            return new InvTemplate();
        }
    }

    public static InvTemplate loadFromJSONString(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapterFactory(RUNTIME_TYPE_ADAPTER_FACTORY).setPrettyPrinting().create();
        return gson.fromJson(json, InvTemplate.class);
    }
}
