package com.molean.folia.adapter.scoreborad;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FoliaTeam implements Team {

    final FoliaScoreboard scoreboard;
    final String name;
    Component display;
    Component prefix = Component.empty();
    Component suffix = Component.empty();
    ChatFormatting color = ChatFormatting.RESET;
    final List<String> members = new ArrayList<>();

    public FoliaTeam(FoliaScoreboard scoreboard, String name) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.display = Component.text(name);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Component displayName() {
        return display;
    }

    public int packOptions() {
        int i = 0;
        if (this.allowFriendlyFire()) {
            i |= 1;
        }

        if (this.canSeeFriendlyInvisibles()) {
            i |= 2;
        }

        return i;
    }


    private ClientboundSetPlayerTeamPacket.Parameters parameters() {
        return ScoreboardPacket.parameters(display, packOptions(), getOption(Option.NAME_TAG_VISIBILITY).name(), getOption(Option.COLLISION_RULE).name(),
                color, prefix, suffix);
    }

    @Override
    public void displayName(@Nullable Component displayName) {
        this.display = displayName;
        ScoreboardPacket.updateTeam(name, parameters(), members);
    }

    @Override
    public @NotNull Component prefix() {
        return prefix;
    }

    @Override
    public void prefix(@Nullable Component prefix) {
        this.prefix = prefix;
        ScoreboardPacket.updateTeam(name, parameters(), members);
    }

    @Override
    public @NotNull Component suffix() {
        return suffix;
    }

    @Override
    public void suffix(@Nullable Component suffix) {
        this.suffix = suffix;
        ScoreboardPacket.updateTeam(name, parameters(), members);
    }

    @Override
    public boolean hasColor() {
        return color == ChatFormatting.RESET;
    }

    @Override
    public @NotNull TextColor color() {
        return PaperAdventure.asAdventure(color);
    }

    @Override
    public void color(@Nullable NamedTextColor namedTextColor) {
        if (namedTextColor == null) {
            color  = ChatFormatting.RESET;
            return;
        }
        this.color = PaperAdventure.asVanilla(namedTextColor);
        ScoreboardPacket.updateTeam(name, parameters(), members);
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
    public @NotNull String getPrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String getSuffix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSuffix(@NotNull String suffix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ChatColor getColor() {
        return CraftChatMessage.getColor(color);
    }

    @Override
    public void setColor(@NotNull ChatColor color) {
        this.color = CraftChatMessage.getColor(color);
    }

    private boolean ff = false;

    @Override
    public boolean allowFriendlyFire() {
        return ff;
    }

    @Override
    public void setAllowFriendlyFire(boolean enabled) {
        this.ff = enabled;
        ScoreboardPacket.updateTeam(name, parameters(), members);
    }

    private boolean fi = false;

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return fi;
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean enabled) {
        this.fi = enabled;
        ScoreboardPacket.updateTeam(name, parameters(), members);
    }

    @Override
    public @NotNull NameTagVisibility getNameTagVisibility() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNameTagVisibility(@NotNull NameTagVisibility visibility) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Set<OfflinePlayer> getPlayers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Set<String> getEntries() {
        return new HashSet<>(members);
    }

    @Override
    public int getSize() {
        return members.size();
    }

    @Override
    public @Nullable FoliaScoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public void addPlayer(@NotNull OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEntry(@NotNull String entry) {
        members.add(entry);
        for (Team team : scoreboard.getTeams()) {
            if (team instanceof FoliaTeam foliaTeam) {
                foliaTeam.members.remove(entry);
            }
        }
        ScoreboardPacket.joinTeam(name, List.of(entry));
    }


    @Override
    public void addEntities(@NotNull Collection<Entity> entities) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }


    @Override
    public void addEntries(@NotNull Collection<String> entries) throws IllegalStateException, IllegalArgumentException {
        members.addAll(entries);

        for (String entry : entries) {
            for (Team team : scoreboard.getTeams()) {
                if (team instanceof FoliaTeam foliaTeam) {
                    foliaTeam.members.remove(entry);
                }
            }
        }

        ScoreboardPacket.joinTeam(name, entries);
    }

    @Override
    public boolean removePlayer(@NotNull OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEntry(@NotNull String entry) {
        boolean remove = members.remove(entry);

        ScoreboardPacket.joinTeam(name, List.of(entry));
        return remove;

    }


    @Override
    public boolean removeEntities(@NotNull Collection<Entity> entities) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEntries(@NotNull Collection<String> entries) throws IllegalStateException, IllegalArgumentException {
        boolean good = true;
        for (String entry : entries) {
            good = members.remove(entry);
        }
        ScoreboardPacket.joinTeam(name, entries);
        return good;
    }

    @Override
    public void unregister() {
        scoreboard.removeTeam(this);
    }

    @Override
    public boolean hasPlayer(@NotNull OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasEntry(@NotNull String entry) {
        return members.contains(entry);
    }

    OptionStatus cr = OptionStatus.ALWAYS;
    OptionStatus ntv = OptionStatus.ALWAYS;
    OptionStatus dmv = OptionStatus.ALWAYS;


    @NotNull
    @Override
    public OptionStatus getOption(@NotNull Team.Option option) {
        return switch (option) {
            case NAME_TAG_VISIBILITY -> ntv;
            case DEATH_MESSAGE_VISIBILITY -> dmv;
            case COLLISION_RULE -> cr;
        };
    }

    @Override
    public void setOption(@NotNull Team.Option option, @NotNull Team.OptionStatus status) {
        switch (option) {
            case NAME_TAG_VISIBILITY -> ntv = status;
            case DEATH_MESSAGE_VISIBILITY -> dmv = status;
            case COLLISION_RULE -> cr = status;
        }
        ScoreboardPacket.updateTeam(name, parameters(), members);
    }

    @Override
    public void addEntity(@NotNull Entity entity) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEntity(@NotNull Entity entity) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasEntity(@NotNull Entity entity) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        throw new UnsupportedOperationException();
    }

    public void fullSend(Player onlinePlayer) {
        ClientboundSetPlayerTeamPacket team = ScoreboardPacket.createTeam(name, parameters(), members);
        ScoreboardPacket.send(onlinePlayer, team);
    }

    public void clearFor(Player player) {
        if (scoreboard.viewers.contains(player.getUniqueId())) {
            ClientboundSetPlayerTeamPacket team = ScoreboardPacket.removeTeam(name);
            ScoreboardPacket.send(player, team);
        }
    }
}
