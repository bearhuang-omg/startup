package com.bear.startup

import android.util.Log
import java.util.concurrent.atomic.AtomicInteger

abstract class Task : Runnable {

    private val TAG = "${TaskDirector.TAG}_Task"
    private val before = HashSet<(String) -> Unit>()
    private val after = HashSet<(String) -> Unit>()

    companion object {
        val serializeNum = AtomicInteger(0)
    }

    open fun getName(): String {
        return "Task${serializeNum.getAndIncrement()}"
    }

    open fun getDepends(): Array<String> {
        return arrayOf()
    }

    open fun getPriority(): Priority {
        return Priority.Calculate
    }

    final override fun run() {
        Log.i(TAG,"start ${getName()}")
        before.forEach {
            it(getName())
        }
        execute()
        after.forEach {
            it(getName())
        }
        Log.i(TAG,"end ${getName()}")
    }

    fun addBefore(block: (String) -> Unit) {
        before.add(block)
    }

    fun removeBefore(block: (String) -> Unit) {
        before.remove(block)
    }

    fun addAfter(block: (String) -> Unit) {
        after.add(block)
    }

    fun removeAfter(block: (String) -> Unit) {
        after.remove(block)
    }

    abstract fun execute()
}