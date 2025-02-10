package com.molean.folia.adapter.scoreborad;

import io.netty.buffer.Unpooled;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboardTranslations;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class ScoreboardPacket {

    public static void send(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public static void broadcast(Packet<?> packet) {
        Bukkit.getOnlinePlayers().forEach(player -> send(player, packet));
    }

    public static ClientboundSetScorePacket updateScore(String key, String objective, Component display, int score, NumberFormat numberFormat) {
        return new ClientboundSetScorePacket(key, objective, score, Optional.of(PaperAdventure.asVanilla(display)), Optional.of(numberFormat));
    }


    public static ClientboundResetScorePacket removeScore(String key, String objective) {
        return new ClientboundResetScorePacket(key, objective);
    }

    private static ClientboundSetObjectivePacket objective(String objective, int method, Component display, ObjectiveCriteria.RenderType renderType) {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), CraftRegistry.getMinecraftRegistry());
        buf.writeUtf(objective);
        buf.writeByte(method);
        if (method != 1) {
            ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, PaperAdventure.asVanilla(display));
            buf.writeEnum(renderType);
            NumberFormatTypes.OPTIONAL_STREAM_CODEC.encode(buf, Optional.of(StyledFormat.NO_STYLE));
        }
        return ClientboundSetObjectivePacket.STREAM_CODEC.decode(buf);
    }

    public static ClientboundSetObjectivePacket createObjective(String objective, Component display, ObjectiveCriteria.RenderType renderType) {
        return objective(objective, ClientboundSetObjectivePacket.METHOD_ADD, display, renderType);
    }

    public static ClientboundSetObjectivePacket updateObjective(String objective, Component display, ObjectiveCriteria.RenderType renderType) {
        return objective(objective, ClientboundSetObjectivePacket.METHOD_CHANGE, display, renderType);
    }

    public static ClientboundSetObjectivePacket removeObjective(String objective) {
        return objective(objective, ClientboundSetObjectivePacket.METHOD_REMOVE, null, null);
    }

    public static ClientboundSetDisplayObjectivePacket setDisplaySlot(String objective, DisplaySlot displaySlot) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeByte(CraftScoreboardTranslations.fromBukkitSlot(displaySlot).id());
        buf.writeUtf(objective);
        return ClientboundSetDisplayObjectivePacket.STREAM_CODEC.decode(buf);
    }

    public static ClientboundSetPlayerTeamPacket createTeam(String team, ClientboundSetPlayerTeamPacket.Parameters parameters, Collection<String> players) {
        return setPlayerTeam(team, 0, parameters, players);
    }

    public static ClientboundSetPlayerTeamPacket updateTeam(String team, ClientboundSetPlayerTeamPacket.Parameters parameters, Collection<String> players) {
        return setPlayerTeam(team, 2, parameters, players);
    }

    public static ClientboundSetPlayerTeamPacket removeTeam(String team) {
        return setPlayerTeam(team, 1, null, Set.of());
    }

    public static ClientboundSetPlayerTeamPacket joinTeam(String team, Collection<String> entries) {

        return setPlayerTeam(team, 3, null, entries);
    }

    public static ClientboundSetPlayerTeamPacket leaveTeam(String team, Collection<String> entries) {
        return setPlayerTeam(team, 4, null, entries);
    }

    private static ClientboundSetPlayerTeamPacket setPlayerTeam(String team, int method, ClientboundSetPlayerTeamPacket.Parameters parameters, Collection<String> players) {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), CraftRegistry.getMinecraftRegistry());
        buf.writeUtf(team);
        buf.writeByte(method);
        if (shouldHaveParameters(method)) {
            parameters.write(buf);
        }

        if (shouldHavePlayerList(method)) {
            buf.writeCollection(players, FriendlyByteBuf::writeUtf);
        }

        return ClientboundSetPlayerTeamPacket.STREAM_CODEC.decode(buf);
    }


    public static ClientboundSetPlayerTeamPacket.Parameters parameters(Component displayName, int options, String nametagVisibility, String collisionRule, ChatFormatting color, Component prefix, Component suffix) {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), CraftRegistry.getMinecraftRegistry());
        net.minecraft.network.chat.Component display = PaperAdventure.asVanilla(displayName);
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, display);
        buf.writeByte(options);
        buf.writeUtf(nametagVisibility);
        buf.writeUtf(collisionRule);
        buf.writeEnum(color);
        net.minecraft.network.chat.Component prefixComponent = PaperAdventure.asVanilla(prefix);
        net.minecraft.network.chat.Component suffixComponent = PaperAdventure.asVanilla(suffix);
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, prefixComponent);
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, suffixComponent);
        return new ClientboundSetPlayerTeamPacket.Parameters(buf);
    }

    private static boolean shouldHavePlayerList(int packetType) {
        return packetType == 0 || packetType == 3 || packetType == 4;
    }

    private static boolean shouldHaveParameters(int packetType) {
        return packetType == 0 || packetType == 2;
    }
}
