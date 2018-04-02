package com.peixing.myapplication.utils;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 *
 * @author xuxiaohe
 * @date 2018/2/8
 */

public class PriorityThreadFactory implements ThreadFactory {

    private final int mThreadPriority;

    public PriorityThreadFactory(int threadPriority) {
        this.mThreadPriority = threadPriority;
    }

    @Override
    public Thread newThread(@NonNull final Runnable runnable) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(mThreadPriority);
                runnable.run();
            }

        };
        return new Thread(wrapperRunnable);
    }
}
