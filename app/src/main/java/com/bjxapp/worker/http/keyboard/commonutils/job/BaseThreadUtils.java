package com.bjxapp.worker.http.keyboard.commonutils.job;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by kanyingkuang on 2018/1/16.
 */
public class BaseThreadUtils {
    public static final long CONSTANT_ANR_TIMEOUT = 500L;
    private static final int THREAD_KEEPALIVETIME = 60;

    private final PriorityBlockingQueue<Runnable> mBlockingQueue;
    private final ThreadPoolExecutor mExecutor;
    private String mThreadName;

    protected BaseThreadUtils(String threadName, int corePoolSize,
                              int maximumPoolSize) {
        mThreadName = threadName;
        mBlockingQueue = new PriorityBlockingQueue<>();
        mExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                THREAD_KEEPALIVETIME, TimeUnit.SECONDS, mBlockingQueue, new ThreadFactory() {
            int count = 0;

            @Override
            public synchronized Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r, mThreadName + (count++));
                return thread;
            }
        }) {

            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);

                if (r instanceof RunnableOrCallableProxy) {
                    RunnableOrCallableProxy proxy = (RunnableOrCallableProxy) r;
                    setThreadPriorityByJobPriority(proxy.getPriority());
                }

                if (r instanceof FutureTaskProxy) {
                    FutureTaskProxy proxy = (FutureTaskProxy) r;
                    setThreadPriorityByJobPriority(proxy.getPriority());
                }
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                tryRestoreThreadDefaultPriority();
            }

            @Override
            protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
                return new FutureTaskProxy<T>(callable);
            }

            @Override
            protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
                return new FutureTaskProxy<T>(runnable, value);
            }

            @Override
            public void execute(Runnable command) {
                boolean isLegalCommand = false;
                if (command instanceof RunnableOrCallableProxy || command instanceof FutureTaskProxy) {
                    isLegalCommand = true;
                }
                if (!isLegalCommand) {
                    command = new RunnableOrCallableProxy(command);
                }
                super.execute(command);
            }
        };
        mExecutor.allowCoreThreadTimeOut(true);
    }

    private void setThreadPriorityByJobPriority(@JobPriority.JobPriorityAnnotation int jobPriority) {
        switch (jobPriority) {
            case JobPriority.JOB_PRIORITY_IMMEDIATE:
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE * 5);
                break;
            case JobPriority.JOB_PRIORITY_HIGH:
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE * 2);
                break;
            case JobPriority.JOB_PRIORITY_LOW:
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_LESS_FAVORABLE);
                break;
            default:
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                break;
        }
    }

    private void tryRestoreThreadDefaultPriority() {
        if (Process.getThreadPriority(Process.myTid()) != Process.THREAD_PRIORITY_BACKGROUND) {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        }
    }

    public void execute(Runnable command) {
        execute(command, JobPriority.JOB_PRIORITY_NORMAL);
    }

    public Future<?> submit(Runnable command) {
        return submit(command, JobPriority.JOB_PRIORITY_NORMAL);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return submit(callable, JobPriority.JOB_PRIORITY_NORMAL);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return submit(task, result, JobPriority.JOB_PRIORITY_NORMAL);
    }

    public void executeImmediately(Runnable command) {
        execute(command, JobPriority.JOB_PRIORITY_IMMEDIATE);
    }

    public <T> Future<T> executeImmediately(Callable<T> callable) {
        return submit(callable, JobPriority.JOB_PRIORITY_IMMEDIATE);
    }

    public <T> Future<T> executeImmediately(Runnable command, T result) {
        return submit(command, result, JobPriority.JOB_PRIORITY_IMMEDIATE);
    }

    public <T> T runWithTimeOut(Callable<T> callable, T defaultValue) {
        Future<T> future = executeImmediately(callable);
        try {
            return future.get(CONSTANT_ANR_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return defaultValue;
    }

    public <T> T runAlwaysUtilComplete(Callable<T> callable, T defaultValue) {
        Future<T> future = executeImmediately(callable);
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public <T> T runWithTimeOut(Callable<T> callable) {
        return runWithTimeOut(callable, null);
    }

    public void execute(Runnable command, @JobPriority.JobPriorityAnnotation int priority) {
        RunnableOrCallableProxy runnableOrCallableProxy = new RunnableOrCallableProxy(command, priority);
        mExecutor.execute(runnableOrCallableProxy);
    }

    public <T> Future<T> submit(Callable<T> callable, @JobPriority.JobPriorityAnnotation int priority) {
        RunnableOrCallableProxy<T> runnableOrCallableProxy = new RunnableOrCallableProxy<T>(callable, priority);
        return mExecutor.submit((Callable<T>) runnableOrCallableProxy);
    }

    public <T> Future<T> submit(Runnable command, T result, @JobPriority.JobPriorityAnnotation int priority) {
        RunnableOrCallableProxy<T> runnableOrCallableProxy = new RunnableOrCallableProxy<T>(command, priority);
        return mExecutor.submit(runnableOrCallableProxy, result);
    }

    public Future<?> submit(Runnable command, @JobPriority.JobPriorityAnnotation int priority) {
        RunnableOrCallableProxy runnableOrCallableProxy = new RunnableOrCallableProxy(command, priority);
        return mExecutor.submit((Runnable) runnableOrCallableProxy);
    }

    public void executeDelay(final Runnable command, long delay, TimeUnit unit) {
        if (delay > 0) {
            if (unit == null) {
                unit = TimeUnit.MILLISECONDS;
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    execute(command, JobPriority.JOB_PRIORITY_NORMAL);
                }
            }, unit.toMillis(delay));
        } else {
            execute(command, JobPriority.JOB_PRIORITY_NORMAL);
        }
    }

    public void executeDelay(final Runnable command, long delay) {
        executeDelay(command, delay, TimeUnit.MILLISECONDS);
    }

    public boolean cancel(Future<?> future) {
        if (future == null || future.isCancelled()) {
            return true;
        }
        return future.cancel(true);
    }

    public ThreadPoolExecutor getExecutor() {
        return mExecutor;
    }
}
