package com.hazeltrinity.invtemplates.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.hazeltrinity.invtemplates.InvTemplates;
import com.hazeltrinity.invtemplates.config.impl.EmptyKeyItem;
import com.hazeltrinity.invtemplates.config.impl.MultiItemKeyItem;
import com.hazeltrinity.invtemplates.config.impl.sorting.SortingKeyItem;
import com.hazeltrinity.invtemplates.config.impl.sorting.SortingType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.*;

public class InvTemplate {
    @Expose(serialize = false, deserialize = false)
    private static final char DEFAULT_CHAR = '#';

    @Expose(serialize = false, deserialize = false)
    private static final String DEFAULT_ROW = "#########";

    @Expose(serialize = true)
    public String[] inventory = {
            "#########",
            "#########",
            "####A####",
            "SPAsBH bF"
    };

    @Expose(serialize = true)
    public HashMap<Character, KeyItem> key = new HashMap<Character, KeyItem>() {{
        put('S', new MultiItemKeyItem("minecraft:netherite_sword", "minecraft:diamond_sword", "minecraft:iron_sword", "minecraft:golden_sword", "minecraft:stone_sword", "minecraft:wooden_sword"));
        put('#', new SortingKeyItem(new SortingType("alphabetically"), new SortingType("numerically", true)));
    }};

    public void verify() {
        if (inventory.length < 4) {
            String[] newInventory = new String[4];
            for (int i = 0; i < 4 - inventory.length; i ++) {
                newInventory[i] = DEFAULT_ROW;
            }

            for (int i = 4 - inventory.length; i < 4; i ++) {
                newInventory[i] = inventory[i - 4 + inventory.length];
            }

            inventory = newInventory;
        } else if (inventory.length > 4) {
            String[] newInventory = new String[4];

            System.arraycopy(inventory, 0, newInventory, 0, 4);

            inventory = newInventory;
        }

        HashSet<Character> charsUsed = new HashSet<>();

        for (int i = 0; i < 4; i ++) {
            String bar = inventory[i];

            while (bar.length() < 9) {
                bar += DEFAULT_CHAR;
            }

            if (bar.length() > 9) {
                bar = bar.substring(0, 9);
            }

            inventory[i] = bar;
        }

        for (String bar : inventory) {
            for (int i = 0; i < 9; i ++) {
                charsUsed.add(bar.charAt(i));
            }
        }

        for (char defined : key.keySet()) {
            charsUsed.remove(defined);

            key.put(defined, KeyItem.convert(key.get(defined)));
        }

        for (char undefined : charsUsed) {
            key.put(undefined, new EmptyKeyItem());
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

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        String json = gson.toJson(this);

        try (PrintWriter out = new PrintWriter(oConfigFile)) {
            out.println(json);
        }
    }

    /**
     * Reads a template from a config file in JSON or returns the default if there is an error.
     * @param fileName the file name to read from
     * @throws IOException if it can not create the new file or if it can not read the given file
     */
    public static InvTemplate loadFromJSON(String fileName) {
        File configFile = FabricLoader.getInstance().getConfigDir().resolve(fileName).toFile();
        String json;
        try {
            if (configFile.createNewFile()) {
                System.out.println(1);
                return new InvTemplate();
            }

            json = new Scanner(configFile).useDelimiter("\\z").next();
        } catch (IOException | NoSuchElementException e) {
            System.out.println(2);
            return new InvTemplate();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            System.out.println(3);
            return gson.fromJson(json, InvTemplate.class);
        } catch (JsonSyntaxException e) {
            System.out.println(4);
            return new InvTemplate();
        }
    }
}
