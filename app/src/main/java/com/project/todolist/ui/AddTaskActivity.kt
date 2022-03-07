package com.project.todolist.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.project.todolist.databinding.ActivityAddTaskBinding
import com.project.todolist.datasource.TaskDataSource
import com.project.todolist.extensions.format
import com.project.todolist.extensions.text
import com.project.todolist.model.Task
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.tilTitle.text = it.title
                binding.tilDate.text = it.date
                binding.tilHour.text = it.hour
                binding.tilDescription.text = it.description
            }
        }
        insertListeners()
    }

    private fun insertListeners() {
        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offSet = timeZone.getOffset(Date().time) * -1
                binding.tilDate.text = Date(it + offSet).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.tilHour.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                val hh = "${timePicker.hour}".padStart(2, '0')
                val mm = "${timePicker.minute}".padStart(2, '0')
                binding.tilHour.text = "$hh:$mm"
            }
            timePicker.show(supportFragmentManager, null)
        }

        binding.btnCancel.setOnClickListener { finish() }

        binding.btnNewTask.setOnClickListener {
            val task = Task(
                id = intent.getIntExtra(TASK_ID, 0),
                title = binding.tilTitle.text.trim(),
                date = binding.tilDate.text,
                hour = binding.tilHour.text,
                description = binding.tilDescription.text.trim()
            )
            if (!(TextUtils.isEmpty(task.title) ||
                    TextUtils.isEmpty(task.description) ||
                    TextUtils.isEmpty(task.date) ||
                    TextUtils.isEmpty(task.hour))) {

                        TaskDataSource.insertTask(task)

                        val toast = Toast.makeText(this@AddTaskActivity,"Tarefa pronta!",Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER,0,0)
                        toast.show()

                        setResult(Activity.RESULT_OK)
                        finish()
            } else {
                        val toast = Toast.makeText(this@AddTaskActivity,"Atenção, não deixe em branco os campos!",Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER,0,0)
                        toast.show()
            }
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }

}