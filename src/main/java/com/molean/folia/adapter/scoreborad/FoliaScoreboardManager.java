package com.molean.folia.adapter.scoreborad;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FoliaScoreboardManager implements Listener {

    private JavaPlugin plugin;

    public FoliaScoreboardManager(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private FoliaScoreboard foliaScoreboard;

    public FoliaScoreboard getScoreboard() {
        if (foliaScoreboard == null) {
            foliaScoreboard = new FoliaScoreboard();
        }
        return foliaScoreboard;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        foliaScoreboard.fullSend(event.getPlayer());
    }

}
