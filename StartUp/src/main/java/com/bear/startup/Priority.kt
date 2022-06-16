package com.bear.startup

sealed class Priority {
    object IO : Priority() //io线程
    object Calculate : Priority() //计算线程
    object Idle : Priority() //空闲时候执行
    object Main : Priority() //主线程执行
}



