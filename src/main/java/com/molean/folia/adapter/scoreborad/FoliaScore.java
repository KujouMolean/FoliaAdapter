package com.molean.folia.adapter.scoreborad;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import io.papermc.paper.util.PaperScoreboardFormat;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoliaScore implements Score {
    String entry;
    FoliaObjective objective;
    int score = 0;
    boolean set = false;
    Component customName = Component.empty();
    NumberFormat numberFormat = NumberFormat.noStyle();

    public FoliaScore(String entry, FoliaObjective objective) {
        this.entry = entry;
        this.customName = Component.text(entry);
        this.objective = objective;
    }

    @Override
    public @NotNull OfflinePlayer getPlayer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String getEntry() {
        return entry;
    }

    @Override
    public @NotNull Objective getObjective() {
        return objective;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
        set = true;
        objective.scoreboard.broadcast(updatePacket());
    }

    @Override
    public boolean isScoreSet() {
        return set;
    }

    @Override
    public @Nullable Scoreboard getScoreboard() {
        return objective.getScoreboard();
    }

    @Override
    public void resetScore() throws IllegalStateException {
        set = false;
        score = 0;
        objective.scoreboard.broadcast(updatePacket());
    }

    @Override
    public boolean isTriggerable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTriggerable(boolean triggerable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Component customName() {
        return customName;
    }

    @Override
    public void customName(@Nullable Component customName) {
        this.customName = customName;
        objective.scoreboard.broadcast(updatePacket());
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
        ScoreboardPacket.send(player, updatePacket());
    }

    public Packet<?> updatePacket() {
        return ScoreboardPacket.updateScore(entry, objective.name, customName, score, PaperScoreboardFormat.asVanilla(numberFormat));
    }
    public Packet<?> removePacket() {
        return ScoreboardPacket.removeScore(entry, objective.name);
    }
}
