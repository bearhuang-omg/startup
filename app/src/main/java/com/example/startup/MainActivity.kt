package com.example.startup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.bear.startup.Task
import com.bear.startup.TaskDirector

class MainActivity : AppCompatActivity() {

    val taskBtn:Button by lazy {
        findViewById(R.id.task_btn)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val director = TaskDirector()

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

            override fun getDepends(): Array<String> {
                return arrayOf("tttt1")
            }

        }

        val task3 = object:Task() {
            override fun execute() {
                Thread.sleep(1000)
            }

            override fun getName(): String {
                return "tttt3"
            }

            override fun getDepends(): Array<String> {
                return arrayOf("tttt2")
            }

        }


        taskBtn.setOnClickListener {
            director.apply {
                addTask(task1)
                addTask(task2)
                addTask(task3)
            }
            director.start()
        }
    }
}