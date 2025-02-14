package com.molean.folia.adapter.scoreborad;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FoliaScoreboardManager implements Listener {
    @Deprecated
    public FoliaScoreboardManager(JavaPlugin plugin) {
    }

    public FoliaScoreboardManager() {

    }

    private static FoliaScoreboard mainScoreboard;

    private static final Map<String, FoliaScoreboard> scoreboardMap = new HashMap<>();

    @Deprecated
    public FoliaScoreboard getScoreboard() {
        if (mainScoreboard == null) {
            mainScoreboard = new FoliaScoreboard(this);
        }
        return mainScoreboard;
    }

    public @NotNull FoliaScoreboard getMainScoreboard() {
        if (mainScoreboard == null) {
            mainScoreboard = new FoliaScoreboard(this);
        }
        return mainScoreboard;
    }

    public @NotNull FoliaScoreboard getOrCreateScoreboard(String name) {
        return scoreboardMap.computeIfAbsent(name, s -> new FoliaScoreboard(this));
    }

    public @Nullable FoliaScoreboard getPlayerScoreboard(Player player) {
        if (mainScoreboard.viewers.contains(player.getUniqueId())) {
            return mainScoreboard;
        }
        for (FoliaScoreboard value : scoreboardMap.values()) {
            if (value.viewers.contains(player.getUniqueId())) {
                return value;
            }
        }
        return null;
    }

    public void setPlayerScoreboard(Player player, FoliaScoreboard foliaScoreboard) {
        quit(player);
        join(player, foliaScoreboard);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent event) {
        join(event.getPlayer(), mainScoreboard);
    }

    public void quit(Player player) {
        boolean b = mainScoreboard.viewers.remove(player.getUniqueId());
        if (b) {
            mainScoreboard.clearFor(player);
        }

        for (FoliaScoreboard value : scoreboardMap.values()) {
            boolean remove = value.viewers.remove(player.getUniqueId());
            if (remove) {
                value.clearFor(player);
            }
        }
    }

    public void join(Player player, FoliaScoreboard foliaScoreboard) {
        foliaScoreboard.viewers.add(player.getUniqueId());
        foliaScoreboard.fullSend(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerQuitEvent event) {
        quit(event.getPlayer());
    }
}
