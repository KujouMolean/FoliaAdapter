package com.molean.folia.adapter;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.molean.folia.adapter.scoreborad.FoliaScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FoliaAdapter extends JavaPlugin implements Listener {

    private static Plugin plugin;

    public static Plugin getPlugin() {
        return plugin;
    }

    private static FoliaScoreboardManager foliaScoreboardManager;
    private static AsyncDaemonTaskExecutor asyncDaemonTaskExecutor;

    public static FoliaScoreboardManager getFoliaScoreboardManager() {
        return foliaScoreboardManager;
    }

    public static AsyncDaemonTaskExecutor getAsyncDaemonTaskExecutor() {
        return asyncDaemonTaskExecutor;
    }

    @Override
    public void onEnable() {
        foliaScoreboardManager = new FoliaScoreboardManager();
        asyncDaemonTaskExecutor = new AsyncDaemonTaskExecutor();
        Bukkit.getPluginManager().registerEvents(foliaScoreboardManager, this);
        Bukkit.getPluginManager().registerEvents(asyncDaemonTaskExecutor, this);
    }


    public FoliaAdapter() {
        plugin = this;

    }
}
