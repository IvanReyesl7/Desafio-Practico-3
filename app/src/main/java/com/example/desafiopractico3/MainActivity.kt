package com.example.desafiopractico3

import TodoAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: TodoAdapter
    private lateinit var fabAgregar: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
            return
        }

        setupRecyclerView()
        setupFab()
        setupLogoutButton()
        loadTasks()
    }

    private fun setupRecyclerView() {
        adapter = TodoAdapter().apply {
            setOnItemClickListener { todo ->
                val intent = Intent(this@MainActivity, editTodoActivity::class.java)
                intent.putExtra("todo", todo)
                startActivity(intent)
            }
        }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupFab() {
        fabAgregar = findViewById<FloatingActionButton>(R.id.fab_agregar).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity, createTodoActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupLogoutButton() {
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getToDos()

                if (response.isSuccessful) {
                    val todos = response.body().orEmpty()
                    adapter.updateList(todos)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error al cargar tareas: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

