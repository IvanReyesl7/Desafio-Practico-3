package com.example.desafiopractico3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import retrofit2.Call
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class createTodoActivity : AppCompatActivity() {

    private lateinit var tituloEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var btnGuardar: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_todo)

        tituloEditText = findViewById(R.id.editTextTitulo)
        descripcionEditText = findViewById(R.id.editTextDescripcion)
        btnGuardar = findViewById(R.id.btnGuardar)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val btnRegresar = findViewById<Button>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        btnGuardar.setOnClickListener {
            val titulo = tituloEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()

            if (titulo.isBlank() || descripcion.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid
            if (userId != null) {
                obtenerNombreDeUsuario(userId) { nombre ->
                    guardarTodo(titulo, descripcion, nombre)
                }
            } else {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerNombreDeUsuario(userId: String, callback: (String) -> Unit) {
        database.child("Usuarios").child(userId).child("username")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = snapshot.getValue(String::class.java) ?: "Desconocido"
                    callback(nombre)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@createTodoActivity, "Error al obtener el nombre", Toast.LENGTH_SHORT).show()
                    callback("Desconocido")
                }
            })
    }

    private fun guardarTodo(titulo: String, descripcion: String, creadoPor: String) {

        val fechaActual = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())



        val todos = Todo(
            title = titulo,
            description = descripcion,
            done = false,
            createdAt = fechaActual,
            createdBy = creadoPor
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.createTodo(todos)
                if (response.isSuccessful) {
                    Toast.makeText(this@createTodoActivity, "Tarea guardada", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@createTodoActivity, "Error al guardar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@createTodoActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}