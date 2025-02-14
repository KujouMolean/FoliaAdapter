package com.molean.folia.adapter.scoreborad;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
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

import java.util.*;
import java.util.stream.Collectors;

public class FoliaScoreboard implements Scoreboard {

    final Map<String, FoliaObjective> foliaObjectiveMap = new HashMap<>();
    final Map<DisplaySlot, FoliaObjective> displaySlotFoliaObjectiveMap = new HashMap<>();
    final Map<String, FoliaTeam> teamMap = new HashMap<>();
    final FoliaScoreboardManager manager;
    final List<UUID> viewers = new ArrayList<>();


    public FoliaScoreboard(FoliaScoreboardManager manager) {
        this.manager = manager;
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull String criteria) {
        return registerNewObjective(name, criteria, Component.text(name));
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @Nullable String criteria, @Nullable Component displayName) {
        return registerNewObjective(name, criteria, displayName, RenderType.INTEGER);

    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @Nullable String criteria, @Nullable Component displayName, @NotNull RenderType renderType) throws IllegalArgumentException {
        FoliaObjective foliaObjective = new FoliaObjective(name, this);
        FoliaObjective put = foliaObjectiveMap.put(name, foliaObjective);
        if (put != null) {
            Bukkit.getOnlinePlayers().stream().filter(player -> viewers.contains(player.getUniqueId())).forEach(this::clearFor);
        }
        foliaObjective.display = displayName;
        foliaObjective.renderType = renderType;
        Bukkit.getOnlinePlayers().stream().filter(player -> viewers.contains(player.getUniqueId())).forEach(this::fullSend);
        return foliaObjective;
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @Nullable Component displayName) throws IllegalArgumentException {
        return registerNewObjective(name, criteria.getName(), displayName);
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @Nullable Component displayName, @NotNull RenderType renderType) throws IllegalArgumentException {
        return registerNewObjective(name, criteria.getName(), displayName, renderType);
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull String criteria, @NotNull String displayName) {
        return registerNewObjective(name,criteria, Component.text(displayName));
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull String criteria, @NotNull String displayName, @NotNull RenderType renderType) {
        return registerNewObjective(name, criteria, Component.text(displayName));
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @NotNull String displayName) {
        return registerNewObjective(name, criteria, Component.text(displayName));
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @NotNull String displayName, @NotNull RenderType renderType) {
        return registerNewObjective(name, criteria, Component.text(displayName), renderType);
    }

    @Override
    public @Nullable Objective getObjective(@NotNull String name) {
        return foliaObjectiveMap.get(name);
    }

    @Override
    public @NotNull Set<Objective> getObjectivesByCriteria(@NotNull String criteria) {
        return foliaObjectiveMap.values().stream().filter(foliaObjective -> Objects.equals(criteria, foliaObjective.getCriteria())).collect(Collectors.toSet());
    }

    @Override
    public @NotNull Set<Objective> getObjectivesByCriteria(@NotNull Criteria criteria) {
        return getObjectivesByCriteria(criteria.getName());
    }

    @Override
    public @NotNull Set<Objective> getObjectives() {
        return new HashSet<>(foliaObjectiveMap.values());
    }

    @Override
    public @Nullable Objective getObjective(@NotNull DisplaySlot slot) {
        return displaySlotFoliaObjectiveMap.get(slot);
    }

    @Override
    public @NotNull Set<Score> getScores(@NotNull OfflinePlayer player) {
        return getScores(Objects.requireNonNull(player.getUniqueId().toString()));
    }

    @Override
    public @NotNull Set<Score> getScores(@NotNull String entry) {
        return foliaObjectiveMap.values().stream().map(foliaObjective -> foliaObjective.getScore(entry)).collect(Collectors.toSet());
    }

    @Override
    public void resetScores(@NotNull OfflinePlayer player) {
        resetScores(player.getUniqueId().toString());
    }

    @Override
    public void resetScores(@NotNull String entry) {
        foliaObjectiveMap.values().forEach(foliaObjective -> {
            foliaObjective.scores.remove(entry);
        });
    }

    @Override
    public @Nullable Team getPlayerTeam(@NotNull OfflinePlayer player) {
        return getEntryTeam(Objects.requireNonNull(player.getUniqueId()).toString());
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
        ArrayList<String> strings = new ArrayList<>();
        for (FoliaObjective value : foliaObjectiveMap.values()) {
            strings.addAll(new HashSet<>(value.scores.keySet()));
        }
        return new HashSet<>(strings);
    }

    @Override
    public void clearSlot(@NotNull DisplaySlot slot) {
        ClientboundSetDisplayObjectivePacket clientboundSetDisplayObjectivePacket = ScoreboardPacket.setDisplaySlot("", slot);
        broadcast(clientboundSetDisplayObjectivePacket);
        FoliaObjective remove = displaySlotFoliaObjectiveMap.remove(slot);
        remove.displaySlot = null;
    }

    @Override
    public @NotNull Set<Score> getScoresFor(@NotNull Entity entity) throws IllegalArgumentException {
        return getScores(entity.getUniqueId().toString());
    }

    @Override
    public void resetScoresFor(@NotNull Entity entity) throws IllegalArgumentException {
        resetScores(entity.getUniqueId().toString());
    }

    @Override
    public @Nullable Team getEntityTeam(@NotNull Entity entity) throws IllegalArgumentException {
        return getEntryTeam(entity.getUniqueId().toString());
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

    public void clearFor(Player player) {
        for (FoliaTeam value : teamMap.values()) {
            value.clearFor(player);
        }
        for (FoliaObjective value : foliaObjectiveMap.values()) {
            value.clearFor(player);
        }
    }

    public void removeTeam(FoliaTeam foliaTeam) {
        teamMap.remove(foliaTeam.name);
        ClientboundSetPlayerTeamPacket clientboundSetPlayerTeamPacket = ScoreboardPacket.removeTeam(foliaTeam.name);
        broadcast(clientboundSetPlayerTeamPacket);
    }

    public void removeObjective(FoliaObjective foliaObjective) {
        foliaObjectiveMap.remove(foliaObjective.name);
        ClientboundSetObjectivePacket clientboundSetObjectivePacket = ScoreboardPacket.removeObjective(foliaObjective.name);
        broadcast(clientboundSetObjectivePacket);
    }

    public void broadcast(Packet<?> packet) {
        Bukkit.getOnlinePlayers().stream().filter(player -> viewers.contains(player.getUniqueId())).forEach(player -> ScoreboardPacket.send(player, packet));
    }
}
