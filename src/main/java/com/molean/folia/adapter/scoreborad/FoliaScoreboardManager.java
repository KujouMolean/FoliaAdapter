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

import java.util.HashSet;
import java.util.Set;

public class FoliaScoreboardManager implements Listener, ScoreboardManager {
    @Deprecated
    public FoliaScoreboardManager(JavaPlugin plugin) {
    }

    public FoliaScoreboardManager() {

    }

    private static FoliaScoreboard mainScoreboard;

    private static final Set<FoliaScoreboard> scoreboards = new HashSet<>();

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
        FoliaScoreboard foliaScoreboard = new FoliaScoreboard(this);
        scoreboards.add(foliaScoreboard);
        return foliaScoreboard;
    }


    public @Nullable FoliaScoreboard getPlayerScoreboard(Player player) {
        if (mainScoreboard.viewers.contains(player.getUniqueId())) {
            return mainScoreboard;
        }
        for (FoliaScoreboard value : scoreboards) {
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

        for (FoliaScoreboard value : scoreboards) {
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
