package com.bear.startup


internal class TaskNode(val task: Task) {
    val next: ArrayList<TaskNode> = ArrayList()
    val key = task.getName()
    var depensCount = task.getDepends().size

    fun start() {
        depensCount--
        if (depensCount <= 0) {
            TaskPool.submitTask(task)
        }
    }
}