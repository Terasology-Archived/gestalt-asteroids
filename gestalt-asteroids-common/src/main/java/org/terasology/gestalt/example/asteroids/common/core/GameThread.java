package org.terasology.gestalt.example.asteroids.common.core;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.Futures;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Todo: Rather than singleton, add a dependency injection framework?
 */
public class GameThread {
    private static volatile Thread gameThread = Thread.currentThread();
    private static BlockingQueue<Runnable> pendingRunnables = new LinkedBlockingQueue<>();

    private GameThread() {
    }

    /**
     * @return Whether the currentThread is the gameThread.
     */
    public static boolean isCurrentThread() {
        return Thread.currentThread() == gameThread;
    }

    /**
     * Runs a process on the game thread, not waiting for it to run.
     * <br><br>
     * If the current thread is the game thread, then the process runs immediately
     *
     * @param process
     */
    public static void async(Runnable process) {
        if (Thread.currentThread() != gameThread) {
            pendingRunnables.add(process);
        } else {
            process.run();
        }
    }

    /**
     * Runs a process on the game thread, waiting for it to run (the current thread is blocked).
     * <br><br>
     * If the current thread is the game thread, then the process runs immediately
     *
     * @param process
     */
    public static void sync(Runnable process) throws InterruptedException {
        if (Thread.currentThread() != gameThread) {
            BlockingProcess blockingProcess = new BlockingProcess(process);
            pendingRunnables.add(blockingProcess);
            blockingProcess.waitForCompletion();
        } else {
            process.run();
        }
    }

    public static <T> Future<T> asyncFuture(Supplier<T> supplier) {
        if (Thread.currentThread() != gameThread) {
            AsynchFuture<T> result = new AsynchFuture<>();
            pendingRunnables.add(() -> result.setValue(supplier.get()));
            return result;
        } else {
            return Futures.immediateFuture(supplier.get());
        }
    }

    /**
     * Runs all pending processes submitted from other threads
     */
    public static void processWaitingProcesses() {
        if (Thread.currentThread() == gameThread) {
            List<Runnable> processes = Lists.newArrayList();
            pendingRunnables.drainTo(processes);
            processes.forEach(Runnable::run);
        }
    }

    /**
     * Removes all pending processess without running them
     */
    public static void clearWaitingProcesses() {
        if (gameThread == Thread.currentThread()) {
            pendingRunnables.clear();
        }
    }

    /**
     * Sets the game thread. This can only be done once.
     */
    public static void setToCurrentThread() {
        if (gameThread == null) {
            gameThread = Thread.currentThread();
        }
    }

    /**
     * Sets the game thread to null. Should called after a test that calls engine.initialise() is finished.
     */
    public static void reset() {
        gameThread = null;
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
