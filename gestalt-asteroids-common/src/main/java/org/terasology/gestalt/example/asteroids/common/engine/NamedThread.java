package org.terasology.gestalt.example.asteroids.common.engine;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Todo: Rather than singleton, add a dependency injection framework?
 */
public class NamedThread {
    private final Thread thread;
    private final BlockingQueue<Runnable> pendingRunnables = new LinkedBlockingQueue<>();

    public NamedThread(String name, Thread thread) {
        this.thread = thread;
        thread.setName(name);
    }

    /**
     * @return Whether the currentThread is the named thrad.
     */
    public boolean isCurrentThread() {
        return Thread.currentThread() == thread;
    }

    /**
     * Runs a process on the game thread, not waiting for it to run.
     * <br><br>
     * If the current thread is the game thread, then the process runs immediately
     *
     * @param process
     */
    public void async(Runnable process) {
        if (isCurrentThread()) {
            process.run();
        } else {
            pendingRunnables.add(process);
        }
    }

    /**
     * Runs a process on the game thread, waiting for it to run (the current thread is blocked).
     * <br><br>
     * If the current thread is the game thread, then the process runs immediately
     *
     * @param process
     */
    public void sync(Runnable process) throws InterruptedException {
        if (isCurrentThread()) {
            process.run();
        } else {
            BlockingProcess blockingProcess = new BlockingProcess(process);
            pendingRunnables.add(blockingProcess);
            blockingProcess.waitForCompletion();
        }
    }

    public <T> Future<T> asyncFuture(Supplier<T> supplier) {
        if (isCurrentThread()) {
            return Futures.immediateFuture(supplier.get());
        } else {
            AsynchFuture<T> result = new AsynchFuture<>();
            pendingRunnables.add(() -> result.setValue(supplier.get()));
            return result;
        }
    }

    /**
     * Runs all pending processes submitted from other threads
     * @throws IllegalStateException If called from the wrong thread
     */
    public void processWaitingProcesses() {
        if (isCurrentThread()) {
            List<Runnable> processes = Lists.newArrayList();
            pendingRunnables.drainTo(processes);
            processes.forEach(Runnable::run);
        } else {
            throw new IllegalStateException("Cannot process waiting processors off thread");
        }
    }

    /**
     * Removes all pending processess without running them
     */
    public void clearWaitingProcesses() {
        if (isCurrentThread()) {
            pendingRunnables.clear();
        } else {
            throw new IllegalStateException("Cannot process waiting processors off thread");
        }
    }

    /**
     * A process decorated allowing a thread to block until the process has been run.
     */
    private static class BlockingProcess implements Runnable {
        private final Runnable internalProcess;
        private final CountDownLatch completionTrack = new CountDownLatch(1);

        BlockingProcess(Runnable runnable) {
            this.internalProcess = runnable;
        }

        @Override
        public void run() {
            internalProcess.run();
            completionTrack.countDown();
        }

        public void waitForCompletion() throws InterruptedException {
            completionTrack.await();
        }
    }

    /**
     * A simple future for providing a value
     *
     * @param <T>
     */
    private static class AsynchFuture<T> implements Future<T> {
        private final CountDownLatch completionTrack = new CountDownLatch(1);
        private volatile T value;

        synchronized void setValue(T value) {
            this.value = value;
            completionTrack.countDown();
        }

        @Override
        public boolean cancel(boolean b) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return completionTrack.getCount() == 0;
        }

        @Override
        public T get() throws InterruptedException {
            completionTrack.await();
            return value;
        }

        @Override
        public T get(long l, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
            completionTrack.await(l, timeUnit);
            if (isDone()) {
                return value;
            }
            throw new TimeoutException("Timed out awaiting future completion");
        }
    }

}
