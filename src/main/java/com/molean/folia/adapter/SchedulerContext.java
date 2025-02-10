package com.molean.folia.adapter;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

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

        @Override
        public Pair<Entity, Location> toPair() {
            return Pair.of(null, new Location(world, chunkX << 4, 0, chunkZ << 4));
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

}
