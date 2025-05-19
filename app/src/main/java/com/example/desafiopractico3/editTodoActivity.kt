package com.example.desafiopractico3

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import kotlinx.coroutines.launch


class editTodoActivity : AppCompatActivity() {

    private lateinit var tituloEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var checkboxDone: CheckBox
    private lateinit var actualizarButton: Button
    private lateinit var eliminarButton: Button
    private lateinit var todo: Todo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_todo)

        tituloEditText = findViewById(R.id.tituloEditText)
        descripcionEditText = findViewById(R.id.descripcionEditText)
        checkboxDone = findViewById(R.id.checkboxDone)
        actualizarButton = findViewById(R.id.actualizarButton)
        eliminarButton = findViewById(R.id.eliminarButton)

        todo = intent.getSerializableExtra("todo") as? Todo ?: return finish()

        tituloEditText.setText(todo.title)
        descripcionEditText.setText(todo.description)
        checkboxDone.isChecked = todo.done

        val btnRegresar = findViewById<Button>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            finish()
        }


        getUserName { userName ->
            if (userName == null || userName != todo.createdBy) {
                println(userName)
                actualizarButton.isEnabled = false
                actualizarButton.alpha = 0.5f
                actualizarButton.text = "No puedes actualizar"

                checkboxDone.isEnabled = false

                eliminarButton.isEnabled = false
                eliminarButton.alpha = 0.5f
                eliminarButton.text = "No puedes eliminar esta tarea"
            }
        }

        actualizarButton.setOnClickListener {
            val titulo = tituloEditText.text.toString().trim()
            val descripcion = descripcionEditText.text.toString().trim()
            val done = checkboxDone.isChecked

            if (titulo.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedTodo = todo.copy(
                title = titulo,
                description = descripcion,
                done = done
            )

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.updateTodo(todo.id!!, updatedTodo)
                    if (response.isSuccessful) {
                        Toast.makeText(this@editTodoActivity, "Tarea actualizada", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@editTodoActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@editTodoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        eliminarButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.deleteTodo(todo.id!!)
                    if (response.isSuccessful) {
                        Toast.makeText(this@editTodoActivity, "Tarea eliminada", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@editTodoActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@editTodoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun getUserName(callback: (String?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return callback(null)
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid).child("username")

        ref.get().addOnSuccessListener { snapshot ->
            val name = snapshot.getValue(String::class.java)
            callback(name)
        }.addOnFailureListener {
            callback(null)
        }
    }
}