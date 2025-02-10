package com.molean.folia.adapter.scoreborad;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FoliaObjective implements Objective {

    String name;
    FoliaScoreboard scoreboard;
    Component display;
    DisplaySlot displaySlot;
    RenderType renderType;
    Map<String, FoliaScore> scores = new HashMap<>();
    NumberFormat numberFormat;


    public FoliaObjective(String name, FoliaScoreboard scoreboard) {
        this.name = name;
        this.scoreboard = scoreboard;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Component displayName() {
        return display;
    }

    private ObjectiveCriteria.RenderType renderType() {
        return switch (renderType) {
            case HEARTS -> ObjectiveCriteria.RenderType.HEARTS;
            case INTEGER -> ObjectiveCriteria.RenderType.INTEGER;
        };
    }

    @Override
    public void displayName(@Nullable Component displayName) {
        this.display = displayName;
        ClientboundSetObjectivePacket clientboundSetObjectivePacket = ScoreboardPacket.updateObjective(name, displayName, renderType());
        ScoreboardPacket.broadcast(clientboundSetObjectivePacket);
    }

    @Override
    public @NotNull String getDisplayName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String getCriteria() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Criteria getTrackedCriteria() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isModifiable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public void unregister() {
        scoreboard.removeObjective(this);
    }

    @Override
    public void setDisplaySlot(@Nullable DisplaySlot slot) {
        scoreboard.displaySlotFoliaObjectiveMap.put(slot, this);
        this.displaySlot = slot;
        ScoreboardPacket.broadcast(ScoreboardPacket.setDisplaySlot(name, displaySlot));
    }

    @Override
    public @Nullable DisplaySlot getDisplaySlot() {
        return displaySlot;
    }

    @Override
    public void setRenderType(@NotNull RenderType renderType) {
        this.renderType = renderType;
        ClientboundSetObjectivePacket clientboundSetObjectivePacket = ScoreboardPacket.updateObjective(name, display, renderType());
        ScoreboardPacket.broadcast(clientboundSetObjectivePacket);
    }

    @Override
    public @NotNull RenderType getRenderType() {
        return renderType;
    }

    @Override
    public @NotNull Score getScore(@NotNull OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Score getScore(@NotNull String entry) {
        return scores.get(entry);
    }

    @Override
    public @NotNull Score getScoreFor(@NotNull Entity entity) throws IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean willAutoUpdateDisplay() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAutoUpdateDisplay(boolean autoUpdateDisplay) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable NumberFormat numberFormat() {
        return numberFormat;
    }

    @Override
    public void numberFormat(@Nullable NumberFormat format) {
        this.numberFormat = format;
    }

    public void fullSend(Player player) {
        ClientboundSetObjectivePacket objective = ScoreboardPacket.createObjective(name, display, renderType());
        ScoreboardPacket.send(player, objective);

        scores.forEach((s, foliaScore) -> {
            foliaScore.fullSend(player);
        });
    }
}
