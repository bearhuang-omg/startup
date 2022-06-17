package com.bear.startup

import android.os.Build
import android.os.Handler
import android.os.Looper
import java.util.concurrent.*
import java.util.concurrent.Executors.newCachedThreadPool
import java.util.concurrent.atomic.AtomicInteger

internal object TaskPool {

    private val mainHandler by lazy<Handler> {
        Handler(Looper.getMainLooper())
    }

    private val ioPool by lazy<ExecutorService> {
        newCachedThreadPool()
    }

    private val calculatePool by lazy<ThreadPoolExecutor> {
        val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        val CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4))
        val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
        val sPoolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(128)

        ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 30,
            TimeUnit.SECONDS, sPoolWorkQueue, object : ThreadFactory {
                private val mCount: AtomicInteger = AtomicInteger(1)
                override fun newThread(r: Runnable?): Thread {
                    return Thread(r, "threadPool #" + mCount.getAndIncrement())
                }
            })
    }

    private var isSet: Boolean = false

    fun submitTask(task: Task) {
        if (!isSet) {
            calculatePool.allowCoreThreadTimeOut(true)
            isSet = true
        }
        when (task.getPriority()) {
            Priority.Calculate -> calculatePool.submit(task)
            Priority.IO -> ioPool.submit(task)
            Priority.Main -> mainHandler.post(task)
            Priority.Idle -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Looper.getMainLooper().queue.addIdleHandler {
                        task.run()
                        return@addIdleHandler false
                    }
                } else {
                    ioPool.submit(task)
                }
            }
        }
    }
}