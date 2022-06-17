package com.example.startup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.bear.startup.IDirectorListener
import com.bear.startup.Task
import com.bear.startup.TaskDirector

class MainActivity : AppCompatActivity() {

    val TAG = "MMMMM"

    val taskBtn:Button by lazy {
        findViewById(R.id.task_btn)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val task1 = object:Task() {
            override fun execute() {
                Thread.sleep(1000)
            }

            override fun getName(): String {
                return "tttt1"
            }

        }

        val task2 = object:Task() {
            override fun execute() {
                Thread.sleep(1000)
            }

            override fun getName(): String {
                return "tttt2"
            }

            override fun getDepends(): Set<String> {
                return setOf("tttt1")
            }

        }

        val task3 = object:Task() {
            override fun execute() {
                Thread.sleep(1000)
            }

            override fun getName(): String {
                return "tttt3"
            }

            override fun getDepends(): Set<String> {
                return setOf("tttt2","tttt1")
            }

        }


        taskBtn.setOnClickListener {
            val director = TaskDirector()
            director.registerListener(object : IDirectorListener{
                override fun onStart() {
                    Log.i(TAG,"director start")
                }

                override fun onFinished(time: Long) {
                    Log.i(TAG,"director finished with time ${time}")
                }

                override fun onError(code: Int, msg: String) {
                    Log.i(TAG,"errorCode:${code},msg:${msg}")
                }
            })
            director.apply {
                addTask(task1)
                addTask(task2)
                addTask(task3)
            }
            director.start()
        }
    }
}