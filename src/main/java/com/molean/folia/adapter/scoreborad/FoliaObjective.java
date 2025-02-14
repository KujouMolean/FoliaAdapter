package com.molean.folia.adapter.scoreborad;

import io.papermc.paper.adventure.PaperAdventure;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FoliaObjective implements Objective {

    String name;
    FoliaScoreboard scoreboard;
    Component display;
    DisplaySlot displaySlot;
    String criteria;
    RenderType renderType;
    Map<String, FoliaScore> scores = new HashMap<>();
    NumberFormat numberFormat;


    public FoliaObjective(String name, FoliaScoreboard scoreboard) {
        this.name = name;
        this.scoreboard = scoreboard;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
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
        scoreboard.broadcast(clientboundSetObjectivePacket);
    }

    @Override
    public @NotNull String getDisplayName() {
        return PaperAdventure.asPlain(display, Locale.CHINA);
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
        displayName(Component.text(displayName));
    }

    @Override
    public @NotNull String getCriteria() {
        return criteria;
    }

    @Override
    public @NotNull Criteria getTrackedCriteria() {
        return new Criteria() {
            @Override
            public @NotNull String getName() {
                return getCriteria();
            }

            @Override
            public boolean isReadOnly() {
                return true;
            }

            @Override
            public @NotNull RenderType getDefaultRenderType() {
                return RenderType.INTEGER;
            }
        };
    }

    @Override
    public boolean isModifiable() {
        return true;
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
        scoreboard.broadcast(ScoreboardPacket.setDisplaySlot(name, displaySlot));
    }

    @Override
    public @Nullable DisplaySlot getDisplaySlot() {
        return displaySlot;
    }

    @Override
    public void setRenderType(@NotNull RenderType renderType) {
        this.renderType = renderType;
        ClientboundSetObjectivePacket clientboundSetObjectivePacket = ScoreboardPacket.updateObjective(name, display, renderType());
        scoreboard.broadcast(clientboundSetObjectivePacket);
    }

    @Override
    public @NotNull RenderType getRenderType() {
        return renderType;
    }

    @Override
    public @NotNull Score getScore(@NotNull OfflinePlayer player) {
        return getScore(Objects.requireNonNull(player.getUniqueId().toString()));
    }

    @Override
    public @NotNull Score getScore(@NotNull String entry) {
        return scores.getOrDefault(entry, new FoliaScore(entry, this));
    }

    @Override
    public @NotNull Score getScoreFor(@NotNull Entity entity) throws IllegalArgumentException, IllegalStateException {
        return getScore(entity.getUniqueId().toString());
    }

    @Override
    public boolean willAutoUpdateDisplay() {
        return true;
    }

    @Override
    public void setAutoUpdateDisplay(boolean autoUpdateDisplay) {

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

    public void clearFor(Player player) {
        ClientboundSetObjectivePacket objective = ScoreboardPacket.removeObjective(name);
        ScoreboardPacket.send(player, objective);
    }
}
