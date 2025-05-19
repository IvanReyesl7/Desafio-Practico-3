package com.example.desafiopractico3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.desafiopractico3.datos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var  auth: FirebaseAuth

    private lateinit var btnRegister: Button
    private lateinit var textViewLogin: TextView

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener{
            val email = findViewById<EditText>(R.id.txtEmail).text.toString()
            val password = findViewById<EditText>(R.id.txtPass).text.toString()
            val username = findViewById<EditText>(R.id.txtUsername).text.toString()
            this.register(email,password,username)
        }

        textViewLogin = findViewById(R.id.textViewLogin)
        textViewLogin.setOnClickListener{
            this.goToLogin()
        }

        this.checkUser()

    }

    private fun register( email: String, password: String, username: String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener{ task ->

                val usuario =task.user
                val uid = usuario?.uid ?: ""

                val Usuarios = hashMapOf(
                    "email" to email,
                    "username" to username
                )

                Firebase.database.reference.child("Usuarios").child(uid)
                    .setValue(Usuarios)
                    .addOnSuccessListener {
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            }.addOnFailureListener{ exception ->
                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()

            }

    }

    private fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun checkUser() {

        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null) {


                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}