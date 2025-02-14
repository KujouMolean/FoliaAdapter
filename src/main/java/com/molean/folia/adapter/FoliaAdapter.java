package com.molean.folia.adapter;

import com.molean.folia.adapter.scoreborad.FoliaScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FoliaAdapter extends JavaPlugin {

    private static Plugin plugin;

    public static Plugin getPlugin() {
        return plugin;
    }

    private static FoliaScoreboardManager foliaScoreboardManager;

    public static FoliaScoreboardManager getFoliaScoreboardManager() {
        return foliaScoreboardManager;
    }

    @Override
    public void onEnable() {
        foliaScoreboardManager = new FoliaScoreboardManager();
        Bukkit.getPluginManager().registerEvents(foliaScoreboardManager, this);
    }

    public FoliaAdapter() {
        plugin = this;

    }
}
