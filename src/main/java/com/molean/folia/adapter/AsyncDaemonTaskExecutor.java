package com.molean.folia.adapter;

import ca.spottedleaf.moonrise.common.util.TickThread;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class AsyncDaemonTaskExecutor implements Listener {
    private final Map<Integer, Deque<MyScheduledTask>> taskMap = new HashMap<>();

    @EventHandler
    public void on(ServerTickEndEvent event) {
        int id = TickThread.getCurrentTickThread().id;
        Deque<MyScheduledTask> runnables = taskMap.get(id);
        Folia.getScheduler().runTaskAsynchronously(FoliaAdapter.getPlugin(), () -> {
            if (runnables != null) {
                while (!runnables.isEmpty()) {
                    MyScheduledTask myScheduledTask = runnables.removeFirst();
                    myScheduledTask.setExecutionState(ScheduledTask.ExecutionState.RUNNING);
                    myScheduledTask.getTask().run();
                    myScheduledTask.setExecutionState(ScheduledTask.ExecutionState.FINISHED);
                }
            }
        });
    }


    public ScheduledTask registerTask(Runnable runnable, Location location) {
        MyScheduledTask myScheduledTask = new MyScheduledTask(runnable, ScheduledTask.ExecutionState.IDLE, ScheduledTask.CancelledState.RUNNING);
        if (!FoliaUtils.isTickThreadFor(location)) {
            Folia.getScheduler().runTask(FoliaAdapter.getPlugin(), SchedulerContext.of(location), () -> {
                TickThread currentTickThread = TickThread.getCurrentTickThread();
                if (currentTickThread == null) {
                    throw new RuntimeException("???");
                } else {
                    taskMap.computeIfAbsent(currentTickThread.id, integer -> new LinkedList<>()).add(myScheduledTask);
                }
            });
        } else {
            TickThread currentTickThread = TickThread.getCurrentTickThread();
            taskMap.computeIfAbsent(currentTickThread.id, integer -> new LinkedList<>()).add(myScheduledTask);
        }
        return myScheduledTask;
    }
}
