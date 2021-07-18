package com.hazeltrinity.invtemplates.inventory;

import com.google.gson.Gson;

public class SortPacketData {
    public SortedInventory inv;
    public boolean player;

    public SortPacketData(SortedInventory inv, boolean player) {
        this.inv = inv;
        this.player = player;
    }

    public String toJSONString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static SortPacketData fromJSONString(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SortPacketData.class);
    }
}
