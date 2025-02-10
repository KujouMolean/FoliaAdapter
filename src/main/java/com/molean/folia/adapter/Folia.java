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
import org.bukkit.plugin.java.JavaPlugin;
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

    private static boolean disable = false;

    public static void disable() {
        Folia.disable = true;
    }

    private static Scheduler scheduler = new Scheduler();

    public static Scheduler getScheduler() {
        return scheduler;
    }

    public static class Scheduler {
        public ScheduledTask runTaskTimer(Plugin plugin, Runnable runnable, Entity entity, long delay, long period) {
            if (disable) {
                return null;
            }
            return entity.getScheduler()
                    .runAtFixedRate(
                            getPlugin(), scheduledTask -> runnable.run(), null, Math.max(1, delay), Math.max(1, period));
        }

        public @NotNull ScheduledTask runTaskTimer(
                Plugin plugin, Runnable runnable, Location location, long delay, long period) {
            if (disable) {
                return null;
            }
            return Bukkit.getRegionScheduler()
                    .runAtFixedRate(
                            getPlugin(), location, scheduledTask -> runnable.run(), Math.max(1, delay), Math.max(1, period));
        }

        public ScheduledTask runTaskTimer(
                Plugin plugin, Consumer<ScheduledTask> runnable, Entity entity, long delay, long period) {
            if (disable) {
                return null;
            }
            return entity.getScheduler()
                    .runAtFixedRate(getPlugin(), runnable, null, Math.max(1, delay), Math.max(1, period));
        }

        public ScheduledTask runTaskTimer(
                Plugin plugin, Consumer<ScheduledTask> runnable, Location location, long delay, long period) {
            if (disable) {
                return null;
            }
            return Bukkit.getRegionScheduler()
                    .runAtFixedRate(getPlugin(), location, runnable, Math.max(1, delay), Math.max(1, period));
        }


        public ScheduledTask runTaskTimerAsynchronously(
                Plugin plugin, Runnable runnable, long delay, long period) {
            if (disable) {
                return null;
            }
            return Bukkit.getAsyncScheduler()
                    .runAtFixedRate(
                            getPlugin(),
                            scheduledTask -> runnable.run(),
                            Math.max(1, delay) * 50L,
                            Math.max(1, period) * 50L,
                            TimeUnit.MILLISECONDS);
        }


        public ScheduledTask runTaskTimerGlobally(
                Plugin plugin, Runnable runnable, long delay, long period) {
            if (disable) {
                return null;
            }
            return Bukkit.getGlobalRegionScheduler()
                    .runAtFixedRate(
                            getPlugin(),
                            scheduledTask -> runnable.run(),
                            delay, period
                    );
        }

        public @NotNull ScheduledTask runTaskLaterAsync(Plugin plugin, Runnable runnable, long delay) {
            if (disable) {
                return null;
            }
            return Bukkit.getAsyncScheduler()
                    .runDelayed(
                            getPlugin(), scheduledTask -> runnable.run(), Math.max(1, delay) * 50L, TimeUnit.MILLISECONDS);
        }

        public @Nullable ScheduledTask scheduleSyncDelayedTask(
                Plugin plugin, Entity entity, Runnable runnable, long delay) {
            if (disable) {
                return null;
            }
            return entity.getScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), null, Math.max(1, delay));
        }

        public @Nullable ScheduledTask scheduleSyncDelayedTask(
                Plugin plugin, Location location, Runnable runnable, long delay) {
            if (disable) {
                return null;
            }
            return Bukkit.getRegionScheduler()
                    .runDelayed(getPlugin(), location, scheduledTask -> runnable.run(), Math.max(1, delay));
        }

        public @Nullable ScheduledTask runTask(Plugin plugin, Entity entity, Runnable runnable) {
            if (disable) {
                return null;
            }
            return entity.getScheduler().run(getPlugin(), scheduledTask -> runnable.run(), null);
        }

        public @Nullable ScheduledTask runTask(Plugin plugin, SchedulerContext context, Runnable runnable) {
            if (disable) {
                return null;
            }
            if (context instanceof SchedulerContext.EntitySchedulerContext entitySchedulerContext) {
                return runTask(getPlugin(), entitySchedulerContext.getEntity(), runnable);
            }
            if (context instanceof SchedulerContext.LocationSchedulerContext locationSchedulerContext) {
                return runTask(getPlugin(), locationSchedulerContext.getWorld(), locationSchedulerContext.getChunkX(), locationSchedulerContext.getChunkZ(), runnable);
            }
            throw new RuntimeException("No enough context.");
        }

        public @Nullable ScheduledTask runTaskGlobally(Plugin plugin, Runnable runnable) {
            if (disable) {
                return null;
            }
            return Bukkit.getGlobalRegionScheduler().run(getPlugin(), scheduledTask -> runnable.run());
        }

        public @Nullable ScheduledTask runTask(Plugin plugin, World world, int chunkX, int chunkZ, Runnable runnable) {
            if (disable) {
                return null;
            }
            return Bukkit.getRegionScheduler().run(getPlugin(), world, chunkX, chunkZ, scheduledTask -> runnable.run());
        }

        public @Nullable ScheduledTask runTask(Plugin plugin, Location location, Runnable runnable) {
            if (disable) {
                return null;
            }
            return Bukkit.getRegionScheduler().run(getPlugin(), location, scheduledTask -> runnable.run());
        }

        public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> callable, Pair<Entity, Location> right) {
            if (disable) {
                return null;
            }
            CompletableFuture<T> tCompletableFuture = new CompletableFuture<>();
            if (right.left() == null) {
                if (right.right() == null) {
                    throw new RuntimeException("Context not enough!");
                }
                runTask(getPlugin(), right.right(), () -> {
                    try {
                        tCompletableFuture.complete(callable.call());
                    } catch (Exception e) {
                        tCompletableFuture.completeExceptionally(e);
                    }
                });
            } else {
                runTask(getPlugin(), right.left(), () -> {
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
            if (disable) {
                return null;
            }
            return entity.getScheduler().runDelayed(getPlugin(), scheduledTask -> runnable.run(), null, Math.max(1, delay));
        }

        public @Nullable ScheduledTask runTaskLaterGlobally(Plugin plugin, Runnable runnable, long delay) {
            if (disable) {
                return null;
            }
            return Bukkit.getGlobalRegionScheduler().runDelayed(getPlugin(), task -> runnable.run(), delay);
        }


        public @Nullable ScheduledTask runTaskLater(Plugin plugin, Location location, Runnable runnable, long delay) {
            if (disable) {
                return null;
            }
            return Bukkit.getRegionScheduler()
                    .runDelayed(getPlugin(), location, scheduledTask -> runnable.run(), Math.max(1, delay));
        }

        public ScheduledTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {

            if (disable) {
                return null;
            }
            return Bukkit.getAsyncScheduler().runNow(getPlugin(), scheduledTask -> runnable.run());
        }

        public ScheduledTask runTaskAsynchronously(Plugin plugin, Consumer<ScheduledTask> runnable) {
            if (disable) {
                return null;
            }
            return Bukkit.getAsyncScheduler().runNow(getPlugin(), runnable);
        }

        public @NotNull ScheduledTask runTaskTimerAsynchronously(
                Plugin plugin, Consumer<ScheduledTask> consumer, long delay, long period) {
            if (disable) {
                return null;
            }
            return Bukkit.getAsyncScheduler()
                    .runAtFixedRate(
                            getPlugin(),
                            consumer,
                            Math.max(1, delay) * 50L,
                            Math.max(1, period) * 50L,
                            TimeUnit.MILLISECONDS);
        }
    }


    private static Plugin getPlugin() {
        return JavaPlugin.getPlugin(FoliaAdapter.class);
    }

    public static void setPlugin(Plugin plugin) {

    }

    public static ScheduledTask runSync(Runnable runnable, Entity entity, long delay) {
        if (disable) {
            return null;
        }
        return getScheduler().runTaskLater(getPlugin(), entity, runnable, delay);
    }

    public static ScheduledTask runGlobally(Runnable runnable) {
        if (disable) {
            return null;
        }
        return getScheduler().runTaskGlobally(getPlugin(), runnable);
    }

    public static ScheduledTask runGlobally(Runnable runnable, int delay) {
        if (disable) {
            return null;
        }
        return getScheduler().runTaskLaterGlobally(getPlugin(), runnable, delay);
    }

    public static ScheduledTask runSync(Runnable runnable, Location location, long delay) {
        if (disable) {
            return null;
        }
        return getScheduler().runTaskLater(getPlugin(), location, runnable, delay);
    }

    public static ScheduledTask runSync(Runnable runnable, Entity entity) {
        if (disable) {
            return null;
        }
        return runSync(runnable, entity, 1);
    }

    public static ScheduledTask runSync(Runnable runnable, Location location) {
        if (disable) {
            return null;
        }
        return runSync(runnable, location, 1);
    }

    public static void runAtFirstTick(Plugin plugin, Runnable runnable) {
        if (disable) {
            return;
        }
        Bukkit.getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            public void on(RegionizedServerInitEvent event) {
                                runnable.run();
                            }
                        },
                        getPlugin());
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
