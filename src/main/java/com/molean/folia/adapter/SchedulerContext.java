package com.molean.folia.adapter;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class SchedulerContext {

    public static final class EntitySchedulerContext extends SchedulerContext {
        private final Entity entity;

        public EntitySchedulerContext(Entity entity) {
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }
        @Override
        public Pair<Entity, Location> toPair() {
            return Pair.of(entity, null);
        }

        @Override
        public ScheduledTask runTask(Plugin plugin, Runnable runnable) {
            return Folia.getScheduler().runTask(plugin, entity, runnable);
        }

        @Override
        public ScheduledTask runTaskLater(Plugin plugin, Runnable runnable, int delay) {
            return Folia.getScheduler().runTaskLater(plugin, entity, runnable,delay);
        }

        @Override
        public ScheduledTask runTaskTimer(Plugin plugin, Runnable runnable, int delay, int period) {
            return Folia.getScheduler().runTaskTimer(plugin, runnable, entity, delay, period);
        }
    }


    public static final class LocationSchedulerContext extends SchedulerContext {
        public World getWorld() {
            return world;
        }

        public int getChunkX() {
            return chunkX;
        }

        public int getChunkZ() {
            return chunkZ;
        }

        private final World world;
        private final int chunkX;
        private final int chunkZ;

        public LocationSchedulerContext(World world, int chunkX, int chunkZ) {
            this.world = world;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }
        public LocationSchedulerContext(Location location) {
            this.world = location.getWorld();
            this.chunkX = location.getBlockX() >> 4;
            this.chunkZ = location.getBlockZ() >> 4;
        }

        public Location location() {
            return new Location(world, chunkX << 4, 0, chunkZ << 4);
        }

        @Override
        public Pair<Entity, Location> toPair() {
            return Pair.of(null, location());
        }

        @Override
        public ScheduledTask runTask(Plugin plugin, Runnable runnable) {
            return Folia.getScheduler().runTask(plugin, location(), runnable);
        }

        @Override
        public ScheduledTask runTaskLater(Plugin plugin, Runnable runnable, int delay) {
            return Folia.getScheduler().runTaskLater(plugin, location(), runnable,delay);
        }

        @Override
        public ScheduledTask runTaskTimer(Plugin plugin, Runnable runnable, int delay, int period) {
            return Folia.getScheduler().runTaskTimer(plugin, runnable, location(), delay, period);
        }
    }

    private SchedulerContext() {
    }

    public static SchedulerContext of(Location location) {
        return new LocationSchedulerContext(location);
    }

    public static SchedulerContext of(Entity entity) {
        return new EntitySchedulerContext(entity);
    }

    public Pair<Entity, Location> toPair() {
        return Pair.of(null, null);
    }

    public ScheduledTask runTask(Plugin plugin,Runnable runnable) {
        throw new RuntimeException("Context not enough");
    }

    public ScheduledTask runTaskLater(Plugin plugin,Runnable runnable, int delay) {
        throw new RuntimeException("Context not enough");
    }

    public ScheduledTask runTaskTimer(Plugin plugin, Runnable runnable, int delay, int period) {
        throw new RuntimeException("Context not enough");
    }

}
