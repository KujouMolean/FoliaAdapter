package com.molean.folia.adapter;

import io.papermc.paper.threadedregions.TickRegionScheduler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class FoliaUtils {

    public static Iterable<Entity> getLocalEntities() {
        return TickRegionScheduler.getCurrentRegionizedWorldData().getLocalEntities();
    }

    public static List<Player> getLocalPlayers() {
        return TickRegionScheduler.getCurrentRegionizedWorldData().getLocalPlayers().stream().map(ServerPlayer::getBukkitEntity).map(craftPlayer -> (Player) craftPlayer).toList();
    }
}
