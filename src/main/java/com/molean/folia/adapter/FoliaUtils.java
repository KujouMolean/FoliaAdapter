package com.molean.folia.adapter;

import com.google.common.collect.Iterables;
import io.papermc.paper.threadedregions.TickRegionScheduler;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FoliaUtils {

    public static Iterable<Entity> getLocalEntities() {
        return Iterables.transform(TickRegionScheduler.getCurrentRegionizedWorldData().getLocalEntities(), net.minecraft.world.entity.Entity::getBukkitEntity);
    }

    public static List<Player> getLocalPlayers() {
        return TickRegionScheduler.getCurrentRegionizedWorldData().getLocalPlayers().stream().map(ServerPlayer::getBukkitEntity).map(craftPlayer -> (Player) craftPlayer).toList();
    }

    public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
        return entity.teleportAsync(location);
    }
}
