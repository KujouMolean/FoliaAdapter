package com.molean.folia.adapter;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FoliaAdapter extends JavaPlugin {

    private static Plugin plugin;

    public static Plugin getPlugin() {
        return plugin;
    }

    public FoliaAdapter() {
        plugin = this;

    }
}
