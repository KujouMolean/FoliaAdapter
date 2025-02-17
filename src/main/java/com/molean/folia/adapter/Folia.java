package com.molean.folia.adapter;

import io.papermc.paper.threadedregions.RegionizedServerInitEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Folia {
    private static Scheduler scheduler = new Scheduler();

    public static Scheduler getScheduler() {
        return scheduler;
    }

    public static class Scheduler {
        public ScheduledTask runTaskTimer(Plugin plugin, Runnable runnable, Entity entity, long delay, long period) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return entity.getScheduler()
                    .runAtFixedRate(
                            plugin, scheduledTask -> runnable.run(), null, Math.max(1, delay), Math.max(1, period));
        }

        public ScheduledTask runTaskTimer(
                Plugin plugin, Runnable runnable, Location location, long delay, long period) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getRegionScheduler()
                    .runAtFixedRate(
                            plugin, location, scheduledTask -> runnable.run(), Math.max(1, delay), Math.max(1, period));
        }

        public ScheduledTask runTaskTimer(
                Plugin plugin, Consumer<ScheduledTask> runnable, Entity entity, long delay, long period) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return entity.getScheduler()
                    .runAtFixedRate(plugin, runnable, null, Math.max(1, delay), Math.max(1, period));
        }

        public ScheduledTask runTaskTimer(
                Plugin plugin, Consumer<ScheduledTask> runnable, Location location, long delay, long period) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getRegionScheduler()
                    .runAtFixedRate(plugin, location, runnable, Math.max(1, delay), Math.max(1, period));
        }


        public ScheduledTask runTaskTimerAsynchronously(
                Plugin plugin, Runnable runnable, long delay, long period) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getAsyncScheduler()
                    .runAtFixedRate(
                            plugin,
                            scheduledTask -> runnable.run(),
                            Math.max(1, delay) * 50L,
                            Math.max(1, period) * 50L,
                            TimeUnit.MILLISECONDS);
        }


        public ScheduledTask runTaskTimerGlobally(
                Plugin plugin, Runnable runnable, long delay, long period) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getGlobalRegionScheduler()
                    .runAtFixedRate(
                            plugin,
                            scheduledTask -> runnable.run(),
                            Math.max(1, delay), Math.max(1, period)
                    );
        }

        public @NotNull ScheduledTask runTaskLaterAsync(Plugin plugin, Runnable runnable, long delay) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getAsyncScheduler()
                    .runDelayed(
                            plugin, scheduledTask -> runnable.run(), Math.max(1, delay) * 50L, TimeUnit.MILLISECONDS);
        }

        public @Nullable ScheduledTask scheduleSyncDelayedTask(
                Plugin plugin, Entity entity, Runnable runnable, long delay) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return entity.getScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), null, Math.max(1, delay));
        }

        public @Nullable ScheduledTask scheduleSyncDelayedTask(
                Plugin plugin, Location location, Runnable runnable, long delay) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getRegionScheduler()
                    .runDelayed(plugin, location, scheduledTask -> runnable.run(), Math.max(1, delay));
        }

        public @Nullable ScheduledTask runTask(Plugin plugin, Entity entity, Runnable runnable) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return entity.getScheduler().run(plugin, scheduledTask -> runnable.run(), null);
        }

        public @Nullable ScheduledTask runTask(Plugin plugin, SchedulerContext context, Runnable runnable) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            if (context instanceof SchedulerContext.EntitySchedulerContext entitySchedulerContext) {
                return runTask(plugin, entitySchedulerContext.getEntity(), runnable);
            }
            if (context instanceof SchedulerContext.LocationSchedulerContext locationSchedulerContext) {
                return runTask(plugin, locationSchedulerContext.getWorld(), locationSchedulerContext.getChunkX(), locationSchedulerContext.getChunkZ(), runnable);
            }
            throw new RuntimeException("No enough context.");
        }

        public @Nullable ScheduledTask runTaskGlobally(Plugin plugin, Runnable runnable) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> runnable.run());
        }

        public @Nullable ScheduledTask runTask(Plugin plugin, World world, int chunkX, int chunkZ, Runnable runnable) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getRegionScheduler().run(plugin, world, chunkX, chunkZ, scheduledTask -> runnable.run());
        }

        public @Nullable ScheduledTask runTask(Plugin plugin, Location location, Runnable runnable) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getRegionScheduler().run(plugin, location, scheduledTask -> runnable.run());
        }
        public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> callable, Pair<Entity, Location> right) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            CompletableFuture<T> tCompletableFuture = new CompletableFuture<>();
            if (right.left() == null) {
                if (right.right() == null) {
                    throw new RuntimeException("Context not enough!");
                }
                runTask(plugin, right.right(), () -> {
                    try {
                        tCompletableFuture.complete(callable.call());
                    } catch (Exception e) {
                        tCompletableFuture.completeExceptionally(e);
                    }
                });
            } else {
                runTask(plugin, right.left(), () -> {
                    try {
                        tCompletableFuture.complete(callable.call());
                    } catch (Exception e) {
                        tCompletableFuture.completeExceptionally(e);
                    }
                });
            }
            return tCompletableFuture;
        }

        public @Nullable ScheduledTask runTaskLater(Plugin plugin, Entity entity, Runnable runnable, long delay) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return entity.getScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), null, Math.max(1, delay));
        }

        public @Nullable ScheduledTask runTaskLaterGlobally(Plugin plugin, Runnable runnable, long delay) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), Math.max(1, delay));
        }


        public @Nullable ScheduledTask runTaskLater(Plugin plugin, Location location, Runnable runnable, long delay) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getRegionScheduler()
                    .runDelayed(plugin, location, scheduledTask -> runnable.run(), Math.max(1, delay));
        }

        public ScheduledTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
        }

        public ScheduledTask runTaskAsynchronously(Plugin plugin, Consumer<ScheduledTask> runnable) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getAsyncScheduler().runNow(plugin, runnable);
        }

        public ScheduledTask runTaskTimerAsynchronously(
                Plugin plugin, Consumer<ScheduledTask> consumer, long delay, long period) {
            if (!plugin.isEnabled()) {
                FoliaAdapter.getPlugin().getLogger().warning(plugin.getName() + " try to register a schedule while disabled, ignore.");
                return null;
            }
            return Bukkit.getAsyncScheduler()
                    .runAtFixedRate(
                            plugin,
                            consumer,
                            Math.max(1, delay) * 50L,
                            Math.max(1, period) * 50L,
                            TimeUnit.MILLISECONDS);
        }
    }


    public static void setPlugin(Plugin plugin) {

    }

    public static ScheduledTask runSync(Runnable runnable, Entity entity, long delay) {
        return getScheduler().runTaskLater(FoliaAdapter.getPlugin(), entity, runnable, delay);
    }

    public static ScheduledTask runGlobally(Runnable runnable) {
        return getScheduler().runTaskGlobally(FoliaAdapter.getPlugin(), runnable);
    }

    public static ScheduledTask runGlobally(Runnable runnable,int delay) {
        return getScheduler().runTaskLaterGlobally(FoliaAdapter.getPlugin(), runnable, delay);
    }

    public static ScheduledTask runSync(Runnable runnable, Location location, long delay) {
        return getScheduler().runTaskLater(FoliaAdapter.getPlugin(), location, runnable, delay);
    }

    public static ScheduledTask runSync(Runnable runnable, Entity entity) {
        return runSync(runnable, entity, 1);
    }

    public static ScheduledTask runSync(Runnable runnable, Location location) {
        return runSync(runnable, location, 1);
    }

    public static void runAtFirstTick(Plugin plugin, Runnable runnable) {
        Bukkit.getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            public void on(RegionizedServerInitEvent event) {
                                runnable.run();
                            }
                        },
                        plugin);
    }

    private static boolean paper = false;

    public static class PluginManager {

        public void callEvent(Event event) {
            ce(event);
        }

        public void ce(Event event) {
            if (!paper) {
                try {
                    // 如果是Folia，关闭callEvent的线程检查
                    Constructor<Unsafe> declaredConstructor = Unsafe.class.getDeclaredConstructor();
                    declaredConstructor.setAccessible(true);
                    Unsafe unsafe = declaredConstructor.newInstance();
                    Field asyncField = Event.class.getDeclaredField("async");
                    unsafe.putBoolean(
                            event,
                            unsafe.objectFieldOffset(asyncField),
                            !Class.forName("ca.spottedleaf.moonrise.common.util.TickThread")
                                    .isAssignableFrom(Thread.currentThread().getClass()));
                } catch (NoSuchFieldException
                        | IllegalAccessException
                        | ClassNotFoundException
                        | InstantiationException
                        | NoSuchMethodException
                        | InvocationTargetException ignored) {
                    // paper
                    paper = true;
                    return;
                }
            }
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    private static final PluginManager pluginManager = new PluginManager();

    public static PluginManager getPluginManager() {
        return pluginManager;
    }
}
