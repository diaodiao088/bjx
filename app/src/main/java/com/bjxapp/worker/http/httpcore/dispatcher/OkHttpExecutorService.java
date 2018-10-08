package com.bjxapp.worker.http.httpcore.dispatcher;

import android.support.annotation.NonNull;


import com.bjxapp.worker.http.httpcore.tag.TagEntity;
import com.bjxapp.worker.http.keyboard.commonutils.job.RunnableOrCallableProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Request;

/**
 * Created by general on 10/11/2017.
 *
 * @author renwenjie
 */

public class OkHttpExecutorService extends AbstractExecutorService {

    private ThreadPoolExecutor mRealThreadPoolExecutor;

    public OkHttpExecutorService(ThreadPoolExecutor executor) {
        mRealThreadPoolExecutor = executor;
    }

    @Override
    public void shutdown() {
        mRealThreadPoolExecutor.shutdown();
    }

    @NonNull
    @Override
    public List<Runnable> shutdownNow() {
        return mRealThreadPoolExecutor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return mRealThreadPoolExecutor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return mRealThreadPoolExecutor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        return mRealThreadPoolExecutor.awaitTermination(timeout, unit);
    }

    @Override
    public void execute(@NonNull Runnable command) {
        boolean executed = false;
        RunnableOrCallableProxy proxy = new RunnableOrCallableProxy(command);
        try {
            Class asyncCallClazz = Class.forName("okhttp3.RealCall$AsyncCall");
            if (Runnable.class.isAssignableFrom(asyncCallClazz)) {
                Method method = asyncCallClazz.getDeclaredMethod("request");
                method.setAccessible(true);
                Object object = method.invoke(command);
                if (object instanceof Request) {
                    Request request = (Request) object;
                    Object tag = request.tag();
                    if (tag instanceof TagEntity) {
                        TagEntity tagEntity = (TagEntity) tag;
                        executed = true;
                        proxy.setPriority(tagEntity.requestPriority);
                        mRealThreadPoolExecutor.execute(proxy);
                    }
/*                    KLog.w("NNN", "priority:" + proxy.getPriority() + ";url=" + request.url());*/
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if (!executed) {
                mRealThreadPoolExecutor.execute(proxy);
            }
        }
    }
}
