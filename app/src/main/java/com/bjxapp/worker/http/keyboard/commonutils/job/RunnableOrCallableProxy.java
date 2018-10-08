package com.bjxapp.worker.http.keyboard.commonutils.job;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by general on 09/11/2017.
 */

public class RunnableOrCallableProxy<T> implements Runnable, Callable<T>, Comparable<Object> {

    private Runnable mRealRunnable;

    private Callable<T> mRealCallable;

    private static final AtomicInteger mSequenceGenerator = new AtomicInteger(0);

    private int mSequence = 0;

    private @JobPriority.JobPriorityAnnotation int mPriority = JobPriority.JOB_PRIORITY_NORMAL;

    public RunnableOrCallableProxy(Runnable runnable) {
        this(runnable, JobPriority.JOB_PRIORITY_NORMAL);
    }

    public RunnableOrCallableProxy(Runnable runnable, @JobPriority.JobPriorityAnnotation int priority) {
        mRealRunnable = runnable;
        mPriority = priority;
        mSequence = generateSequenceId();
    }

    public RunnableOrCallableProxy(Callable<T> callable) {
        this(callable, JobPriority.JOB_PRIORITY_NORMAL);
    }

    public RunnableOrCallableProxy(Callable<T> callable, @JobPriority.JobPriorityAnnotation int priority) {
        mRealCallable = callable;
        mPriority = priority;
        mSequence = generateSequenceId();
    }


    @Override
    public void run() {
        if (mRealRunnable != null) {
            mRealRunnable.run();
        }
    }

    @Override
    public T call() throws Exception {
        if (mRealCallable != null) {
            return mRealCallable.call();
        }
        return null;
    }

    public int getSequence() {
        return mSequence;
    }

    public @JobPriority.JobPriorityAnnotation int getPriority() {
        return mPriority;
    }

    public void setPriority(@JobPriority.JobPriorityAnnotation int mPriority) {
        this.mPriority = mPriority;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof FutureTaskProxy) {
            FutureTaskProxy other = (FutureTaskProxy) o;
            int left = this.getPriority();
            int right = other.getPriority();
            return left == right ?
                    this.getSequence() - other.getSequence() :
                    right - left;
        } else if (o instanceof  RunnableOrCallableProxy) {
            RunnableOrCallableProxy other = (RunnableOrCallableProxy) o;
            int left = this.getPriority();
            int right = other.getPriority();
            return left == right ?
                    this.getSequence() - other.getSequence() :
                    right - left;
        }
        return 0;
    }

    private int generateSequenceId() {
        if (mSequenceGenerator.get() == Integer.MAX_VALUE) {
            mSequenceGenerator.set(0);
        }
        return mSequenceGenerator.addAndGet(1);
    }
}
