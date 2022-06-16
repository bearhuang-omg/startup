package com.bear.startup


internal class TaskNode(val task: Task) {
    val next: ArrayList<TaskNode> = ArrayList()
    val key = task.getName()

    fun start() {
        TaskPool.submitTask(task)
    }
}