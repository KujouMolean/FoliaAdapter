package com.molean.folia.adapter;


import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FoliaBatchBlockTask {

    private int limitPerTick = 1000;

    public static FoliaBatchBlockTask create() {
        return new FoliaBatchBlockTask();
    }

    private final LinkedList<Pair<Location, Consumer<Block>>> tasks = new LinkedList<>();

    public FoliaBatchBlockTask task(Location location, Consumer<Block> consumer) {
        tasks.add(Pair.of(location, consumer));
        return this;
    }

    public FoliaBatchBlockTask limitPerTick(int limitPerTick) {
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
        Pair<Location, Consumer<Block>> locationConsumerPair;
        int count = 0;
        while (!tasks.isEmpty()) {
            locationConsumerPair = tasks.getFirst();
            Consumer<Block> consumer = locationConsumerPair.right();
            Location location = locationConsumerPair.left();
            if (count > limitPerTick || !FoliaUtils.isTickThreadFor(location)) {
                Folia.runSync(() -> execute(completableFuture), location);
                return;
            }
            tasks.removeFirst();
            consumer.accept(location.getBlock());
            count++;
        }
        completableFuture.complete(null);
    }


}
