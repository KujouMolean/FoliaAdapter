package com.molean.folia.adapter.scoreborad;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FoliaScoreboard implements Scoreboard {

     final Map<String, FoliaObjective> foliaObjectiveMap = new HashMap<>();
     final Map<DisplaySlot, FoliaObjective> displaySlotFoliaObjectiveMap = new HashMap<>();
     final Map<String, FoliaTeam> teamMap = new HashMap<>();

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull String criteria) {
        return registerNewObjective(name, criteria, Component.text(name));
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull String criteria, @Nullable Component displayName) {
        return registerNewObjective(name, criteria, displayName, RenderType.INTEGER);

    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull String criteria, @Nullable Component displayName, @NotNull RenderType renderType) throws IllegalArgumentException {
        FoliaObjective foliaObjective = new FoliaObjective(name, this);
        foliaObjectiveMap.put(name, foliaObjective);
        foliaObjective.display = displayName;
        foliaObjective.renderType = renderType;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            foliaObjective.fullSend(onlinePlayer);
        }
        return foliaObjective;
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @Nullable Component displayName) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @Nullable Component displayName, @NotNull RenderType renderType) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull String criteria, @NotNull String displayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull String criteria, @NotNull String displayName, @NotNull RenderType renderType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @NotNull String displayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @NotNull String displayName, @NotNull RenderType renderType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Objective getObjective(@NotNull String name) {
        return foliaObjectiveMap.get(name);
    }

    @Override
    public @NotNull Set<Objective> getObjectivesByCriteria(@NotNull String criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Set<Objective> getObjectivesByCriteria(@NotNull Criteria criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Set<Objective> getObjectives() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Objective getObjective(@NotNull DisplaySlot slot) {
        return displaySlotFoliaObjectiveMap.get(slot);
    }

    @Override
    public @NotNull Set<Score> getScores(@NotNull OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Set<Score> getScores(@NotNull String entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetScores(@NotNull OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetScores(@NotNull String entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Team getPlayerTeam(@NotNull OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Team getEntryTeam(@NotNull String entry) {
        for (FoliaTeam value : teamMap.values()) {
            if (value.members.contains(entry)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public @Nullable Team getTeam(@NotNull String teamName) {
        return teamMap.get(teamName);
    }

    @Override
    public @NotNull Set<Team> getTeams() {
        return new HashSet<>(teamMap.values());
    }

    @Override
    public @NotNull Team registerNewTeam(@NotNull String name) {
        FoliaTeam foliaTeam = new FoliaTeam(this, name);
        teamMap.put(name, foliaTeam);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            foliaTeam.fullSend(onlinePlayer);
        }
        return foliaTeam;
    }

    @Override
    public @NotNull Set<OfflinePlayer> getPlayers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Set<String> getEntries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearSlot(@NotNull DisplaySlot slot) {
        ClientboundSetDisplayObjectivePacket clientboundSetDisplayObjectivePacket = ScoreboardPacket.setDisplaySlot("", slot);
        ScoreboardPacket.broadcast(clientboundSetDisplayObjectivePacket);
        FoliaObjective remove = displaySlotFoliaObjectiveMap.remove(slot);
        remove.displaySlot = null;
    }

    @Override
    public @NotNull Set<Score> getScoresFor(@NotNull Entity entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetScoresFor(@NotNull Entity entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Team getEntityTeam(@NotNull Entity entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    public void fullSend(Player player) {
        for (FoliaTeam value : teamMap.values()) {
            value.fullSend(player);
        }
        for (FoliaObjective value : foliaObjectiveMap.values()) {
            value.fullSend(player);
        }
        displaySlotFoliaObjectiveMap.forEach((displaySlot, foliaObjective) -> {
            ClientboundSetDisplayObjectivePacket clientboundSetDisplayObjectivePacket = ScoreboardPacket.setDisplaySlot(foliaObjective.getName(), displaySlot);
            ScoreboardPacket.send(player, clientboundSetDisplayObjectivePacket);
        });

    }

    public void removeTeam(FoliaTeam foliaTeam) {
        teamMap.remove(foliaTeam.name);
        ClientboundSetPlayerTeamPacket clientboundSetPlayerTeamPacket = ScoreboardPacket.removeTeam(foliaTeam.name);
        ScoreboardPacket.broadcast(clientboundSetPlayerTeamPacket);
    }

    public void removeObjective(FoliaObjective foliaObjective) {
        foliaObjectiveMap.remove(foliaObjective.name);
        ClientboundSetObjectivePacket clientboundSetObjectivePacket = ScoreboardPacket.removeObjective(foliaObjective.name);
        ScoreboardPacket.broadcast(clientboundSetObjectivePacket);
    }
}
