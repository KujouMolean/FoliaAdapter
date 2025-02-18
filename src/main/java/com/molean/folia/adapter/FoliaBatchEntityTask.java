package com.molean.folia.adapter;


import it.unimi.dsi.fastutil.Pair;
import org.bukkit.entity.Entity;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FoliaBatchEntityTask {

    private int limitPerTick = 1000;

    public static FoliaBatchEntityTask create() {
        return new FoliaBatchEntityTask();
    }

    private final LinkedList<Pair<Entity, Consumer<Entity>>> tasks = new LinkedList<>();

    public FoliaBatchEntityTask task(Entity entity, Consumer<Entity> consumer) {
        tasks.add(Pair.of(entity, consumer));
        return this;
    }

    public FoliaBatchEntityTask limitPerTick(int limitPerTick) {
        this.limitPerTick = limitPerTick;
        return this;
    }

    public CompletableFuture<Void> start() {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        completableFuture.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        execute(completableFuture);
        return completableFuture;
    }

    private void execute(CompletableFuture<Void> completableFuture) {
        Pair<Entity, Consumer<Entity>> consumerPair;
        int count = 0;
        while (!tasks.isEmpty()) {
            consumerPair = tasks.getFirst();
            Consumer<Entity> consumer = consumerPair.right();
            Entity entity = consumerPair.left();
            if (count > limitPerTick || !FoliaUtils.isTickThreadFor(entity)) {
                Folia.runSync(() -> execute(completableFuture), entity);
                return;
            }
            tasks.removeFirst();
            consumer.accept(entity);
            count++;
        }
        completableFuture.complete(null);
    }


}
