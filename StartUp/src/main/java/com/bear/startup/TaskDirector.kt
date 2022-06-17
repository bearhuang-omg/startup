package com.bear.startup

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.util.concurrent.ConcurrentLinkedDeque

class TaskDirector {
    companion object {
        val TAG = "TaskExecutor"
    }

    private val taskMap = HashMap<String, TaskNode>()
    private val ROOTNODE = "ROOTNODE"
    private val rootNode by lazy {
        val task = object : Task() {

            override fun execute() {

            }

            override fun getName(): String {
                return ROOTNODE
            }
        }
        task.addAfter(after)
        TaskNode(task)
    }
    private val handlerThread = HandlerThread(TAG)
    private val handler by lazy {
        handlerThread.start()
        Handler(handlerThread.looper)
    }

    private var finishedTasks = 0
    private var taskSum = 0
    private var startTime: Long = 0
    private var endTime: Long = 0
    private val beforeList = HashSet<() -> Unit>()
    private val afterList = HashSet<() -> Unit>()

    private val after = { name: String ->
        runAfter(name)
    }

    private fun runAfter(name: String) {
        handler.post {
            finishedTasks++
            if (finishedTasks == taskSum) {
                endTime = System.currentTimeMillis()
                Log.i(TAG, "finished All task , time:${endTime - startTime}")
                afterList.forEach { after ->
                    after()
                }
            }
            if (taskMap.containsKey(name) && taskMap[name]!!.next.isNotEmpty()) {
                taskMap[name]!!.next.forEach { taskNode ->
                    taskNode.start()
                }
                taskMap.remove(name)
            }
        }
    }

    private fun runBefore() {
        handler.post {
            beforeList.forEach { before ->
                before()
            }
        }
    }

    fun addTask(task: Task) {
        if (taskMap.containsKey(task.getName())) {
            Log.i(TAG, "already has the task ${task.getName()}")
            return
        }
        task.addAfter(after)
        taskMap[task.getName()] = TaskNode(task)
    }

    /**
     * 构造任务图
     */
    private fun constructGrapic(): Boolean {
        taskMap.forEach { entry ->
            val depends = entry.value.task.getDepends()
            if (depends.isEmpty()) { //不依赖，直接加入到根结点
                rootNode.next.add(entry.value)
            } else { //依赖，则加入到对应节点的子节点
                depends.forEach { depend ->
                    if (!taskMap.containsKey(depend)) {
                        Log.i(TAG, "can not find the task ${depend}")
                        return false
                    }
                    taskMap[depend]!!.next.add(entry.value)
                }
            }
        }
        return true
    }

    /**
     * 检查任务图中是否有环
     */
    private fun checkCycle(): Boolean {
        val tempQueue = ConcurrentLinkedDeque<TaskNode>()
        tempQueue.offer(rootNode)
        val tempMap = HashMap<String, TaskNode>()
        taskMap[ROOTNODE] = rootNode
        taskSum = taskMap.size
        taskMap.forEach {
            tempMap[it.key] = it.value
        }
        while (tempQueue.isNotEmpty()) {
            val node = tempQueue.poll()
            if (!tempMap.containsKey(node.key)) {
                Log.i(TAG, "task has cycle ${node.key}")
                return false
            }
            tempMap.remove(node.key)
            if (node.next.isNotEmpty()) {
                node.next.forEach {
                    tempQueue.offer(it)
                }
            }
        }
        if (tempMap.isNotEmpty()) {
            Log.i(TAG, "seperate from Root task,tasks:${tempMap.keys}")
            return false
        }
        return true
    }

    fun addBefore(block: () -> Unit) {
        beforeList.add(block)
    }

    fun addAfter(block: () -> Unit) {
        afterList.add(block)
    }

    fun start(): Boolean {
        if (!constructGrapic()) {
            Log.i(TAG, "constructGrapic error!")
            return false
        }
        if (!checkCycle()) {
            Log.i(TAG, "check cycle error!")
            return false
        }
        startTime = System.currentTimeMillis()
        runBefore()
        handler.post {
            rootNode.start()
        }
        return true
    }

    fun clear() {
        startTime = 0
        endTime = 0
        taskMap.clear()
        rootNode.next.clear()
        beforeList.clear()
        afterList.clear()
    }
}