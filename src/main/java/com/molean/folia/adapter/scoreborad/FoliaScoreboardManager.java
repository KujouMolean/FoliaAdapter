package com.molean.folia.adapter.scoreborad;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FoliaScoreboardManager implements Listener, ScoreboardManager {
    @Deprecated
    public FoliaScoreboardManager(JavaPlugin plugin) {
    }

    public FoliaScoreboardManager() {

    }

    private static FoliaScoreboard mainScoreboard;

    private static final Map<UUID, FoliaScoreboard> scoreboards = new HashMap<>();

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

    @Override
    public @NotNull Scoreboard getNewScoreboard() {
        return new FoliaScoreboard(this);
    }


    public @Nullable FoliaScoreboard getPlayerScoreboard(Player player) {
        return scoreboards.get(player.getUniqueId());
    }

    public void setPlayerScoreboard(Player player, FoliaScoreboard foliaScoreboard) {
        scoreboards.put(player.getUniqueId(), foliaScoreboard);
        join(player, foliaScoreboard);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent event) {
        join(event.getPlayer(), mainScoreboard);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerQuitEvent event) {
        quit(event.getPlayer());
    }

    public void quit(Player player) {
        FoliaScoreboard playerScoreboard = getPlayerScoreboard(player);
        if (playerScoreboard != null) {
            playerScoreboard.viewers.remove(player.getUniqueId());
            playerScoreboard.clearFor(player);
        }
    }

    public void join(Player player, FoliaScoreboard foliaScoreboard) {
        foliaScoreboard.viewers.add(player.getUniqueId());
        foliaScoreboard.fullSend(player);
    }
}
