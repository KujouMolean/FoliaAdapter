package com.molean.folia.adapter;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class MyScheduledTask implements ScheduledTask {
    private Runnable task;
    private ExecutionState executionState;
    private CancelledState cancelledState;

    @Override
    public @NotNull Plugin getOwningPlugin() {
        return FoliaAdapter.getPlugin();
    }

    @Override
    public boolean isRepeatingTask() {
        return false;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public void setExecutionState(ExecutionState executionState) {
        this.executionState = executionState;
    }

    public CancelledState getCancelledState() {
        return cancelledState;
    }

    public void setCancelledState(CancelledState cancelledState) {
        this.cancelledState = cancelledState;
    }

    public MyScheduledTask(Runnable task, ExecutionState executionState, CancelledState cancelledState) {
        this.task = task;
        this.executionState = executionState;
        this.cancelledState = cancelledState;
    }

    @NotNull
    @Override
    public CancelledState cancel() {
        return cancelledState;
    }

    @NotNull
    @Override
    public ExecutionState getExecutionState() {
        return executionState;
    }
}
