package com.molean.folia.adapter;

import com.google.common.collect.Iterables;
import io.papermc.paper.threadedregions.TickRegionScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
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

    public static CompletableFuture<Location> getPlayerRespawnLocation(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer handle = craftPlayer.getHandle();
        ServerLevel world = handle.server.getLevel(handle.getRespawnDimension());
        BlockPos bed = handle.getRespawnPosition();
        CompletableFuture<Location> locationCompletableFuture = new CompletableFuture<>();
        if (world != null && bed != null) {
            Folia.runSync(() -> {
                Optional<ServerPlayer.RespawnPosAngle> spawnLoc = ServerPlayer.findRespawnAndUseSpawnBlock(world, bed, handle.getRespawnAngle(), handle.isRespawnForced(), true);
                if (spawnLoc.isPresent()) {
                    ServerPlayer.RespawnPosAngle vec = spawnLoc.get();
                    locationCompletableFuture.complete(CraftLocation.toBukkit(vec.position(), world.getWorld(), vec.yaw(), 0));
                } else {
                    locationCompletableFuture.complete(null);
                }
            }, new Location(world.getWorld(), bed.getX(), bed.getY(), bed.getZ()));
            return locationCompletableFuture;
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

}
