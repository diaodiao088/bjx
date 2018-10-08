package com.bjxapp.worker.http.keyboard.commonutils.job;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by general on 09/11/2017.
 */

public class FutureTaskProxy<V> extends FutureTask<V> implements Comparable<Object> {

    private int mSequence = 0;
    private @JobPriority.JobPriorityAnnotation int mPriority = JobPriority.JOB_PRIORITY_NORMAL;

    public FutureTaskProxy(@NonNull Callable<V> callable) {
        super(callable);
        if (callable instanceof RunnableOrCallableProxy) {
            RunnableOrCallableProxy proxy = (RunnableOrCallableProxy) callable;
            mSequence = proxy.getSequence();
            mPriority = proxy.getPriority();
        }
    }

    public FutureTaskProxy(@NonNull Runnable runnable, V result) {
        super(runnable, result);

        if (runnable instanceof RunnableOrCallableProxy) {
            RunnableOrCallableProxy proxy = (RunnableOrCallableProxy) runnable;
            mSequence = proxy.getSequence();
            mPriority = proxy.getPriority();
        }
    }

    @Override
    public void run() {
        super.run();
    }

    int getSequence() {
        return mSequence;
    }

    public @JobPriority.JobPriorityAnnotation int getPriority() {
        return mPriority;
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
}
