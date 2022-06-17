package com.bear.startup

interface IDirectorListener {
    fun onStart()
    fun onFinished(time: Long)
    fun onError(code: Int, msg: String)
}