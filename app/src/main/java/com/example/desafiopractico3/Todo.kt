package com.example.desafiopractico3

import java.util.*

import java.io.Serializable
import java.text.SimpleDateFormat


data class Todo(
    val id: String? = null,
    val title: String,
    val description: String,
    val done: Boolean = false,
    val createdAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    val createdBy: String
) : Serializable


